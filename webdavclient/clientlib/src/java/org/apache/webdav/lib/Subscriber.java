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

package org.apache.webdav.lib;

import java.util.Map;

/**
 * The Subscriber interface
 * 
 */
public interface Subscriber {
    public final static String URI = "uri";
    public final static String SOURCE_URI = "source-uri";
    public final static String TARGET_URI = "target-uri";

    public void notify(String uri, Map information);
}