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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Priority;
import org.apache.xml.security.encryption.XMLCipher;

import com.googlecode.jaxbis.util.Logger;

public final class SecuredMemberConfig {
    
    private Configuration configuration;
    
    public Configuration getConfiguration() {
        return configuration;
    }
    
    private static SecuredMemberConfig instance = new SecuredMemberConfig();
    
    private SecuredMemberConfig() {
        org.apache.xml.security.Init.init();
        
        configuration = new Configuration();
        configuration.setNameSpace("http://code.google.com/p/jaxbis/");
        configuration.setPrefix("jaxbis");
        
        configuration.setAsymKeySize(512);
        configuration.setAsymKeyType(XMLCipher.RSA_v1dot5);
        
        configuration.setSymKeySize(256);
        configuration.setSymKeyType(XMLCipher.AES_256);
        
        try {
            // Try to set strong key size
            // this will fail if the JCE jurisdiction policy files are not
            // installed.
            final SecretKey tK = configuration.getSymKeyGen().generateKey();
            final byte[] rB = tK.getEncoded();
            final SecretKeySpec tKSpec = new SecretKeySpec(rB, "AES");
            final Cipher tC = Cipher.getInstance("AES/ECB/NoPadding");
            tC.init(Cipher.ENCRYPT_MODE, tKSpec);
        } catch (final InvalidKeyException e) {
            // So lets take 128 Bit
            Logger.log(this, Priority.WARN_INT,
                    "Auto-fallback to 128 Bit key size.\n"
                    + "Please install JCE policy files if you require"
                    + " stronger encryption.");
            configuration.setSymKeySize(128);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static SecuredMemberConfig getInstance() {
        return instance;
    }
    
    public class Configuration  {

        private String nameSpace;

        private String prefix;

        private int asymKeySize;
        private String asymKeyType;
        
        private int symKeySize;
        private String symKeyType;

        private KeyGenerator symKeyGen;
        
        
        public final KeyGenerator getSymKeyGen() {
            return symKeyGen;
        }

        public final String getSymKeyType() {
            return symKeyType;
        }

        public final void setSymKeyType(final String symKeyType) {
            this.symKeyType = symKeyType;
        }
        
        public final String getAsymKeyType() {
            return asymKeyType;
        }

        public final void setAsymKeyType(final String asymKeyType) {
            this.asymKeyType = asymKeyType;
        }
        
        public final int getAsymKeySize() {
            return asymKeySize;
        }

        public final void setAsymKeySize(final int asymKeySize) {
            this.asymKeySize = asymKeySize;
        }

        public final int getSymKeySize() {
            return symKeySize;
        }

        public final void setSymKeySize(final int symKeySize) {
            try {
                this.symKeyGen = KeyGenerator.getInstance("AES");
            } catch (final NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            this.symKeySize = symKeySize;
            this.symKeyGen.init(symKeySize);
        }
        
        public final String getNameSpace() {
            return nameSpace;
        }

        public final void setNameSpace(final String nameSpace) {
            this.nameSpace = nameSpace;
        }

        public final String getPrefix() {
            return prefix;
        }

        public final void setPrefix(final String prefix) {
            this.prefix = prefix;
        }
    }
}
