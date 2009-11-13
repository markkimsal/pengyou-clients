/*
 * $Header$
 * $Revision: 208417 $
 * $Date: 2005-01-14 17:39:23 +0800 (Fri, 14 Jan 2005) $
 *
 * ====================================================================
 *
 * Copyright 1999-2002 The Apache Software Foundation 
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
 *
 */ 

package org.apache.webdav.lib.util;

import org.w3c.dom.Node;

/**
 * A <code>QName</code> represents a fully-qualified name.
 */
public class QName
{
	private String namespaceURI;
	private String localName;
	private int hashCode;

	public QName(String namespaceURI, String localName)
	{
		this.namespaceURI = (namespaceURI == null ? "" : namespaceURI).intern();
		this.localName = localName.intern();
		
		this.hashCode= new StringBuffer()
            .append(this.namespaceURI.hashCode())
            .append('_')
            .append(this.localName.hashCode())
            .toString()
            .hashCode();
	}

	public String getNamespaceURI()
	{
		return this.namespaceURI;
	}

	public String getLocalName()
	{
		return this.localName;
	}

	public int hashCode()
	{
		return this.hashCode;
	}

	public boolean equals(Object obj)
	{
      if (this == obj) return true;
      if (obj instanceof QName) {
          QName that = (QName)obj;
          return this.namespaceURI == that.namespaceURI &&
                 this.localName == that.localName;
      }
      return false;
	}

	public boolean matches(Node node)
	{
		return (node!=null)
					&& (node.getNamespaceURI()!=null)
					&& (node.getLocalName()!=null)
					&& (node.getNamespaceURI().intern()==this.namespaceURI)
					&& (node.getLocalName().intern()==this.localName);
	}

	public String toString()
	{
		return namespaceURI + ':' + localName;
	}
}
