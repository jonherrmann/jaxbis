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

package com.googlecode.jaxbis.integr.employee;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXB;

import org.junit.Test;

import com.googlecode.jaxbis.crypt.KeyRing;
import static org.junit.Assert.*;

public class EncryptDecryptTest {

    @Test public void testEncryptDecrypt() throws Exception {
        final KeyRing secretKey = generateKeyRing();
        
        final Employee employee = new Employee();
        employee.setId(4711);
        employee.setName("Kalle");
        employee.setDepartment("buying");
        employee.setDateOfBirth(DateFactory.createDate("1965-11-11"));
        employee.setDateOfHire(DateFactory.createDate("2004-01-01"));
        
        final SecuredEmployeeBuilder builder = new SecuredEmployeeBuilder(
                secretKey);
        final SecuredEmployee securedEmployee = builder.create(employee);
        assertNotNull(securedEmployee);
        JAXB.marshal(securedEmployee, System.out);
        
        final Employee employee2 = securedEmployee.getPlainInstance(secretKey);
        assertNotNull(employee2);
        JAXB.marshal(employee2, System.out);
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
