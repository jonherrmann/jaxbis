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

package com.googlecode.jaxbis.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public final class Logger {

    private static org.apache.log4j.Logger log4jLogger = 
        org.apache.log4j.Logger.getLogger("JAXBiS Logger");
    
    @SuppressWarnings("unused")
    private static Logger instance = new Logger();
    
    public static void log(final Object caller, 
            final int level, final String message, 
            final Node node) {
        final DOMImplementationLS impl = getDOMImplLS();
        final LSSerializer writer = impl.createLSSerializer();
        writer.getDomConfig().setParameter("format-pretty-print", true);

        log4jLogger.log(Level.toLevel(level), "\n" 
                + caller.getClass().getName() + ":\n--\n" + message
                + "\nDom:\n" + writer.writeToString(node) + "\n--\n");
    }
    
    public static void log(final Object caller, 
            final int level, final String message) {
        log4jLogger.log(Level.toLevel(level), "\n" 
                + caller.getClass().getName() + ":\n--\n" + message + "\n--\n");
    }
    
    private Logger() {
        try {            
            final SimpleLayout layout = new SimpleLayout();
            final ConsoleAppender consoleAppender
                = new ConsoleAppender(layout);
            log4jLogger.addAppender(consoleAppender);
            final FileAppender fileAppender
                 = new FileAppender(layout, "JAXBiS.log", false);
            log4jLogger.addAppender(fileAppender);
            log4jLogger.setLevel(Level.WARN);
            // log4jLogger.setLevel(Level.DEBUG);
          } catch (final Exception e) {
            throw new RuntimeException(e);
          }   
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
