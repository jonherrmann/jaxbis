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

package com.googlecode.jaxbis.generate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.googlecode.jaxbis.analyse.BeanIntrospector;
import com.googlecode.jaxbis.model.Bean;

// Class Generator
// TODO Exception handling
// TODO docu
// TODO velocity

public class Generator {
    
    private static final String TEMPLATE_PATH 
        = "com/googlecode/jaxbis/generate/template/";
    
    private String securedObjectTemplatePath 
        = TEMPLATE_PATH + "SecuredObject.java.vm";
    
    private String securedObjectBuilderTemplatePath
        = TEMPLATE_PATH +  "SecuredObjectBuilder.java.vm";
    
    private final VelocityEngine ve 
        = ClassLoaderVelocityEngineFactory.createVelocityEngine();
    
    private final Map<String, Bean> beanMap;

    private final String basePath;

    
    public Generator(final Class< ? > clasz) throws Exception {
        this(clasz, "out/");
    }
    
    public Generator(
            final Class< ? > clasz, final String basePath) throws Exception {
        System.out.println("Output directory: " + basePath);
        ve.init();

        this.beanMap = BeanIntrospector.introspect(clasz);
        this.basePath = basePath;
    }
    
    public Generator(
            final String classPath, 
            final String pkg, 
            final String className,
            final String basePath) {

        System.out.println(
                "Initiating Generator with parameters: \n"
                + "ClassPath:  " + classPath + "\n" 
                + "Package:    " + pkg + "\n" 
                + "ClassName:  " + className + "\n" 
                + "BasePath:   " + basePath);

        // Eclipse class loader workaround
        final Thread current = Thread.currentThread();
        final ClassLoader oldLoader = current.getContextClassLoader();
        try {
            current.setContextClassLoader(getClass().getClassLoader());
            final URLClassLoader cl = new URLClassLoader(
                    new URL[] { new URL("file://" + classPath) });
            this.beanMap = BeanIntrospector.introspect(
                    cl.loadClass(pkg + "." + className));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            current.setContextClassLoader(oldLoader);
        }

        this.basePath = basePath;
        System.out.println("Generator initiated!");
    }

    public void generate() throws Exception {
        final Template securedObjectTemplate = ve.getTemplate(
                    securedObjectTemplatePath);
        final Template securedObjectBuilderTemplate = ve.getTemplate(
                    securedObjectBuilderTemplatePath);
        for (final Bean bean : beanMap.values()) {
            final VelocityContext context = new VelocityContext();
            context.put("bean", bean);
            merge(bean, context, "Secured", "", 
                    securedObjectTemplate);
            merge(bean, context, "Secured", "Builder", 
                    securedObjectBuilderTemplate);
        }
    }

    private void merge(
            final Bean bean, final VelocityContext context,
            final String prefix, final String postfix,
            final Template template) throws IOException {
        
        final Writer writer = createWriter(bean, prefix, postfix);
        template.merge(context, writer);
        writer.close();
    }

    private Writer createWriter(
            final Bean bean, final String prefix, 
            final String postfix) throws IOException {
        final String path = 
            basePath + bean.getPackageName().replace('.', '/') + "/";
        final String fileName = 
            prefix + bean.getClassName() + postfix + ".java";
        System.out.println("Generating: " + fileName);
        new File(path).mkdirs();
        return new BufferedWriter(new FileWriter(path + fileName));
    }
    
    public void setSecuredObjectTemplatePath(
            final String securedObjectTemplatePath) {
        this.securedObjectTemplatePath = securedObjectTemplatePath;
    }
    
    public void setSecuredObjectBuilderTemplatePath(
            final String securedObjectBuilderTemplatePath) {
        this.securedObjectBuilderTemplatePath 
            = securedObjectBuilderTemplatePath;
    }

}
