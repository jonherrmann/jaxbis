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

import java.util.Arrays;

import java.util.List;

import javax.xml.bind.JAXB;

import static com.googlecode.jaxbis.test.DateFactory.*;

public final class Employee2ExampleFactory {
    
    private Employee2ExampleFactory() {
    }
    
    public static Employee2 createExample() {
        final Employee2 employee;
        
        final Address2 address = new Address2(
                "Kölner Straße", "Bonn", "NRW", "12345", "Germany");
        
        final Address2 address2 = new Address2(
                "Bonner Straße", "Köln", "NRW", "54321", "Germany");
        
        final Address2 address3 = new Address2(
                "Lange Straße", "Sankt Augustin", "NRW", "22222", "Germany");
        
        final Salary2 salary = new Salary2(20000, "EUR");
        
        final List<Position2> positions = Arrays.asList(
                new Position2("caretaker", new SalaryRange2(
                        new Salary2(10000, "EUR"), new Salary2(25000, "EUR"))),
                new Position2("director", new SalaryRange2(
                        new Salary2(50000, "USD"), new Salary2(100000, "USD")))
                );
        
        
        employee = new Employee2(
                "Kalle", "August", "Test", "Herr", 
                createDate("1950-06-23"), address,
                Arrays.asList(address2, address3),
                Arrays.asList("a@b.c", "x@y.z"),
                Arrays.asList("+12-34", "+45-54"),
                createDate("2003-11-10"), 
                salary, positions, null, "K04");
        return employee;
    }
    
    public static Employee2 createExample2() {
        final Employee2 employee;
        
        final Address2 address = new Address2(
                "Lange Straße", "Troisdorf", "NRW", "12321", "Germany");
        
        final Address2 address2 = new Address2(
                "Kurze Straße", "Hennef", "NRW", "55555", "Germany");
        
        final Address2 address3 = new Address2(
                "Mittlere Straße", "Grevenbroich", "NRW", "121212", "Germany");
        
        final Salary2 salary = new Salary2(200000, "EUR");
        
        final List<Position2> positions = Arrays.asList(
                new Position2("The Boss", new SalaryRange2(
                        new Salary2(100000, "USD"), 
                        new Salary2(1000000, "USD")))
                );
        
        
        employee = new Employee2(
                "Karl", "Theodor", "zu Test", "Herr", 
                createDate("1952-08-13"), address,
                Arrays.asList(address2, address3),
                Arrays.asList("c@b.a", "z@x.y"),
                Arrays.asList("+34-12", "+35-24"),
                createDate("1995-07-10"), 
                salary, positions, null, "Z16");
        return employee;
    }
    
    
    public static Employee2 createExampleWithBoss() {
        final Employee2 employee = createExample();
        final Employee2 boss = createExample2();
        employee.setBoss(boss);
        return employee;
    }
    
    public static void main(final String[] args) {
        JAXB.marshal(createExample(), System.out);
    }

}
