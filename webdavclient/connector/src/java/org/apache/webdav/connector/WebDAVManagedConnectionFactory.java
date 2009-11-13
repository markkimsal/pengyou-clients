/*
 * $Header$
 * $Revision: 208159 $
 * $Date: 2004-11-17 14:32:22 +0800 (Wed, 17 Nov 2004) $
 *
 * ====================================================================
 *
 * Copyright 2004 The Apache Software Foundation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.webdav.connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.Properties;
import java.security.AccessController;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;

import org.apache.commons.httpclient.HttpException;

/**
 * 
 * @version $Revision: 208159 $
 *  
 */
public class WebDAVManagedConnectionFactory implements ManagedConnectionFactory {

   private String connectionURL = null;
   private String username = null;
   private String password = null;
   private Integer timeout = null;

   public String getConnectionURL() {
      return connectionURL;
   }

   public void setConnectionURL(final String  connectionURL) {
      this.connectionURL = connectionURL;
   }

   public String getUserName() {
      return username;
   }

   public void setUserName(final String  username) {
      this.username = username;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getPassword() {
      return password;
   }

   public void setTimeout(Integer timeout) {
      this.timeout = timeout;
   }

   public Integer getTimeout() {
      return timeout;
   }


    protected PrintWriter writer;

    /**
     * @see ManagedConnectionFactory#createConnectionFactory(ConnectionManager)
     */
    public Object createConnectionFactory(ConnectionManager cm) throws ResourceException {

        return new WebDAVConnectionFactory(this, cm);
    }

    /**
     * @see ManagedConnectionFactory#createConnectionFactory()
     */
    public Object createConnectionFactory() throws ResourceException {

        return new WebDAVConnectionFactory(this, null);
    }

    /**
     * @see ManagedConnectionFactory#createManagedConnection(Subject,
     *      ConnectionRequestInfo)
     */
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {

       if(subject != null) {
          Properties props = new Properties();
          WebDAVSubjectAction action = new WebDAVSubjectAction(subject, props, this);
          Boolean matched = (Boolean) AccessController.doPrivileged(action);
          if(matched.booleanValue() ) {
             String userName = props.getProperty("userName");
             String password = props.getProperty("password");

             try {
                WebDAVConnectionSpec spec =
                     new WebDAVConnectionSpec(connectionURL, userName, password, timeout.intValue());

               System.out.println("Creating WebDAVConnection using JAAS Subject:  " + userName);
               return new WebDAVManagedConnection(spec);
             }
             catch(IOException e) {
               throw new ResourceException("Could not create managed connection", e.toString());
             }
          }
          else {
            // No matching credentials were found on the subject
            throw new ResourceException("Could not create managed connection");
          }
       }


       if(cxRequestInfo instanceof WebDAVConnectionSpec ) {
        try {
            System.out.println("Creating WebDAVConnection with WebDAVConnectionSpec");
            return new WebDAVManagedConnection(cxRequestInfo);
        } catch (HttpException e) {
            if (writer != null) {
                writer.println("Exception: " + e);
                e.printStackTrace(writer);
            }
            // XXX only in 1.4
//            throw new ResourceException("Could not create managed connection", e);
            throw new ResourceException("Could not create managed connection", e.toString());
        } catch (IOException e) {
            if (writer != null) {
                writer.println("Exception: " + e);
                e.printStackTrace(writer);
            }
            // XXX only in 1.4
//          throw new ResourceException("Could not create managed connection", e);
            throw new ResourceException("Could not create managed connection", e.toString());
        }
       }


       try {
         if(connectionURL != null && username != null) {
            System.out.println("Creating WebDAVConnection with configured properties");
            WebDAVConnectionSpec spec =
                    new WebDAVConnectionSpec(connectionURL, username, password, timeout.intValue());

             return new WebDAVManagedConnection(spec);
         }
         else {
            throw new ResourceException("Could not create managed connection");
         }
       }
       catch(Exception e) {
         throw new ResourceException("Could not create managed connection", e.toString());
       }
    }


    /**
     * @see ManagedConnectionFactory#matchManagedConnections(Set, Subject,
     *      ConnectionRequestInfo)
     */
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject,
            ConnectionRequestInfo cxRequestInfo) throws ResourceException {

        ManagedConnection match = null;
        Iterator iterator = connectionSet.iterator();
        if (iterator.hasNext()) {
            match = (ManagedConnection) iterator.next();
        }

        return match;
    }

    /**
     * @see ManagedConnectionFactory#setLogWriter(PrintWriter)
     */
    public void setLogWriter(PrintWriter writer) throws ResourceException {

        this.writer = writer;
    }

    /**
     * @see ManagedConnectionFactory#getLogWriter()
     */
    public PrintWriter getLogWriter() throws ResourceException {

        return writer;
    }

    public boolean equals(Object other) {

        if (other instanceof WebDAVManagedConnectionFactory) {
            return true;
        }
        return false;
    }

    public int hashCode() {

        return 0;
    }
}