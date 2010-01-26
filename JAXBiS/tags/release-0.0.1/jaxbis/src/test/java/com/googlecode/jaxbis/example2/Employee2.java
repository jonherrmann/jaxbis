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

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.googlecode.jaxbis.api.Encrypt;
import com.googlecode.jaxbis.api.EncryptBeforeSign;
import com.googlecode.jaxbis.api.SignBeforeEncrypt;

public class Employee2 extends Person2 {
    
    private Date dateOfHire;
    
    private Salary2 salary;
    
    private String departmentName;

    private List<Position2> positions;
    
    private Employee2 boss;
    
    public Employee2() {
    }

    public Employee2(
            final String givenName, final String middleName, 
            final String surname, final String salutation, 
            final Date dateOfBirth, final Address2 livesAt,
            final List<Address2> formalyLivedAt,
            final List<String> emailAddresses,
            final List<String> phoneNumbers,
            final Date dateOfHire, final Salary2 salary, 
            final List<Position2> positions,
            final Employee2 boss,
            final String departmentName) {
        
        super(givenName, middleName, surname, salutation, 
                dateOfBirth, livesAt, formalyLivedAt,
                emailAddresses, phoneNumbers);
        this.dateOfHire = dateOfHire;
        this.salary = salary;
        this.positions = positions;
        this.boss = boss;
        this.departmentName = departmentName;
    }
    
    @SignBeforeEncrypt
    public String getDepartmentName() {
        return departmentName;
    }

    public final void setDepartmentName(final String departmentName) {
        this.departmentName = departmentName;
    }

    @EncryptBeforeSign
    public Date getDateOfHire() {
        return dateOfHire;
    }

    public void setDateOfHire(final Date dateOfHire) {
        this.dateOfHire = dateOfHire;
    }

    @Encrypt
    public Salary2 getSalary() {
        return salary;
    }

    public void setSalary(final Salary2 salary) {
        this.salary = salary;
    }

    @XmlElementWrapper(name = "positions")
    @XmlElement(name = "position")
    public List<Position2> getPositions() {
        return positions;
    }

    public void setPositions(final List<Position2> positions) {
        this.positions = positions;
    }

    @Encrypt
    public Employee2 getBoss() {
        return boss;
    }

    public void setBoss(final Employee2 boss) {
        this.boss = boss;
    }
    
    
    

}
