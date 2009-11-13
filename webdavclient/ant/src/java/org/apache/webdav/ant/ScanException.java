/* 
 * $Header$
 * $Revision: 207541 $
 * $Date: 2004-07-28 17:48:34 +0800 (Wed, 28 Jul 2004) $
 * ========================================================================
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
 * ========================================================================
 */
package org.apache.webdav.ant;

import org.apache.tools.ant.BuildException;

/**
 * Signals an error condition during the scan of a directory or web 
 * collection.
 *
 */

public class ScanException extends BuildException  {

    public ScanException() {
	super();
    }

    /**
     * Constructs an exception with the given descriptive message.
     * @param msg Description of or information about the exception.
     */

    public ScanException(String msg) {
	super(msg);
    }

    /**
     * Constructs an exception with the given message and exception as
     * a root cause.
     * @param msg Description of or information about the exception.
     * @param cause Throwable that might have cause this one.
     */

    public ScanException(String msg, Throwable cause) {
	super(msg, cause);
    }
}
