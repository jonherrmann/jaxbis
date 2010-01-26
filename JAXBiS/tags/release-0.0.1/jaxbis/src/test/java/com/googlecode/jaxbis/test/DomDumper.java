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

package com.googlecode.jaxbis.test;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

public final class DomDumper {

    private DomDumper() {
    }
    
    public static String writeDom(
            final Node node, final boolean prettyPrint) {
        final DOMImplementationLS impl = getDOMImplLS();
        final LSSerializer writer = impl.createLSSerializer();
        writer.getDomConfig().setParameter("format-pretty-print", prettyPrint);
        return writer.writeToString(node);
    }
    
    public static void dumpDom(final Node node) {
        System.out.println(writeDom(node, true));
    }
    
    public static void dumpDomNP(final Node node) {
        System.out.println(writeDom(node, false));
    }
    
    public static Document parseDom(final String inString) {
        final DOMImplementationLS impl = getDOMImplLS();
        final LSParser parser = impl.createLSParser(
                DOMImplementationLS.MODE_SYNCHRONOUS, null);
        final LSInput input = impl.createLSInput();
        input.setStringData(inString);
        return parser.parse(input);
    }
    
    // FIXME Does not work correctly with signatures!
    @Deprecated
    public static Document deepCloneDocument(final Document doc) {
        return parseDom(writeDom(doc, false));
    }

    private static DOMImplementationLS getDOMImplLS() {
        DOMImplementationRegistry registry;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        final DOMImplementationLS impl = 
            (DOMImplementationLS) registry.getDOMImplementation("LS");
        return impl;
    }
    
}
