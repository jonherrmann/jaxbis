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

package com.googlecode.jaxbis.model;

import static com.googlecode.jaxbis.util.CollectionFilter.filter;

import java.util.Collection;
import java.util.Map;

import com.googlecode.jaxbis.util.CollectionFilter.Filter;


public final class Bean {
    
    private String fullName;
    private String className;
    private String packageName;
    
    private Map<String, Attribute> attributeMap;
    private Map<String, BeanReference> beanReferenceMap;
    
    public String getClassName() {
        return className;
    }
    public void setClassName(final String className) {
        this.className = className;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }
    public Map<String, Attribute> getAttributeMap() {
        return attributeMap;
    }
    public void setAttributeMap(final Map<String, Attribute> attributeMap) {
        this.attributeMap = attributeMap;
    }

    public Map<String, BeanReference> getBeanReferenceMap() {
        return beanReferenceMap;
    }
    public void setBeanReferenceMap(
            final Map<String, BeanReference> beanReferenceMap) {
        this.beanReferenceMap = beanReferenceMap;
    }
    

    
    public Collection<BeanReference> getNotSecuredDirectBeanReferences() {
        return filterBeanReferences(false, ConnectionType.DIRECT);
    }
    
    public Collection<BeanReference> getSecuredDirectBeanReferences() {
        return filterBeanReferences(true, ConnectionType.DIRECT);
    }
    
    public Collection<BeanReference> getSecuredCollectionBeanReferences() {
        return filterBeanReferences(true, ConnectionType.COLLECTION);
    }
    
    public Collection<BeanReference> getNotSecuredCollectionBeanReferences() {
        return filterBeanReferences(false, ConnectionType.COLLECTION);
    }
    
    public Collection<BeanReference> filterBeanReferences(
            final boolean secured, final ConnectionType connectionType) {
        return filter(beanReferenceMap.values(), new Filter<BeanReference>() {
            @Override public boolean filter(final BeanReference object) {
                return object.isSecured() == secured 
                    && object.getConnectionType() == connectionType;
            }
        });
    }
    
    public Collection<Attribute> getSecuredAttributesAndBeanReferences() {
        final Collection<Attribute> attributes = filterAttributes(true);
        attributes.addAll(getSecuredCollectionBeanReferences());
        attributes.addAll(getSecuredDirectBeanReferences());
        return attributes;
    }

    public Collection<Attribute> getSecuredDirectAttributes() {
        return filterAttributes(true, ConnectionType.DIRECT);
    }
    
    public Collection<Attribute> getSecuredCollectionAttributes() {
        return filterAttributes(true, ConnectionType.COLLECTION);
    }
    
    public Collection<Attribute> getNotSecuredAttributes() {
        return filterAttributes(false);
    }
    
    public Collection<Attribute> getNotSecuredDirectAttributes() {
        return filterAttributes(false, ConnectionType.DIRECT);
    }
    
    public Collection<Attribute> getNotSecuredCollectionAttributes() {
        return filterAttributes(false, ConnectionType.COLLECTION);
    }
    
    private Collection<Attribute> filterAttributes(final boolean secured,
            final ConnectionType connectionType) {
        return filter(attributeMap.values(), new Filter<Attribute>() {
            @Override public boolean filter(final Attribute object) {
                return object.isSecured() == secured 
                    && object.getConnectionType() == connectionType;
            }
        });
    }
    private Collection<Attribute> filterAttributes(final boolean secured) {
        return filter(attributeMap.values(), new Filter<Attribute>() {
            @Override public boolean filter(final Attribute object) {
                return object.isSecured() == secured;
            }
        });
    }
    
    public boolean hasSecuredMembers() {
        return getSecuredAttributesAndBeanReferences().size() > 0;
    }
    
    public boolean hasSecuredCollectionMembers() {
        return getSecuredCollectionAttributes().size() 
            + getSecuredCollectionBeanReferences().size() > 0;
    }
    
    public boolean hasCollectionBeanReferences() {
        return getSecuredCollectionBeanReferences().size() 
            + getNotSecuredCollectionBeanReferences().size() > 0;
    }
    
    
    public boolean hasSecuredCollectionAttributes() {
        return getSecuredCollectionAttributes().size() > 0;
    }
    
    public boolean hasSecuredObjects() {
        return hasSecuredMembers() || !getBeanReferenceMap().isEmpty();
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

}
