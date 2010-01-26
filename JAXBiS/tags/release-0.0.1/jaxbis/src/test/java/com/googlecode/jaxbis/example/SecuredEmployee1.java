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

import com.googlecode.jaxbis.crypt.KeyRing;

import com.googlecode.jaxbis.api.SecuredMember;
import com.googlecode.jaxbis.api.SecurityType;
import com.googlecode.jaxbis.api.SecuredObject;
import com.googlecode.jaxbis.crypt.PlainInstanceBuilder;
import com.googlecode.jaxbis.crypt.SecuredCollectionBuilder;
import com.googlecode.jaxbis.crypt.CollectionWrapper;

/*
 * JAXBIS AUTOGENERATED CLASS
 */

public class SecuredEmployee1 implements SecuredObject<Employee1> {
    
    private java.lang.String middleName;
    
    public void setMiddleName(
        final java.lang.String middleName) {
        this.middleName = middleName;
    }
    
    public java.lang.String getMiddleName() {
        return middleName;
    }
    
    private java.util.Date dateOfHire;
    
    public void setDateOfHire(
        final java.util.Date dateOfHire) {
        this.dateOfHire = dateOfHire;
    }
    
    public java.util.Date getDateOfHire() {
        return dateOfHire;
    }
    
    private java.lang.String surname;
    
    public void setSurname(
        final java.lang.String surname) {
        this.surname = surname;
    }
    
    public java.lang.String getSurname() {
        return surname;
    }
    
    private java.lang.String givenName;
    
    public void setGivenName(
        final java.lang.String givenName) {
        this.givenName = givenName;
    }
    
    public java.lang.String getGivenName() {
        return givenName;
    }
    
    private java.lang.String salutation;
    
    public void setSalutation(
        final java.lang.String salutation) {
        this.salutation = salutation;
    }
    
    public java.lang.String getSalutation() {
        return salutation;
    }
    
    private java.util.List< 
        java.lang.String> emailAddresses;
    
    public void setEmailAddresses(
        final java.util.List<
            java.lang.String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }
    
    public java.util.List<java.lang.String> getEmailAddresses() {
        return emailAddresses;
    }
    
    private SecuredMember securedDateOfBirth;
    
    public void setSecuredDateOfBirth(
        final SecuredMember securedDateOfBirth) {
        this.securedDateOfBirth = securedDateOfBirth;
    }
    
    public SecuredMember getSecuredDateOfBirth() {
        return securedDateOfBirth;
    }
    private SecuredMember securedPhoneNumbers;
    
    public void setSecuredPhoneNumbers(
        final SecuredMember securedPhoneNumbers) {
        this.securedPhoneNumbers = securedPhoneNumbers;
    }
    
    public SecuredMember getSecuredPhoneNumbers() {
        return securedPhoneNumbers;
    }
    private SecuredMember securedFormalyLivedAt;
    
    public void setSecuredFormalyLivedAt(
        final SecuredMember securedFormalyLivedAt) {
        this.securedFormalyLivedAt = securedFormalyLivedAt;
    }
    
    public SecuredMember getSecuredFormalyLivedAt() {
        return securedFormalyLivedAt;
    }
    private SecuredMember securedBoss;
    
    public void setSecuredBoss(
        final SecuredMember securedBoss) {
        this.securedBoss = securedBoss;
    }
    
    public SecuredMember getSecuredBoss() {
        return securedBoss;
    }
    private SecuredMember securedSalary;
    
    public void setSecuredSalary(
        final SecuredMember securedSalary) {
        this.securedSalary = securedSalary;
    }
    
    public SecuredMember getSecuredSalary() {
        return securedSalary;
    }
    private SecuredMember securedLivesAt;
    
    public void setSecuredLivesAt(
        final SecuredMember securedLivesAt) {
        this.securedLivesAt = securedLivesAt;
    }
    
    public SecuredMember getSecuredLivesAt() {
        return securedLivesAt;
    }
     
    private java.util.Collection<
        com.googlecode.jaxbis.example.SecuredPosition1> positions;
    
    public void setPositions(
        final java.util.Collection<
            com.googlecode.jaxbis.example.SecuredPosition1> positions) {
        this.positions = positions;
    }
    
    public java.util.Collection<
        com.googlecode.jaxbis.example.SecuredPosition1> getPositions() {
        return positions;
    }
    @SuppressWarnings("unchecked")
    @Override
    public Employee1 getPlainInstance(final KeyRing keyRing) {
        
        final Employee1 decrpytedObject = new Employee1();

        final PlainInstanceBuilder plainInstanceBuilder = 
            new PlainInstanceBuilder(keyRing);

        decrpytedObject.setMiddleName(getMiddleName());
        decrpytedObject.setDateOfHire(getDateOfHire());
        decrpytedObject.setSurname(getSurname());
        decrpytedObject.setGivenName(getGivenName());
        decrpytedObject.setSalutation(getSalutation());
        decrpytedObject.setEmailAddresses(getEmailAddresses());

        decrpytedObject.setPositions(
            SecuredCollectionBuilder.createPlain(
                getPositions(), keyRing)
        );                
    
        decrpytedObject.setDateOfBirth(
            plainInstanceBuilder.createPlainInstance(
                getSecuredDateOfBirth(),
                java.util.Date.class,
                SecurityType.ENCRYPTION)
        );

        decrpytedObject.setPhoneNumbers(
            (java.util.List<java.lang.String>)
            plainInstanceBuilder.createPlainInstance(
                getSecuredPhoneNumbers(),
                CollectionWrapper.class,
                SecurityType.ENCRYPTION).getElement()
        );

        decrpytedObject.setBoss(
            plainInstanceBuilder.createPlainInstanceRecursive(
                getSecuredBoss(),
                com.googlecode.jaxbis.example.SecuredEmployee1.class,
                SecurityType.ENCRYPTION)
        );                
        decrpytedObject.setSalary(
            plainInstanceBuilder.createPlainInstanceRecursive(
                getSecuredSalary(),
                com.googlecode.jaxbis.example.SecuredSalary1.class,
                SecurityType.ENCRYPTION)
        );                
        decrpytedObject.setLivesAt(
            plainInstanceBuilder.createPlainInstanceRecursive(
                getSecuredLivesAt(),
                com.googlecode.jaxbis.example.SecuredAddress1.class,
                SecurityType.ENCRYPTION)
        );                

        decrpytedObject.setFormalyLivedAt(
            SecuredCollectionBuilder.createPlainForRef(
            (java.util.Collection<
                com.googlecode.jaxbis.example.SecuredAddress1>)
            plainInstanceBuilder.createPlainInstance(
                getSecuredFormalyLivedAt(),
                CollectionWrapper.class,
                SecurityType.ENCRYPTION).getElement(),
                com.googlecode.jaxbis.example.SecuredAddress1.class,
                keyRing)
        );                
        
        return decrpytedObject;
    }
}
