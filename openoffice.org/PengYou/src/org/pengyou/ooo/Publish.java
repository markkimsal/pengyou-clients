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
package org.pengyou.ooo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.pengyou.ooo.utils.Settings;
import org.pengyou.ooo.utils.WebDavStore;

import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.lang.ArrayIndexOutOfBoundsException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public class Publish {
	
	private static Logger log = Logger.getLogger(Publish.class);
	
	private XComponentContext m_xComponentContext;
	
	private Object m_oDesktop;
	private XDesktop m_xDesktop;
	private XComponent m_xComponent;
	private XStorable m_xStorable;
	private XDocumentInfo m_xDocumentInfo;
	private XModel m_xModel;
	
	/**
	 * Publish constructor
	 * @param XComponentContext xComponentContext
	 */
	public Publish(XComponentContext xComponentContext) {
		m_xComponentContext = xComponentContext;
		
		XMultiComponentFactory xMultiComponentFactory = m_xComponentContext.getServiceManager();
		
		try {
			m_oDesktop = xMultiComponentFactory.createInstanceWithContext(
					"com.sun.star.frame.Desktop", m_xComponentContext);
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());
		}
		
		m_xDesktop = (XDesktop)UnoRuntime.queryInterface(
	            XDesktop.class, m_oDesktop);

		m_xComponent = m_xDesktop.getCurrentComponent();
		
		
		m_xStorable = (XStorable)UnoRuntime.queryInterface(
	            XStorable.class, m_xComponent);
			
		XDocumentInfoSupplier xDocumentInfoSupplier =
			(XDocumentInfoSupplier)UnoRuntime.queryInterface(
					XDocumentInfoSupplier.class, m_xComponent);
		
		m_xDocumentInfo = xDocumentInfoSupplier.getDocumentInfo();
		
		m_xModel = (XModel)UnoRuntime.queryInterface(XModel.class,
				m_xComponent);
	}
	
	public boolean checkDoc()
	{
		try {
			if (m_xDocumentInfo.getUserFieldName((short) 0).compareTo("PengYouDocument") == 0 &&
					m_xDocumentInfo.getUserFieldValue((short) 0).compareTo("true") == 0 &&
					m_xDocumentInfo.getUserFieldName((short) 1).compareTo("PengYouLocation") == 0 &&
					m_xDocumentInfo.getUserFieldValue((short) 1).length() > 0)
				return (true);
		} catch (ArrayIndexOutOfBoundsException e) {
			log.debug(e.getLocalizedMessage());
		}
		return (false);
	}
	
	public void storeLocal()
	{
		try {
			m_xStorable.store();
		} catch (IOException e) {
			log.debug(e.getLocalizedMessage());
		}
	}
	
	@SuppressWarnings("deprecation")
	public void storeRemote() throws java.io.IOException
	{
		String localFilePath = null;
		String remotePath = null;
		String remoteFile = null;
		
		localFilePath = URLDecoder.decode(m_xModel.getURL().replaceFirst("file:///", ""));
		
		try {
			remotePath = m_xDocumentInfo.getUserFieldValue((short) 1);
			remoteFile = m_xDocumentInfo.getUserFieldValue((short) 2);
		} catch (ArrayIndexOutOfBoundsException e) {
			log.error(e.getLocalizedMessage());
		}
		if (remotePath != null &&
				localFilePath != null &&
				remoteFile != null)
		{
			WebDavStore dav = new WebDavStore();
			dav.setConnectionUrl(Settings.getServer());
			dav.setConnectionPort(Settings.getPort());
			dav.setConnectionBaseDirectory(remotePath);
			dav.setConnectionUsername(Settings.getUser());
			dav.setConnectionPassword(Settings.getPassword());
			
			dav.connect(remoteFile);

			try {
				dav.upload(new FileInputStream(localFilePath));
				//dav.putMethod(new String(remotePath + "/" + remoteFile), new FileInputStream(localFilePath));
			} catch (FileNotFoundException e) {
				log.error(e.getMessage());
			} catch (java.io.IOException e) {
				log.error(e.getMessage());
			}
		}
		else
		{
			log.error("path = null");
		}
	}
}
