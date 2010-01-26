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
import java.io.StringWriter;
import java.math.BigDecimal;

import javax.xml.bind.JAXB;

public final class Shop {

    private static final String ACCOUNT_NUMBER = "123456";

    private Shop() {
    }
    
    public static void processOrder(final String securedOrderXml) {
        System.out.println("********** Shop received XML order: **********");
        System.out.println(securedOrderXml);
        System.out.println();
        
        // Deserialize Order
        final SecuredOrder securedOrder = JAXB.unmarshal(
                new StringReader(securedOrderXml), SecuredOrder.class);
        System.out.println("Shop: Processing order of " 
                + securedOrder.getCustomerName() + ".");
        System.out.println();
        
        // Create SecuredTransaction with SecuredCreditCardNo
        final SecuredTransaction securedTransaction = new SecuredTransaction();
        securedTransaction.setAccountNumber(ACCOUNT_NUMBER);
        securedTransaction.setAmount(new BigDecimal("1.50"));
        securedTransaction.setSecuredCreditCardNo(
                securedOrder.getSecuredCreditCardNo());
        
        // Serialize SecuredTransaction
        final StringWriter stringWriter = new StringWriter();
        JAXB.marshal(securedTransaction, stringWriter);
        
        if (CreditCardCompany.processTransaktion(stringWriter.toString())) {
            System.out.println("Shop: Transaction successful. Shipping " 
                    + securedOrder.getGoods() + " to " 
                    + securedOrder.getAddress() + ".");
        } else {
            System.out.println("Shop: Transaction failed.");
        }
    }

}
