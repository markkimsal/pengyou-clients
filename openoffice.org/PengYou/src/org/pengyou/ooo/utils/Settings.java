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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	private static String fileName = "settings.ini";
	private static File f = null;
	private static Properties p = null;;
	
	private static void checkFile() throws IOException {
		if (p != null)
			return ;
		load();
	}
	
	public static void load() throws IOException {
		String path = System.getProperty("user.home") + File.separatorChar;
		String os = System.getProperty("os.name");
		if (os.toLowerCase().indexOf("linux") != -1)
			path += ".pengyou";
		else if (os.toLowerCase().indexOf("windows") != -1)
			path += "Application Data" + File.separatorChar + "PengYou";
		else if (os.toLowerCase().indexOf("mac") != -1)
			path += ".pengyou";
		f = new File(path); 
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(path + File.separatorChar + fileName);
		if (!f.exists()) {
			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			fo.write("server=http://jeremi.info\nport=8080\npath=/pengyou/repository/default\nuser=test\npassword=test\nuse-proxy=no\nhost-proxy=\nport-proxy=\nuser-proxy=\npassword-proxy=\n".getBytes());
			fo.flush();
			fo.close();
		}
		p = new Properties();
		p.load(new FileInputStream(f));
	}
	
	public static void store() throws IOException {
		checkFile();
		p.store(new FileOutputStream(f), "By OpenOffice.org");
	}
	
	public static String get(String key) throws IOException {
		checkFile();
		return p.getProperty(key);
	}
	
	public static void set(String key, String value) throws IOException {
		checkFile();
		p.put(key, value);
	}
	
	public static String getServer() throws IOException {
		checkFile();
		return p.getProperty("server");
	}
	
	public static int getPort() throws IOException {
		checkFile();
		if (p.getProperty("port").length() > 0)
			return Integer.parseInt(p.getProperty("port"));
		return -1;
	}
	
	public static String getPath() throws IOException {
		checkFile();
		return p.getProperty("path");
	}
	
	public static String getUser() throws IOException {
		checkFile();
		return p.getProperty("user");
	}
	
	public static String getPassword() throws IOException {
		checkFile();
		return p.getProperty("password");
	}
	
	public static void setServer(String value) throws IOException {
		checkFile();
		p.put("server", value);
	}
	
	public static void setPort(int value) throws IOException {
		checkFile();
		p.put("port", Integer.toString(value));
	}
	
	public static void setPath(String value) throws IOException {
		checkFile();
		p.put("path", value);
	}
	
	public static void setUser(String value) throws IOException {
		checkFile();
		p.put("user", value);
	}
	
	public static void setPassword(String value) throws IOException {
		checkFile();
		p.put("password", value);
	}
	
	public static boolean getProxy() throws IOException {
		checkFile();
		if (p.getProperty("use-proxy").indexOf("yes") != -1)
			return true;
		return false;
	}
	
	public static String getProxyHost() throws IOException {
		checkFile();
		return p.getProperty("host-proxy");
	}
	
	public static int getProxyPort() throws IOException {
		checkFile();
		if (p.getProperty("port-proxy").length() > 0)
			return Integer.parseInt(p.getProperty("port-proxy"));
		return -1;
	}
	
	public static String getProxyUser() throws IOException {
		checkFile();
		return p.getProperty("user-proxy");
	}
	
	public static String getProxyPassword() throws IOException {
		checkFile();
		return p.getProperty("password-proxy");
	}
	
	public static void setProxy(boolean value) throws IOException {
		checkFile();
		if (value)
			p.put("use-proxy", "yes");
		else
			p.put("use-proxy", "no");
	}
	
	public static void setProxyHost(String value) throws IOException {
		checkFile();
		p.put("host-proxy", value);
	}
	
	public static void setProxyPort(int value) throws IOException {
		checkFile();
		p.put("port-proxy", Integer.toString(value));
	}
	
	public static void setProxyUser(String value) throws IOException {
		checkFile();
		p.put("user-proxy", value);
	}
	
	public static void setProxyPassword(String value) throws IOException {
		checkFile();
		p.put("password-proxy", value);
	}
}
