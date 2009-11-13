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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class DownloadDoc implements Runnable {
	
	private static Logger log = Logger.getLogger(DownloadDoc.class);
	
	private Thread m_thread;
	private WebDavStore m_dav;
	private long m_totalLength = 0;
	private long m_actualLength = 0;
	private String m_dst = null;
	
	
	public DownloadDoc(WebDavStore dav, String dst) {
		m_dav = dav;
		m_dst = dst;
		m_thread = new Thread(this);
	}
	
	public long getTotalLength() {
		return (m_totalLength);
	}
	
	public long getActualLength() {
		return (m_actualLength);
	}
	
	public String getFilePath() {
		return (m_dst);
	}
	
	@SuppressWarnings("deprecation")
	public String getUeFilePath() {
		return (URLEncoder.encode(m_dst));
	}
	
	public void start() {
		m_thread.start();
	}
	
	public void stop() {
		
	}
	
	public void join() {
		try {
			m_thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	public void run() {
		InputStream st = null;
		
		try {
			st = m_dav.getContentAsStream();
			m_totalLength = m_dav.getContentLength();
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error(e1.getMessage());
		}
		try
		{
			FileOutputStream fos = new FileOutputStream(m_dst);
			byte buffer[] = new byte[1024];
			int cc = 0;
			while ((cc = st.read(buffer, 0, 1024)) != -1)
			{
				m_actualLength += cc;
				//log.info("fos.write(buffer, 0, " + cc + ");");
				fos.write(buffer, 0, cc);
			}
			st.close();
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			log.log(Level.DEBUG, e);
		}
		catch (IOException e)
		{
			log.log(Level.DEBUG, e);
		}
	}

}
