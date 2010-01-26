/**
 *
 * Copyright (c) 2010, Joachim Draeger, Jon Herrmann, Simon Müller
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     * Neither the name of JAXBiS nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.googlecode.jaxbis.crypt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.dom.DOMResult;

import org.apache.log4j.Priority;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.googlecode.jaxbis.util.DomUtil;
import com.googlecode.jaxbis.util.Logger;
import com.googlecode.jaxbis.api.SecuredMember;
import com.googlecode.jaxbis.api.SecurityType;
import com.googlecode.jaxbis.crypt.SecuredMemberConfig.Configuration;

// Class SecuredMemberBuilder
// TODO Exception handling

public final class SecuredMemberBuilder {
    
private KeyRing keyRing;
    
    private XMLSignatureFactory xmlSigFactory;
    
    private KeyGenerator keyGenerator;
    
    private XMLCipher keyCipher;
    
    private XMLCipher dataCipher;
    
    private DigestMethod digestMethod;
    
    private Transform transform;
    
    private List<Transform> transformList;
    
    private CanonicalizationMethod canonMethod;
    
    private SignatureMethod  signMethod;
    
    private javax.xml.crypto.dsig.keyinfo.KeyInfo signatureKeyInfo;
    
    private Configuration config;
    
    // Creates the SecureMemberBuilder
    public SecuredMemberBuilder(final KeyRing keyRing)  {
        
        config = SecuredMemberConfig.getInstance().getConfiguration();
             
        this.keyRing = keyRing;
        
        try {
            // Init constant objects
            keyCipher = XMLCipher.getInstance(config.getAsymKeyType());
            dataCipher = XMLCipher.getInstance(config.getSymKeyType());
            keyGenerator = config.getSymKeyGen();
            
            if (keyRing.getSignatureKeyPair() != null) {
                xmlSigFactory = XMLSignatureFactory.getInstance("DOM");
                
                digestMethod = xmlSigFactory.newDigestMethod(
                        DigestMethod.SHA1, null);
                transform = xmlSigFactory.newTransform(
                        CanonicalizationMethod.EXCLUSIVE,
                        (TransformParameterSpec) null);
                transformList = Collections.singletonList(transform);
                
                canonMethod = xmlSigFactory.newCanonicalizationMethod(
                        CanonicalizationMethod.EXCLUSIVE,
                        (C14NMethodParameterSpec) null);
                
                signMethod = 
                    xmlSigFactory.newSignatureMethod(
                            SignatureMethod.DSA_SHA1, null);
                
                final KeyInfoFactory keyInfoFactory = 
                    xmlSigFactory.getKeyInfoFactory();
                final KeyValue keyValue = 
                    keyInfoFactory.newKeyValue(
                            keyRing.getSignatureKeyPair().getPublic());
                signatureKeyInfo = 
                    keyInfoFactory.newKeyInfo(
                            Collections.singletonList(keyValue));
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
   

    public SecuredMember createSecuredMemberOrNull(final Object unsecuredObject,
            final String memberName, final SecurityType securityType) {
        if (unsecuredObject != null) {
            return createSecuredMember(unsecuredObject, memberName,
                    securityType);
        } else {
            return null;
        }
    }
    
    public SecuredMember createSecuredMemberFromCollection(
            final Collection< ? > securedCollection,
            final Class< ? > securedObjectClass,
            final String memberName,
            final SecurityType securityType) {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(
                    securedObjectClass, CollectionWrapper.class);            
            final DOMResult resultDOM = new DOMResult();
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.FALSE);
            marshaller.marshal(
                    new CollectionWrapper(securedCollection), resultDOM); 
            return createSecuredMember(
                    (Document) resultDOM.getNode(), memberName,
                    securityType);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public SecuredMember createSecuredMember(final Object unsecuredObject,
            final String memberName, final SecurityType securityType) {
        try {
            // Marshal object and save result as String
            final DOMResult resultDOM = new DOMResult();                        
            JAXB.marshal(unsecuredObject, resultDOM);
            return createSecuredMember(
                    (Document) resultDOM.getNode(), memberName,
                    securityType);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public SecuredMember createSecuredMember(final Document objDoc,
            final String memberName, final SecurityType securityType)
        throws Exception {
    
        // Reset memberName
        objDoc.renameNode(
                objDoc.getFirstChild(), "", memberName);
        
        // Print debug stuff
        Logger.log(this, Priority.DEBUG_INT, "createSecuredMember input:",
                objDoc);
        Logger.log(this, Priority.DEBUG_INT, 
                "createSecuredMember securityType:\n"
                + securityType);
        
            
        // Create document structure
        final Document securedMemberDoc = DomUtil.createDocument();      
        
        // SecuredMember element
        final Element securedMemberElement = 
            securedMemberDoc.createElementNS(config.getNameSpace(),
                   config.getPrefix() + ":securedMember");
        securedMemberElement.setAttributeNS(Constants.NamespaceSpecNS, 
                "xmlns", config.getNameSpace());
        securedMemberElement.setAttributeNS(Constants.NamespaceSpecNS, 
                "xmlns:" + config.getPrefix(), config.getNameSpace());
        
        // key element
        final Element keyElement =
            securedMemberDoc.createElementNS(config.getNameSpace(), 
                    config.getPrefix() + ":key");
        securedMemberDoc.appendChild(securedMemberElement);
        
        final Element signatureElement =
            securedMemberDoc.createElementNS(config.getNameSpace(), 
                    config.getPrefix() + ":signature");
        
        // securedObject element
        final String signatureID = UUID.randomUUID().toString();
        final Attr secObjAttrib = 
            securedMemberDoc.createAttributeNS(null, "id");
        secObjAttrib.setValue(signatureID);
        final Element securedObjectElement =
            securedMemberDoc.createElementNS(config.getNameSpace(), 
                    config.getPrefix() + ":securedObject");
        
        // Do we need a key element
        if (securityType != SecurityType.SIGNATURE) {
            securedMemberElement.appendChild(keyElement);
        }
        // Do we need a signature element
        if (securityType != SecurityType.ENCRYPTION) {
            securedMemberElement.appendChild(signatureElement);
        }
        // Add the securedObject element with an ID
        securedMemberElement.appendChild(securedObjectElement);
        securedObjectElement.getAttributes().setNamedItem(secObjAttrib);
 
        
        // So what to do?
        
        
        // Only sign securedMember
        if (securityType == SecurityType.SIGNATURE) { 
            this.sign(securedMemberDoc, 
                    objDoc, 
                    securedObjectElement, 
                    signatureElement, 
                    signatureID);
        // Sign securedMember before encryption
        } else if (securityType
                == SecurityType.SIGNATURE_BEFORE_ENCRYPTION) {
            this.sign(securedMemberDoc, 
                    objDoc, 
                    securedObjectElement, 
                    signatureElement, 
                    signatureID);
            // Remove element that should be encrypted
            securedObjectElement.removeChild(
                    securedObjectElement.getFirstChild());
        }

        // Encrypt securedMember
        if (securityType != SecurityType.SIGNATURE) {
            this.encrypt(securedMemberDoc,
                    objDoc, 
                    securedObjectElement, 
                    keyElement);
        }
        
        // Sign securedMember afte encryption
        if (securityType == SecurityType.ENCRYPTION_BEFORE_SIGNATURE) {
            this.sign(securedMemberDoc,
                    securedObjectElement, 
                    signatureElement, 
                    signatureID);
        }
        
        // Print debug stuff
        Logger.log(this, Priority.DEBUG_INT, "createSecuredMember output:\n",
                securedMemberDoc);
        
        // Create and return securedMember
        final SecuredMember securedMember = new SecuredMember(
                securedMemberDoc.getDocumentElement());
        return securedMember;
    }
    
    
    
    /*
     * SIGN
     */
    // This method will append our securedObject element
    private void sign(final Document securedMemberDoc, 
            final Document objDoc, 
            final Element securedObjectElement, 
            final Element signatureElement,
            final String signatureID) throws Exception {
        
     // Append the object to the securedObject element
        securedObjectElement.appendChild(
                securedMemberDoc.importNode(
                        objDoc.getFirstChild(), true));
        this.sign(securedMemberDoc,  
                securedObjectElement, 
                signatureElement, 
                signatureID);
    }
    
    private void sign(final Document securedMemberDoc, 
            final Element securedObjectElement, 
            final Element signatureElement,
            final String signatureID) throws Exception {
                
        // Normalize our doc
        securedMemberDoc.normalizeDocument();

        // Create a reference between the signature and the securedObject
        final javax.xml.crypto.dsig.Reference reference = 
            xmlSigFactory.newReference(
                "#" + signatureID, digestMethod, transformList, null, null);
        final List<javax.xml.crypto.dsig.Reference> refList = 
            Collections.singletonList(reference);
                
        // Add info about the use canonicalization and signature method
        final SignedInfo signedInfo = 
            xmlSigFactory.newSignedInfo(canonMethod, signMethod, refList);
        
        // Create a context and sign our securedObject
        final XMLSignature signature = 
            xmlSigFactory.newXMLSignature(signedInfo, signatureKeyInfo);
        final DOMSignContext signContext = new DOMSignContext(
                this.keyRing.getSignatureKeyPair().getPrivate(),
                signatureElement);
        signContext.setDefaultNamespacePrefix("ds");
        signature.sign(signContext);
                
        
        
        // Selftest
        /*
        securedMemberDoc.normalizeDocument();
        final SecuredMember securedMember = new SecuredMember(
                securedMemberDoc.getDocumentElement());
        
         
         final Document tmpDoc = DomUtil.createDocument();
         tmpDoc.appendChild(
                 tmpDoc.importNode(securedMember.getSecuredMember(), true));
         
        final Element signatureElement2 = 
            (Element) tmpDoc.getElementsByTagNameNS(
                    config.getNameSpace(),
               "signature").item(0).getChildNodes().item(0);
        
        final DOMValidateContext valContext = new DOMValidateContext(
                this.keyRing.getSignatureKeyPair().getPublic(),
                signatureElement2);
        
        final XMLSignature signature2 = 
            xmlSigFactory.unmarshalXMLSignature(valContext);
        
        final boolean valid = signature2.validate(valContext);
        Logger.log(this, Priority.DEBUG_INT, "Selftest: "
                + valid);
        
        if (!valid) {
            throw new RuntimeException("Selftest failed!");
        }
        System.out.println("OK");
        DomDumper.dumpDom(tmpDoc);
        */
        
    }
    
    
    /*
     * ENCRYPT
     */
    private void encrypt(final Document securedMemberDoc, 
            final Document objDoc, 
            final Element securedObjectElement, 
            final Element keyElement) throws Exception {
        // Generate sessionKey
        final SecretKey sessionKey = keyGenerator.generateKey();
        
        // Init data cipher with session key
        dataCipher.init(XMLCipher.ENCRYPT_MODE, sessionKey);
        
        // Create EncryptedData and assing an ID
        final String refId = "_" + UUID.randomUUID().toString();
        
        // Encrypt!
        final EncryptedData encryptedData = 
            dataCipher.encryptData(securedMemberDoc, 
                    objDoc.getDocumentElement());
        encryptedData.setId(refId);
        
        // Generate EncryptedData Element and append it
        final Element encryptedDataElement = 
            dataCipher.martial(encryptedData);
        securedObjectElement.appendChild(encryptedDataElement);
        
        // Create encrypted Key with sessionKey
        keyCipher.init(XMLCipher.WRAP_MODE, 
                this.keyRing.getEncryptionKeyPair().getPublic());
        final EncryptedKey encryptedKey =
                keyCipher.encryptKey(securedMemberDoc, sessionKey);
        
        // and add KeyInfo
        final KeyInfo encKeyInfo = new KeyInfo(securedMemberDoc);
        encryptedKey.setKeyInfo(encKeyInfo);
        
        // Create ID reference list
        final org.apache.xml.security.encryption.ReferenceList 
        referenceList = keyCipher.createReferenceList(
                org.apache.xml.security.encryption.
                ReferenceList.DATA_REFERENCE);
            encryptedKey.setReferenceList(referenceList);
        final org.apache.xml.security.encryption.Reference dataReference = 
            referenceList.newDataReference("#" + refId);
        referenceList.add(dataReference);
        
        // Create encryptedKey Element
        final Element encryptedKeyElement = 
            keyCipher.martial(encryptedKey);
        keyElement.appendChild(encryptedKeyElement);
    }
}
