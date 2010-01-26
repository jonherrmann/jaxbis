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



package com.googlecode.jaxbis.example2;
import com.googlecode.jaxbis.crypt.KeyRing;

import com.googlecode.jaxbis.api.SecuredObjectBuilder;


/*
 * JAXBIS AUTOGENERATED CLASS
 */

public class SecuredSalary2Builder
    implements SecuredObjectBuilder<Salary2, SecuredSalary2> {
 
    public SecuredSalary2Builder(final KeyRing keyRing) {
    }
    public SecuredSalary2 createOrNull(
        final Salary2 unsecuredObject) {
        if (unsecuredObject != null) {
            return create(unsecuredObject);
        } else {
            return null;
        }
    }
    
    public SecuredSalary2 create(
        final Salary2 unsecuredObject) {
   
        final SecuredSalary2 securedObject = new SecuredSalary2();
       
        securedObject.setAmount(
            unsecuredObject.getAmount());
        securedObject.setCurrency(
            unsecuredObject.getCurrency());
       

       



        
        return securedObject;
    }
}