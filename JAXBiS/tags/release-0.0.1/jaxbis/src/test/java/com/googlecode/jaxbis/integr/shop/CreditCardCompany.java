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

package com.googlecode.jaxbis.integr.shop;

import java.io.StringReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.JAXB;

import com.googlecode.jaxbis.crypt.KeyRing;

public final class CreditCardCompany {
    
    private static final KeyRing KEY_STORE  = generateDataEncryptionKey();   
    
    private CreditCardCompany() {
        
    }
    
    public static KeyRing getKeyForCustomer(final String c) {
        return KEY_STORE;
    }

    private static KeyRing generateDataEncryptionKey() {
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

    public static boolean processTransaktion(
            final String securedTransactionXml) {
        System.out.println("** CreditCardCompany received XML transaktion: **");
        System.out.println(securedTransactionXml);
        System.out.println();
        
        final SecuredTransaction securedTransaction = JAXB.unmarshal(
                new StringReader(securedTransactionXml), 
                SecuredTransaction.class);
        
        // Decrypt
        final Transaction transaction 
            = securedTransaction.getPlainInstance(KEY_STORE);
        System.out.println("CreditCardCompany: Transferring " 
                + transaction.getAmount() 
                + " from credit card " + transaction.getCreditCardNo()
                + " to account " + transaction.getAccountNumber());
        
        return true;
    }
}
