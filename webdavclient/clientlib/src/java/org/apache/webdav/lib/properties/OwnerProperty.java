/*
 * $Header$
 * $Revision: 207869 $
 * $Date: 2004-09-26 22:32:23 +0800 (Sun, 26 Sep 2004) $
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

package org.apache.webdav.lib.properties;

import org.apache.webdav.lib.ResponseEntity;
import org.w3c.dom.Element;

/**
 * This interface models the <code>&lt;D:owner&gt;</code> property, which is
 * defined in the WebDAV Access Control Protocol specification.
 */
public class OwnerProperty extends HrefValuedProperty {

    // -------------------------------------------------------------- Constants

    /**
     * The property name.
     */
    public static final String TAG_NAME = "owner";

    // ----------------------------------------------------------- Constructors

    /**
     * Default constructor for the property.
     */
    public OwnerProperty(ResponseEntity response, Element element) {
        super(response, element);
    }
}
