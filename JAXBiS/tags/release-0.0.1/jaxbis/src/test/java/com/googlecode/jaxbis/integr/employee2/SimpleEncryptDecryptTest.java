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

package com.googlecode.jaxbis.integr.employee2;

import static com.googlecode.jaxbis.test.XPathAssert.*;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;

import org.junit.Test;

import com.googlecode.jaxbis.crypt.KeyRing;
import com.googlecode.jaxbis.example.Position1;
import com.googlecode.jaxbis.example.Salary1;
import com.googlecode.jaxbis.example.SalaryRange1;
import com.googlecode.jaxbis.example.SecuredPosition1;
import com.googlecode.jaxbis.example.SecuredPosition1Builder;

@SuppressWarnings("unused")
public final class SimpleEncryptDecryptTest {

    @SuppressWarnings("unchecked")
    @Test 
    public void testEncryptDecrypt1() throws Exception {
        // final StringWriter w = new StringWriter();
        final DOMResult resultDOM = new DOMResult();
        final KeyRing keyRing = generateKeyRing();
    
        final Position1 position1 
            = new Position1("The Boss", new SalaryRange1(
                    new Salary1(100000, "USD"), 
                    new Salary1(1000000, "USD")));
        
        final SecuredPosition1Builder builder = new SecuredPosition1Builder(
                keyRing);
        final SecuredPosition1 securedposition1 = builder.create(position1);
        
        final Position1 position1test = securedposition1.getPlainInstance(
                keyRing);
        
        assertNotNull(securedposition1);
        
        
        final StringWriter w1 = new StringWriter();
        final JAXBContext pContext = 
            JAXBContext.newInstance(SecuredPosition1.class);
        final Marshaller marshaller = 
            pContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        marshaller.setSchema(null);
        marshaller.marshal(new JAXBElement(new QName("", "rootTag"), 
                SecuredPosition1.class, securedposition1), w1);
        
        final SecuredPosition1 securedPosition1new 
            = JAXB.unmarshal(new StringReader(w1.toString()), 
                    SecuredPosition1.class);
        
        final Position1 position1new = securedPosition1new.getPlainInstance(
                keyRing);
        checkDecrypted(position1new);
        compareXml(position1, position1new);
    }

    private void compareXml(
            final Position1 position1, final Position1 position1new) {
        final StringWriter w1 = new StringWriter();
        final StringWriter w2 = new StringWriter();
        JAXB.marshal(position1, w1);
        JAXB.marshal(position1new, w2);
        assertEquals(w1.toString(), w2.toString());
    }

    private void checkDecrypted(final Position1 position1new) {
        assertNotNull(position1new);
        
        assertEquals("The Boss", position1new.getTitle());
        assertEquals(100000, position1new.getSalaryRange().getFrom().
                getAmount().intValue());
    }
        private static KeyRing generateKeyRing() throws Exception {
        try {
            KeyPairGenerator generator =
                KeyPairGenerator.getInstance("RSA");
            generator.initialize(512);
            final KeyPair encKP = generator.generateKeyPair();
            
            generator = KeyPairGenerator.getInstance("DSA");
            generator.initialize(512);
            final KeyPair sigKP = generator.generateKeyPair();
            
            return new KeyRing(encKP, sigKP);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    
}
