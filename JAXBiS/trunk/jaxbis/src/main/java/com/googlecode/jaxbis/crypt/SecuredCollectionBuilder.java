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

package com.googlecode.jaxbis.crypt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Node;

import com.googlecode.jaxbis.api.SecuredObject;
import com.googlecode.jaxbis.api.SecuredObjectBuilder;

public final class SecuredCollectionBuilder {
    
    private SecuredCollectionBuilder() {
    }
    
    public static <U, S> Collection<S> create(
            final SecuredObjectBuilder<U, S> builder, 
            final Collection< U > objects) {
        final Collection<S> collection = new ArrayList<S>();
        for (final U o : objects) {
            collection.add(builder.create(o));
        }
        return collection;
    }
    
    public static <U, S extends SecuredObject<U>> List<U> createPlainForRef(
            final Collection< ? > objects,
            final Class<S> clasz,
            final KeyRing keyRing) {
        final List<U> collection = new ArrayList<U>();
        for (final Object o : objects) {
            final DOMSource domSource = new DOMSource((Node) o);
            final SecuredObject<U> securedObject 
                = JAXB.unmarshal(domSource, clasz);
            collection.add(securedObject.getPlainInstance(keyRing));
        }
        return collection;
    }

    public static <U, S extends SecuredObject<U>> List<U> createPlain(
            final Collection<S> objects, final KeyRing keyRing) {
        final List<U> collection = new ArrayList<U>();
        for (final S o : objects) {
            collection.add(o.getPlainInstance(keyRing));
        }
        return collection;
    }
    

}
