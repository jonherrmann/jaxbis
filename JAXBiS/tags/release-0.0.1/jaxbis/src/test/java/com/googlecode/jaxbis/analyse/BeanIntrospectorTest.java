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

package com.googlecode.jaxbis.analyse;

import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.googlecode.jaxbis.model.Bean;
import com.googlecode.jaxbis.model.Attribute;
import static com.googlecode.jaxbis.api.SecurityType.*;

import static org.junit.Assert.*;

public class BeanIntrospectorTest {

    @Test public void testIntrospectEmployee() {
        final Map<String, Attribute> attributeMap;
        final Bean bean = BeanIntrospector.introspect(
                Employee.class).get(Employee.class.getCanonicalName());
        
        assertNotNull(bean);
        
        assertEquals("Employee", bean.getClassName());
        assertEquals("com.googlecode.jaxbis.analyse", 
                bean.getPackageName());
        attributeMap = bean.getAttributeMap();
        assertNotNull(attributeMap);
        assertEquals(6, attributeMap.size());
        
        assertEquals("id", attributeMap.get("id").getName());
        assertEquals("name", attributeMap.get("name").getName());
        assertEquals("dateOfBirth", attributeMap.get("dateOfBirth").getName());
        assertEquals("department", attributeMap.get("department").getName());

        
        assertEquals(int.class, attributeMap.get("id").getType());
        assertEquals(String.class, attributeMap.get("name").getType());
        assertEquals(Date.class, attributeMap.get("dateOfBirth").getType());
        assertEquals(String.class, attributeMap.get("department").getType());
        
        
        
        assertEquals(false, attributeMap.get("id").isSecured());
        assertEquals(true, attributeMap.get("name").isSecured());
        assertEquals(true, attributeMap.get("dateOfBirth").isSecured());
        assertEquals(true, attributeMap.get("dateOfHire").isSecured());
        assertEquals(true, attributeMap.get("department").isSecured());
        assertEquals(true, attributeMap.get("position").isSecured());
        
        assertEquals(ENCRYPTION, 
                attributeMap.get("name").getSecurityType());
        assertEquals(ENCRYPTION, 
                attributeMap.get("dateOfBirth").getSecurityType());
        assertEquals(SIGNATURE, 
                attributeMap.get("position").getSecurityType());
        assertEquals(SIGNATURE_BEFORE_ENCRYPTION, 
                attributeMap.get("department").getSecurityType());
        assertEquals(ENCRYPTION_BEFORE_SIGNATURE, 
                attributeMap.get("dateOfHire").getSecurityType());
    }
    
}
