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

package com.googlecode.jaxbis.analyse.complex;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.googlecode.jaxbis.analyse.BeanIntrospector;
import com.googlecode.jaxbis.example2.Address2;
import com.googlecode.jaxbis.example2.Employee2;
import com.googlecode.jaxbis.example2.Position2;
import com.googlecode.jaxbis.example2.Salary2;
import com.googlecode.jaxbis.model.Bean;
import com.googlecode.jaxbis.model.Attribute;
import com.googlecode.jaxbis.model.BeanReference;
import com.googlecode.jaxbis.model.ConnectionType;
import static com.googlecode.jaxbis.api.SecurityType.*;
import static org.junit.Assert.*;

public class ComplexBeanIntrospectorTest {
    

    @Test public void testIntrospectEmployee() {
        final Map<String, Attribute> attributeMap;
        final Map<String, BeanReference> beanReferenceMap;
        final Map<String, Bean> introspectMap = BeanIntrospector.introspect(
                Employee2.class);
        final Bean employeeBean = introspectMap.get(
                    Employee2.class.getCanonicalName());
        assertEquals(5, introspectMap.size());
        
        assertNotNull(employeeBean);
        
        assertEquals("Employee2", employeeBean.getClassName());
        // assertEquals("com.googlecode.jaxbis.analyse.complex",
        assertEquals("com.googlecode.jaxbis.example2",
                employeeBean.getPackageName());
        attributeMap = employeeBean.getAttributeMap();
        assertNotNull(attributeMap);
        assertEquals(9, attributeMap.size());
        
        assertEquals(Date.class, attributeMap.get("dateOfBirth").getType());
        assertTrue(attributeMap.get("dateOfBirth").isSecured());
        assertEquals(Date.class, attributeMap.get("dateOfHire").getType());
        assertEquals(String.class, 
                attributeMap.get("emailAddresses").getType());
        assertEquals(String.class, 
                attributeMap.get("phoneNumbers").getType());
        assertEquals(List.class, 
                attributeMap.get("emailAddresses").getCollectionType());
        assertEquals(ConnectionType.COLLECTION, 
                attributeMap.get("phoneNumbers").getConnectionType());
        
        
        beanReferenceMap = employeeBean.getBeanReferenceMap();
        
        assertEquals(5, beanReferenceMap.size());
        
        assertSame(employeeBean, beanReferenceMap.get("boss").getBean());
        assertEquals(Employee2.class, beanReferenceMap.get("boss").getType());
        assertEquals(ConnectionType.DIRECT, 
                beanReferenceMap.get("boss").getConnectionType());
        assertEquals(ENCRYPTION, 
                beanReferenceMap.get("boss").getSecurityType());

        assertEquals(Salary2.class, beanReferenceMap.get("salary").getType());
        assertEquals(ConnectionType.DIRECT, 
                beanReferenceMap.get("salary").getConnectionType());
        assertEquals(ENCRYPTION, 
                beanReferenceMap.get("salary").getSecurityType());
        assertSame(beanReferenceMap.get("salary").getBean(), 
                introspectMap.get(Salary2.class.getCanonicalName()));
        checkSalaryBean(beanReferenceMap.get("salary").getBean());
        
        
        
        assertEquals(ConnectionType.DIRECT, 
                beanReferenceMap.get("livesAt").getConnectionType());
        
        assertEquals(ConnectionType.COLLECTION, 
                beanReferenceMap.get("formalyLivedAt").getConnectionType());
        assertEquals(ENCRYPTION, 
                beanReferenceMap.get("formalyLivedAt").getSecurityType());
        assertEquals(Address2.class, 
                beanReferenceMap.get("formalyLivedAt").getType());
        assertEquals(List.class, 
                beanReferenceMap.get("formalyLivedAt").getCollectionType());
        
        assertEquals(ConnectionType.COLLECTION, 
                beanReferenceMap.get("positions").getConnectionType());
        assertSame(beanReferenceMap.get("positions").getBean(), 
                introspectMap.get(Position2.class.getCanonicalName()));
        checkPositionsBean(beanReferenceMap.get("positions").getBean());
    }

    private void checkPositionsBean(final Bean bean) {
        final Map<String, Attribute> attributeMap;
        final Map<String, BeanReference> beanReferenceMap;
        
        assertEquals("Position2", bean.getClassName());
        assertEquals("com.googlecode.jaxbis.example2", 
                bean.getPackageName());
        
        attributeMap = bean.getAttributeMap();
        assertNotNull(attributeMap);
        assertEquals(1, attributeMap.size());
        assertEquals(String.class, attributeMap.get("title").getType());
        assertTrue(attributeMap.get("title").isSecured());
        
        beanReferenceMap = bean.getBeanReferenceMap();
        assertEquals(1, beanReferenceMap.size());
        assertTrue(beanReferenceMap.get("salaryRange").isSecured());
        
    }

    private void checkSalaryBean(final Bean bean) {
        final Map<String, Attribute> attributeMap;
        final Map<String, BeanReference> beanReferenceMap;
        assertEquals("Salary2", bean.getClassName());
        assertEquals("com.googlecode.jaxbis.example2", 
                bean.getPackageName());
        
        attributeMap = bean.getAttributeMap();
        assertNotNull(attributeMap);
        assertEquals(2, attributeMap.size());
        assertEquals(Integer.class, attributeMap.get("amount").getType());
        assertFalse(attributeMap.get("amount").isSecured());
        
        beanReferenceMap = bean.getBeanReferenceMap();
        assertEquals(0, beanReferenceMap.size());
    }
}
