/*
 * $Header: /home/cvs/jakarta-slide/webdavclient/clientlib/src/java/org/apache/webdav/lib/properties/CheckedoutProperty.java,v 1.1.2.1 2004/10/08 15:04:36 luetzkendorf Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/10/08 15:04:36 $
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

package org.pengyou.client.lib.properties;

import org.w3c.dom.Element;
import org.pengyou.client.lib.ResponseEntity;

/**
 * This interface models the <code>&lt;D:checked-in&gt;</code> property, which is
 * defined in the WebDAV Access Control Protocol specification.
 *
 */
public class CheckedoutProperty extends HrefValuedProperty {


    // -------------------------------------------------------------- Constants


    /**
     * The property name.
     */
    public static final String TAG_NAME = "checked-out";



    // ----------------------------------------------------------- Constructors


    /**
     * Default constructor for the property.
     */
    public CheckedoutProperty(ResponseEntity response, Element element) {
        super(response, element);
    }
}
