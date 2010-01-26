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

import java.security.Key;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.JAXB;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.transform.dom.DOMSource;

import org.apache.log4j.Priority;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;

import com.googlecode.jaxbis.util.DomUtil;
import com.googlecode.jaxbis.util.Logger;
import com.googlecode.jaxbis.api.SecuredMember;
import com.googlecode.jaxbis.api.SecuredObject;
import com.googlecode.jaxbis.api.SecurityType;
import com.googlecode.jaxbis.crypt.SecuredMemberConfig.Configuration;

// Class SecuredMemberDecrypter
// TODO Exception handling

public class PlainInstanceBuilder {
        
    private KeyRing keyRing;
        
    private final XMLCipher xmlCipher;
    
    private XMLSignatureFactory xmlSigFactory;
    
    private Configuration config;
       
    // Creates the PlainInstanceBuilder
    public PlainInstanceBuilder(final KeyRing keyRing) {
        org.apache.xml.security.Init.init();
        
        config = SecuredMemberConfig.getInstance().getConfiguration();
        
        this.keyRing = keyRing;
        try {
            xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(XMLCipher.DECRYPT_MODE, null);

            xmlSigFactory = XMLSignatureFactory.getInstance("DOM");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public <U, S extends SecuredObject<U>> U 
        createPlainInstanceRecursive(
            final SecuredMember securedMember, 
            final Class< S > clasz,
            final SecurityType securityType) {
        if (securedMember != null) {
            final SecuredObject<U> securedObject = createPlainInstance(
                    securedMember, clasz, 
                    securityType);
            return securedObject.getPlainInstance(keyRing);
        } else {
            return null;
        }
    }
    
    public final <T> T createPlainInstance(
            final SecuredMember securedMember, 
            final Class< T > clasz, final SecurityType securityType) {
        final Node plainInstanceNode 
            = createPlainInstance(
                    securedMember.getSecuredMember(),
                    securityType);
        final DOMSource resultDom = new DOMSource(plainInstanceNode);
        
        // Print debug stuff
        Logger.log(this, Priority.DEBUG_INT, "createPlainInstance output:",
                resultDom.getNode());
        
        return (T) JAXB.unmarshal(resultDom, clasz);
    }
    
    public final Node createPlainInstance(
            final Element securedMemberElement, 
            final SecurityType securityType) {
        try {
            
            // Store all in a tmp doc
            final Document plainInstanceDoc = 
                DomUtil.createDocAndAppend(securedMemberElement);
            
            // Print debug stuff
            Logger.log(this, Priority.DEBUG_INT, "createPlainInstance input:",
                    plainInstanceDoc);
            Logger.log(this, Priority.DEBUG_INT, 
                    "createPlainInstance securityType:\n"
                    + securityType);
            
            // Get securedObjectElement
            final Element securedObjectElement = 
            (Element) plainInstanceDoc.getElementsByTagNameNS(
                    config.getNameSpace(), "securedObject").item(0);
            // Check existence
            if (securedObjectElement == null) {
                
                Logger.log(this, Priority.DEBUG_INT, 
                        "securedObject Element not found:",
                        plainInstanceDoc);
                throw new Exception("securedObject Element not found!");
            }
            
            // So what to do?
            
            // Check SIGNATURE of plain element
            if (securityType == SecurityType.SIGNATURE
                     || securityType
                         == SecurityType.ENCRYPTION_BEFORE_SIGNATURE) {
                 this.verifySignature(plainInstanceDoc, securedObjectElement);
            }
            
            // DECRYPT securedObject
            if (securityType != SecurityType.SIGNATURE) {
                this.decrypt(plainInstanceDoc);
            }
            
            // Check SIGNATURE of encrypted element
            if (securityType == SecurityType.SIGNATURE_BEFORE_ENCRYPTION) {
                // Reset correct namespace for signature
                plainInstanceDoc.renameNode(
                        securedObjectElement, 
                        config.getNameSpace(),
                        config.getPrefix() + ":securedObject");
                plainInstanceDoc.normalizeDocument();
                
                this.verifySignature(plainInstanceDoc, securedObjectElement);
            }
            return securedObjectElement.getFirstChild();

        } catch (final XMLEncryptionException e) {
            Logger.log(this, Priority.DEBUG_INT, "Unable to create plain "
                    + "instance of securedMember:",
                    securedMemberElement);
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /*
     * VERIFY SIGNATURE
     */
    private void verifySignature(final Document tmpDoc, 
            final Element securedObjectElement) throws Exception {

        // First normalize our doc
        tmpDoc.normalize();
        
        // Find signatureElement
        final Element signatureElement = 
            (Element) tmpDoc.getElementsByTagNameNS(
                    config.getNameSpace(),
                    "signature").item(0).getChildNodes().item(0);
       
        // Check existence
        if (signatureElement == null) {
            Logger.log(this, Priority.DEBUG_INT, 
                    "Signature element not found in:",
                    tmpDoc);
            throw new Exception("Signature element not found!");
        }
        
        // Create validation context with our public key
        final DOMValidateContext valContext = 
            new DOMValidateContext(
                    this.keyRing.getSignatureKeyPair().getPublic(),
                    signatureElement);
        
        // Unmarshal signature and validate it
        final XMLSignature signature = 
            xmlSigFactory.unmarshalXMLSignature(valContext);        
        final boolean valid = signature.validate(valContext); 
        if (!valid) {
            Logger.log(this, Priority.DEBUG_INT, 
                    "Validation process failed. "
                    + "Signature is not valid for SecuredMember:",
                    tmpDoc);
            
            throw new Exception("Invalid signature!");
        } else {
            Logger.log(this, Priority.DEBUG_INT, "Signature is valid!");
        }
    }
    
    /*
     * DECRYPT
     */
    private void decrypt(final Document tmpDoc) throws Exception {
        
        // Find encryptedDataElement
        final Element encryptedDataElement = (Element) 
            tmpDoc.getElementsByTagNameNS(
                config.getNameSpace(),
                "securedObject").item(0).getFirstChild();
        
        // Check existence
        if (encryptedDataElement == null) {
            Logger.log(this, Priority.DEBUG_INT, 
                    "EncryptedData element not found!", tmpDoc);
            throw new Exception("EncryptedData element not found!");
        }
        
        // Get Encrypted data 
        final EncryptedData encryptedData = 
            xmlCipher.loadEncryptedData(tmpDoc, encryptedDataElement);
        
        // Get Algorithm
        final String encAlgo = 
            encryptedData.getEncryptionMethod().getAlgorithm();
                        
        // Get encrypted element and load key
        final Element encryptedKeyElement = 
            (Element) tmpDoc.getElementsByTagNameNS(
                config.getNameSpace(),
                "key").item(0).getFirstChild();
        final EncryptedKey encryptedKey = 
            xmlCipher.loadEncryptedKey(encryptedKeyElement);
        
        // Decrypt sessionKey
        xmlCipher.init(XMLCipher.DECRYPT_MODE,
                this.keyRing.getEncryptionKeyPair().getPrivate());
        final Key sessionKey = 
            xmlCipher.decryptKey(encryptedKey, encAlgo);
        
        // Set key for encryption
        xmlCipher.init(XMLCipher.DECRYPT_MODE, sessionKey);
        xmlCipher.setKEK(sessionKey);
                      
        // Namespace workaround
        // Now remove our namespace from securedObject.
        // The decryptedObject will inherit this namespace.
        // This is important for correct JAXB unmarshalling
        // and signature verifying!!!!
        tmpDoc.renameNode(
                encryptedDataElement.getParentNode(), 
                "", "securedObject");
        tmpDoc.normalizeDocument();
        
        // Decrypt it
        xmlCipher.doFinal(
                tmpDoc,
                encryptedDataElement);
    }
    
}
