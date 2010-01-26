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

package com.googlecode.jaxbis.example;

import java.util.Date;
import java.util.List;

import com.googlecode.jaxbis.api.Encrypt;

public class Person1 {
    
    private String givenName;
    private String middleName;
    private String surname;
    private String salutation;
    private Date dateOfBirth;
    
    private Address1 livesAt;
    
    private List<Address1> formalyLivedAt;
    
    private List<String> emailAddresses;
    private List<String> phoneNumbers;
    
    public Person1() {
    }
    
    public Person1(
            final String givenName, final String middleName, 
            final String surname, final String salutation, 
            final Date dateOfBirth, final Address1 livesAt,
            final List<Address1> formalyLivedAt,
            final List<String> emailAddresses,
            final List<String> phoneNumbers) {
        super();
        this.givenName = givenName;
        this.middleName = middleName;
        this.surname = surname;
        this.salutation = salutation;
        this.dateOfBirth = dateOfBirth;
        this.livesAt = livesAt;
        this.formalyLivedAt = formalyLivedAt;
        this.emailAddresses = emailAddresses;
        this.phoneNumbers = phoneNumbers;
    }

    public String getGivenName() {
        return givenName;
    }
    
    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }
    
    public String getSurname() {
        return surname;
    }
    
    public void setSurname(final String surname) {
        this.surname = surname;
    }
    
    public String getSalutation() {
        return salutation;
    }
    
    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }
    
    @Encrypt
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(final Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Encrypt
    public Address1 getLivesAt() {
        return livesAt;
    }

    public void setLivesAt(final Address1 livesAt) {
        this.livesAt = livesAt;
    }
    
    @Encrypt
    public List<Address1> getFormalyLivedAt() {
        return formalyLivedAt;
    }

    public void setFormalyLivedAt(final List<Address1> formalyLivedAt) {
        this.formalyLivedAt = formalyLivedAt;
    }

    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(final List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    @Encrypt
    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(final List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
    
    
    
    

}
