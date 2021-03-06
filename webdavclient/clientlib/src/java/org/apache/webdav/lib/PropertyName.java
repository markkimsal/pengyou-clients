/*
 * $Header$
 * $Revision: 207563 $
 * $Date: 2004-08-02 23:45:51 +0800 (Mon, 02 Aug 2004) $
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
 */

package org.apache.webdav.lib;

import org.apache.webdav.lib.util.QName;

/**
 * This class models a DAV property name.
 *
 * @version $Revision: 207563 $
 */
public class PropertyName extends QName
{
    public PropertyName(String namespaceURI, String localName)
    {
        super(namespaceURI, localName);
    }

}
