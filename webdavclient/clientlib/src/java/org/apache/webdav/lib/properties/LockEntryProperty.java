/*
 * $Header$
 * $Revision: 207541 $
 * $Date: 2004-07-28 17:48:34 +0800 (Wed, 28 Jul 2004) $
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

import org.apache.webdav.lib.Property;

/**
 * @version $Revision: 207541 $
 */
public interface LockEntryProperty extends Property {

    public static final short TYPE_WRITE = 0;

    public static final short SCOPE_EXCLUSIVE = 0;
    public static final short SCOPE_SHARED = 1;

    public abstract short getScope();
    public abstract short getType();
}
