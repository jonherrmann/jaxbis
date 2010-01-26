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

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.googlecode.jaxbis.api.Encrypt;
import com.googlecode.jaxbis.api.EncryptBeforeSign;
import com.googlecode.jaxbis.api.Sign;
import com.googlecode.jaxbis.api.SignBeforeEncrypt;
import com.googlecode.jaxbis.api.SecurityType;
import com.googlecode.jaxbis.model.Bean;
import com.googlecode.jaxbis.model.Attribute;
import com.googlecode.jaxbis.model.BeanReference;
import com.googlecode.jaxbis.model.ConnectionType;

import static com.googlecode.jaxbis.analyse.IntrospectorConstants.*;
import static com.googlecode.jaxbis.util.Assertor.*;

public final class BeanIntrospector {
    
    private BeanIntrospector() {
    }
    
    public static Map<String, Bean> introspect(final Class< ? > clasz) {
        final Map<String, Bean> beanMap = new HashMap<String, Bean>();
        introspect(clasz, beanMap);
        return beanMap;
    }
    
    private static Bean introspect(
            final Class< ? > clasz, final Map<String, Bean> beanMap) {
        final Bean bean;
        final String canonicalName = clasz.getCanonicalName();
        
        if (beanMap.containsKey(canonicalName)) {
            bean = beanMap.get(canonicalName);
        } else {
            bean = createBean(clasz, beanMap);
        }
        return bean;
    }

    private static Bean createBean(
            final Class< ? > clasz,
            final Map<String, Bean> beanMap) {
        final Bean bean = new Bean();
        beanMap.put(clasz.getCanonicalName(), bean);
        addNames(clasz, bean);
        addBeanReferenceAndAttributeMap(bean, clasz, beanMap);
        beanMap.put(bean.getFullName(), bean);
        return bean;
    }

    private static void addBeanReferenceAndAttributeMap(
            final Bean bean, final Class< ? > clasz, 
            final Map<String, Bean> beanMap) {
        final Map<String, Attribute> attributeMap 
            = new HashMap<String, Attribute>();
        final Map<String, BeanReference> beanReferenceMap
            = new HashMap<String, BeanReference>();
        final PropertyDescriptor[] descs = PropertyUtils.getPropertyDescriptors(
                clasz);
        
        for (PropertyDescriptor desc : descs) {
            if (!desc.getName().equals("class")) {
                analysePropertyDescriptor(beanMap, attributeMap,
                        beanReferenceMap, desc);
            }
        }
        bean.setAttributeMap(attributeMap);
        bean.setBeanReferenceMap(beanReferenceMap);
    }

    private static void analysePropertyDescriptor(
            final Map<String, Bean> beanMap,
            final Map<String, Attribute> attributeMap,
            final Map<String, BeanReference> beanReferenceMap,
            final PropertyDescriptor desc) {
        final Class< ? > propertyClasz = desc.getPropertyType();
        if (isAttribute(propertyClasz)) {
            attributeMap.put(desc.getName(), createAttribute(desc));
        } else if (isCollection(propertyClasz) 
                && isAttribute(getGenericTypeOfCollection(desc))) {
            attributeMap.put(desc.getName(), createAttribute(desc));
        } else {
            beanReferenceMap.put(
                    desc.getName(), createBeanReference(desc, beanMap));
        }
    }

    private static boolean isAttribute(final Class< ? > propertyClasz) {
        return ATTRIBUTE_TYPES.contains(propertyClasz);
    }

    private static BeanReference createBeanReference(
            final PropertyDescriptor desc, final Map<String, Bean> beanMap) {
        final Bean referencedBean;
        final BeanReference beanReference = new BeanReference();
        addAttributeInfo(desc, beanReference);
        
        referencedBean = introspect(beanReference.getType(), beanMap);
        beanReference.setBean(referencedBean);

        return beanReference;
    }

    private static boolean isCollection(final Class< ? > propertyClasz) {
        return Collection.class.isAssignableFrom(propertyClasz);
    }

    private static Class< ? > getGenericTypeOfCollection(
            final PropertyDescriptor desc) {
        final Type[] typeArguments = (
                (ParameterizedType) 
                desc.getReadMethod().getGenericReturnType()).
            getActualTypeArguments();
        assertEquals(1, typeArguments.length);
        return (Class< ? >) typeArguments[0];
    }

    private static Attribute createAttribute(
            final PropertyDescriptor desc) {
        final Attribute attribute = new Attribute();
        addAttributeInfo(desc, attribute);
        return attribute;
    }
    

    private static void addAttributeInfo(final PropertyDescriptor desc,
            final Attribute attribute) {
        attribute.setName(desc.getName());
        
        if (isCollection(desc.getPropertyType())) {
            attribute.setConnectionType(ConnectionType.COLLECTION);
            attribute.setCollectionType(desc.getPropertyType());
            attribute.setType(getGenericTypeOfCollection(desc));
        } else {
            attribute.setConnectionType(ConnectionType.DIRECT);
            attribute.setType(desc.getPropertyType());
        }
        
        // TODO check: Annotations are mutual exclusive
        if (desc.getReadMethod().getAnnotation(Encrypt.class) != null) {
            attribute.setSecurityType(SecurityType.ENCRYPTION);
        } else if (desc.getReadMethod().getAnnotation(Sign.class) != null) {
            attribute.setSecurityType(SecurityType.SIGNATURE);
        } else if (desc.getReadMethod().getAnnotation(
                EncryptBeforeSign.class) != null) {
            attribute.setSecurityType(SecurityType.ENCRYPTION_BEFORE_SIGNATURE);
        } else if (desc.getReadMethod().getAnnotation(
                SignBeforeEncrypt.class) != null) {
            attribute.setSecurityType(SecurityType.SIGNATURE_BEFORE_ENCRYPTION);
        } else {
            attribute.setSecurityType(SecurityType.UNSECURED);
        }
    }

    private static void addNames(final Class< ? > clasz, final Bean bean) {
        bean.setClassName(clasz.getSimpleName());
        bean.setPackageName(clasz.getCanonicalName().substring(
                0, clasz.getCanonicalName().length() 
                - clasz.getSimpleName().length() - 1));
        bean.setFullName(clasz.getCanonicalName());
    }

}

