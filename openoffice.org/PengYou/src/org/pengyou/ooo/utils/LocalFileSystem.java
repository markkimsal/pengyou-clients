/*
 * Licensed to Jeremy Bethmont under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pengyou.ooo.utils;

import java.io.File;

public class LocalFileSystem {
	private static String fileName = "PengYou Documents";
	
	public static String getDocumentsPath() {
		File f = new File(System.getProperty("user.home") + File.separatorChar + fileName); 
		if (!f.exists()) {
			f.mkdirs();
		}
		return f.getAbsolutePath();
	}
	
	public static String getLocalPath(String dst, String name) {
		String path = getDocumentsPath() + dst.replace('/', File.separatorChar) + 
						File.separatorChar + name.replace('/', File.separatorChar);
		File f = new File(path); 
		if (!f.exists()) {
			f.mkdirs();
		}
		f.delete();
		return f.getAbsolutePath();
	}
}
