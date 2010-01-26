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

package com.googlecode.jaxbis.test;

import java.security.Key;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.googlecode.jaxbis.crypt.SecuredMemberConfig;
import com.googlecode.jaxbis.crypt.SecuredMemberConfig.Configuration;

public final class XPathDecrypter {
    
    private Key secretKey;
    
    private final XMLCipher xmlCipher;

    public XPathDecrypter(final Key secretKey) {
        this.secretKey = secretKey;
        try {
            xmlCipher = XMLCipher.getInstance();
        } catch (final XMLEncryptionException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Document decrypt(final Document root, final String xPath) {
        final Configuration config 
            = SecuredMemberConfig.getInstance().getConfiguration();
        try {
            xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
            final Element ns = root.createElement("Context");
            ns.setAttribute("xmlns:jaxbis", "http://code.google.com/p/jaxbis/");
            
            final Element securedMemberElement = 
                (Element) XPathAPI.selectSingleNode(
                    root, xPath, ns);
           
            // Find encryptedDataElement
            final Element encryptedDataElement = (Element) 
                securedMemberElement.getElementsByTagNameNS(
                    config.getNameSpace(),
                    "securedObject").item(0).getFirstChild();
            // Get Encrypted data 
            final EncryptedData encryptedData = 
                xmlCipher.loadEncryptedData(root, encryptedDataElement);
            // Get Algorithm
            final String encAlgo = 
                encryptedData.getEncryptionMethod().getAlgorithm();
                            
            // Get encrypted element and load key
            final Element encryptedKeyElement = 
                (Element) securedMemberElement.getElementsByTagNameNS(
                    config.getNameSpace(),
                    "key").item(0).getFirstChild();
            final EncryptedKey encryptedKey = 
                xmlCipher.loadEncryptedKey(encryptedKeyElement);
            
            
            // Decrypt sessionKey
            xmlCipher.init(XMLCipher.DECRYPT_MODE, secretKey);
            final Key sessionKey = 
                xmlCipher.decryptKey(encryptedKey, encAlgo);
            
            // Set key for encryption
            xmlCipher.init(XMLCipher.DECRYPT_MODE, sessionKey);
//            xmlCipher.setKEK(sessionKey);
                          
            // Decrypt it
            xmlCipher.doFinal(
                    root,
                    encryptedDataElement);
            return root;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
