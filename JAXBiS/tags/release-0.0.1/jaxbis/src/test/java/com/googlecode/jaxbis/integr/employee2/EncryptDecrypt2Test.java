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

import static com.googlecode.jaxbis.test.DateFactory.createDate;
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

import org.junit.Test;

import com.googlecode.jaxbis.crypt.KeyRing;
import com.googlecode.jaxbis.example2.Employee2;
import com.googlecode.jaxbis.example2.Employee2ExampleFactory;
import com.googlecode.jaxbis.example2.SecuredEmployee2;
import com.googlecode.jaxbis.example2.SecuredEmployee2Builder;
import com.googlecode.jaxbis.example2.SecuredPosition2;

@SuppressWarnings("unused")
public final class EncryptDecrypt2Test {

    @SuppressWarnings("unchecked")
    @Test 
    public void testEncryptDecrypt2() throws Exception {
        final StringWriter w = new StringWriter();
        final KeyRing keyRing = generateKeyRing();
        final Employee2 employee1 
            = Employee2ExampleFactory.createExampleWithBoss();
        
        final SecuredEmployee2Builder builder = new SecuredEmployee2Builder(
                keyRing);
        final SecuredEmployee2 securedEmployee1 = builder.create(employee1);
        assertNotNull(securedEmployee1);
        
        dump(securedEmployee1);
        
        final JAXBContext pContext = 
            JAXBContext.newInstance(SecuredEmployee2.class);
        final Marshaller marshaller = pContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        marshaller.setSchema(null);
        marshaller.marshal(new JAXBElement(new QName("", "rootTag"), 
                SecuredPosition2.class, securedEmployee1), w);
        
        final SecuredEmployee2 securedEmployee1new 
            = JAXB.unmarshal(
                    new StringReader(w.toString()), SecuredEmployee2.class);
        
        final Employee2 employee1new = securedEmployee1new.getPlainInstance(
                keyRing);
        checkDecrypted(employee1new);
        compareXml(employee1, employee1new);
    }

    private void dump(final Object o) {
        final StringWriter writer = new StringWriter();
        JAXB.marshal(o, writer);
        System.out.println(writer.toString());
    }

    private void compareXml(
            final Employee2 employee1, final Employee2 employee1new) {
        final StringWriter w1 = new StringWriter();
        final StringWriter w2 = new StringWriter();
        JAXB.marshal(employee1, w1);
        JAXB.marshal(employee1new, w2);
        assertEquals(w1.toString(), w2.toString());
    }

    private void checkDecrypted(final Employee2 employee1new) {
        assertNotNull(employee1new);
        
        assertEquals("Test", employee1new.getSurname());
        assertEquals(createDate("2003-11-10"), employee1new.getDateOfHire());
        assertEquals(20000, employee1new.getSalary().getAmount().intValue());
        assertEquals("Bonn", employee1new.getLivesAt().getCity());
        
        assertEquals(2, employee1new.getPositions().size());
        assertEquals("caretaker", 
                employee1new.getPositions().get(0).getTitle());   
        assertEquals(10000,
                employee1new.getPositions().get(0).getSalaryRange().getFrom().
                getAmount().intValue());
        assertEquals(2, employee1new.getPhoneNumbers().size());
        assertEquals("+12-34", employee1new.getPhoneNumbers().get(0));
        
        
        assertEquals(2, employee1new.getFormalyLivedAt().size());
        assertEquals("Köln", 
                employee1new.getFormalyLivedAt().get(0).getCity());
        
        assertEquals("zu Test",  employee1new.getBoss().getSurname());
        assertEquals("The Boss", 
                employee1new.getBoss().getPositions().get(0).getTitle());  
        assertEquals(2, employee1new.getBoss().getPhoneNumbers().size());
        assertEquals("+34-12", employee1new.getBoss().getPhoneNumbers().get(0));
    }

    private static KeyRing generateKeyRing() throws Exception {
        try {
            KeyPairGenerator generator =
                KeyPairGenerator.getInstance("RSA");
            generator.initialize(512);
            final KeyPair encKP = generator.generateKeyPair();
            
            generator = KeyPairGenerator.getInstance("DSA");
            generator.initialize(1024);
            final KeyPair sigKP = generator.generateKeyPair();
            
            return new KeyRing(encKP, sigKP);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    
}
