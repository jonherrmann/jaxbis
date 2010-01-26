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

package com.googlecode.jaxbis.experiment;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.JAXB;
import javax.xml.transform.dom.DOMResult;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.googlecode.jaxbis.analyse.Employee;
import com.googlecode.jaxbis.test.DomDumper;

public class DoFinalRSATest {
    
    static {
        org.apache.xml.security.Init.init();
    }
    
    @Test public void testDoFinalSymDES() throws Exception {
        final Employee employee = new Employee();
        employee.setId(4711);
        
        final DOMResult domResult = new DOMResult();
        
        JAXB.marshal(employee, domResult);
        final Document document = (Document) domResult.getNode();
        DomDumper.dumpDom(document);
        
        
        final SecretKey secretKey = generateDataEncryptionKey();
        
        final Document encDoc = encrypt(
                document, secretKey, XMLCipher.TRIPLEDES_KeyWrap);
        
        DomDumper.dumpDom(encDoc);
        
        final Document decDoc = decrypt(secretKey, encDoc);
        DomDumper.dumpDom(decDoc);
        
    }
    
    @Test public void testDoFinalAsymRSA() throws Exception {
        final Employee employee = new Employee();
        employee.setId(815);
        final DOMResult domResult = new DOMResult();
        
        JAXB.marshal(employee, domResult);
        final Document document = (Document) domResult.getNode();
        DomDumper.dumpDom(document);
        
        
        final KeyPair keyPair = generateRSAKeyPair();
        
        final Document encDoc = encrypt(
                document, keyPair.getPublic(), XMLCipher.RSA_v1dot5);
        
        DomDumper.dumpDom(encDoc);
        
        final Document decDoc = decrypt(keyPair.getPrivate(), encDoc);
        DomDumper.dumpDom(decDoc);
        
    }

    private Document encrypt(
            final Document document, 
            final Key key,
            final String algorithm) throws Exception {
        final XMLCipher keyCipher = XMLCipher.getInstance(algorithm);
        keyCipher.init(XMLCipher.WRAP_MODE, key);
        
        final SecretKey sessionKey = generateKey();
        final EncryptedKey encryptedKey = keyCipher.encryptKey(
                document, sessionKey);
        final XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);
        xmlCipher.init(XMLCipher.ENCRYPT_MODE, sessionKey);
        
        
        final EncryptedData encryptedData = xmlCipher.getEncryptedData();
        
        final KeyInfo keyInfo = new KeyInfo(document);
        keyInfo.add(encryptedKey);
        encryptedData.setKeyInfo(keyInfo);
        final Document encDoc = xmlCipher.doFinal(
                document, (Element) document.getFirstChild(), false);
        return encDoc;
    }

    private Document decrypt(final Key key, final Document encDoc)
            throws Exception {
        final XMLCipher xmlCipher = XMLCipher.getInstance();
        xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
        xmlCipher.setKEK(key);
        final Document decDoc = xmlCipher.doFinal(
                encDoc, (Element) encDoc.getFirstChild());
        return decDoc;
    }

    private KeyPair generateRSAKeyPair() throws Exception {
        final KeyPairGenerator generator =
            KeyPairGenerator.getInstance("RSA");
        generator.initialize(512);
        final KeyPair keyPair = generator.generateKeyPair();
        return keyPair;
    }

    private SecretKey generateKey() throws NoSuchAlgorithmException {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        keyGenerator.init(128);
        final SecretKey symetricKey = keyGenerator.generateKey();
        return symetricKey;
    }
    
    private static SecretKey generateDataEncryptionKey() throws Exception {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
        return keyGenerator.generateKey();
    }

}
