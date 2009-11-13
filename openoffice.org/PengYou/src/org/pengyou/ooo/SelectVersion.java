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

import java.io.IOException;
import java.util.Comparator;
import java.util.Vector;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pengyou.ooo.utils.DownloadDoc;
import org.pengyou.ooo.utils.LocalFileSystem;
import org.pengyou.ooo.utils.Settings;
import org.pengyou.ooo.utils.WebDavStore;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XProgressBar;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.lang.ArrayIndexOutOfBoundsException;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * This class create a dialog box that allow the user to open a remote document.
 * @author Jeremy Bethmont
 */
public class SelectVersion {
	
	private static final String _compName = "compName";
	private static final String _okName = "okName";
	private static final String _cancelName = "cancelName";

	private static final String _containerVersionName = "containerVersionName";
	private static final String _containerCommentName = "containerCommentName";
	
	private static final String _listVersionName = "listVersionName";
	private static final String _listCommentName = "listCommentName";
	private static final String _progressName = "progressName";

	private String filePath = null;
	private String fileName = null;
	
	private Hashtable table = null;
	
	private XComponentContext _xComponentContext;
	
	private Object _oDesktop;
	private XDesktop _xDesktop;
	private XComponent _xComponent;
	private XDocumentInfo _xDocumentInfo;
	private XModel _xModel;
	
	private XDialog xDialog = null;

	private static Logger log = Logger.getLogger(SelectVersion.class);
	
	/**
	 * OpenDialog constructor
	 * @param XComponentContext xComponentContext
	 * @param String davServer
	 * @param int davPort
	 * @param String davPath
	 * @param String davUser
	 * @param String davPass
	 * @return OpenDialog
	 * @throws IOException 
	 */
	public SelectVersion(XComponentContext xComponentContext) throws IOException {
		table= new Hashtable();
		_xComponentContext = xComponentContext;
		Settings.load();
		
		XMultiComponentFactory xMultiComponentFactory = _xComponentContext.getServiceManager();
		
		try {
			_oDesktop = xMultiComponentFactory.createInstanceWithContext(
					"com.sun.star.frame.Desktop", _xComponentContext);
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());
		}
		
		_xDesktop = (XDesktop)UnoRuntime.queryInterface(
	            XDesktop.class, _oDesktop);

		_xComponent = _xDesktop.getCurrentComponent();
		
		XDocumentInfoSupplier xDocumentInfoSupplier =
			(XDocumentInfoSupplier)UnoRuntime.queryInterface(
					XDocumentInfoSupplier.class, _xComponent);
		
		_xDocumentInfo = xDocumentInfoSupplier.getDocumentInfo();
		
		_xModel = (XModel)UnoRuntime.queryInterface(XModel.class,
				_xComponent);
		
		
		//XComponent xcomponent = (XComponent) UnoRuntime.queryInterface( XComponent.class, _xComponent );
        //XCloseable xcloseable = (XCloseable) UnoRuntime.queryInterface( XCloseable.class, _xComponent );
	}

	public boolean checkDoc()
	{
		try {
			if (_xDocumentInfo.getUserFieldName((short) 0).compareTo("PengYouDocument") == 0 &&
					_xDocumentInfo.getUserFieldValue((short) 0).compareTo("true") == 0 &&
					_xDocumentInfo.getUserFieldName((short) 1).compareTo("PengYouLocation") == 0 &&
					_xDocumentInfo.getUserFieldValue((short) 1).length() > 0)
				return (true);
		} catch (ArrayIndexOutOfBoundsException e) {
			log.debug(e.getLocalizedMessage());
		}
		return (false);
	}

	/**
	 * Method for creating a the dialog box
	 * @throws IOException 
	 */
	public void createDialog() throws com.sun.star.uno.Exception, IOException {
		XMultiComponentFactory xMultiComponentFactory = _xComponentContext.getServiceManager();
		Object dialogModel = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.UnoControlDialogModel", _xComponentContext);
		XPropertySet xPSetDialog = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, dialogModel);
		xPSetDialog.setPropertyValue("Width", new Integer(260));
		xPSetDialog.setPropertyValue("Height", new Integer(280));
		xPSetDialog.setPropertyValue("Title", new String("Open at a previous version"));
		XMultiServiceFactory xMultiServiceFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
				XMultiServiceFactory.class, dialogModel);
		
		/*
		 * Server Container
		 */
		Object containerServerModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetServerContainerDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, containerServerModel);
		xPSetServerContainerDesc.setPropertyValue("PositionX", new Integer(10));
		xPSetServerContainerDesc.setPropertyValue("PositionY", new Integer(15));
		xPSetServerContainerDesc.setPropertyValue("Width", new Integer(70));
		xPSetServerContainerDesc.setPropertyValue("Height", new Integer(10));
		xPSetServerContainerDesc.setPropertyValue("Align", new Short((short)0));
		xPSetServerContainerDesc.setPropertyValue("Name", _containerVersionName);
		xPSetServerContainerDesc.setPropertyValue("TabIndex", new Short((short)0));
		//xPSetServerContainerDesc.setPropertyValue("FontDescriptor", ((new FontDescriptor()).Strikeout = 4));
		xPSetServerContainerDesc.setPropertyValue("Label", new String("Versions: "));
		
		/*
		 * Version List
		 */
		Object listModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlListBoxModel");
		XPropertySet xPSetList = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, listModel);
		xPSetList.setPropertyValue("PositionX", new Integer(15));
		xPSetList.setPropertyValue("PositionY", new Integer(30));
		xPSetList.setPropertyValue("Width", new Integer(230));
		xPSetList.setPropertyValue("Height", new Integer(120));
		xPSetList.setPropertyValue("Name", _listVersionName);
		xPSetList.setPropertyValue("TabIndex", new Short((short)3));
		
		/*
		 * Comment Container
		 */
		Object containerProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetProxyContainerDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, containerProxyModel);
		xPSetProxyContainerDesc.setPropertyValue("PositionX", new Integer(10));
		xPSetProxyContainerDesc.setPropertyValue("PositionY", new Integer(165));
		xPSetProxyContainerDesc.setPropertyValue("Width", new Integer(70));
		xPSetProxyContainerDesc.setPropertyValue("Height", new Integer(10));
		xPSetProxyContainerDesc.setPropertyValue("Align", new Short((short)0));
		xPSetProxyContainerDesc.setPropertyValue("Name", _containerCommentName);
		xPSetProxyContainerDesc.setPropertyValue("TabIndex", new Short((short)0));
		//xPSetServerContainerDesc.setPropertyValue("FontDescriptor", ((new FontDescriptor()).Strikeout = 4));
		xPSetProxyContainerDesc.setPropertyValue("Label", new String("Comment: "));
		
		/*
		 * Comment List
		 */
		Object commentListModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetCommentList = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, commentListModel);
		xPSetCommentList.setPropertyValue("PositionX", new Integer(15));
		xPSetCommentList.setPropertyValue("PositionY", new Integer(180));
		xPSetCommentList.setPropertyValue("Width", new Integer(230));
		xPSetCommentList.setPropertyValue("Height", new Integer(60));
		xPSetCommentList.setPropertyValue("Name", _listCommentName);
		xPSetCommentList.setPropertyValue("TabIndex", new Short((short)3));
		xPSetCommentList.setPropertyValue("MultiLine", new Boolean("true"));
		xPSetCommentList.setPropertyValue("Text", new String("Not avaible yet..."));
		
		/*
		 * Compare Button
		 */
		Object compModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetComp = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, compModel);
		xPSetComp.setPropertyValue("PositionX", new Integer(10));
		xPSetComp.setPropertyValue("PositionY", new Integer(256));
		xPSetComp.setPropertyValue("Width", new Integer(80));
		xPSetComp.setPropertyValue("Height", new Integer(14));
		xPSetComp.setPropertyValue("Name", _compName);
		xPSetComp.setPropertyValue("TabIndex", new Short((short)4));
		xPSetComp.setPropertyValue("Label", new String("Compare with this version"));
		
		/*
		 * Progress Bar
		 */
		Object progressModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlProgressBarModel");
		XPropertySet xPSetProgress = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, progressModel);
		xPSetProgress.setPropertyValue("PositionX", new Integer(78));
		xPSetProgress.setPropertyValue("PositionY", new Integer(256));
		xPSetProgress.setPropertyValue("Width", new Integer(51));
		xPSetProgress.setPropertyValue("Height", new Integer(14));
		xPSetProgress.setPropertyValue("Name", _progressName);
		xPSetProgress.setPropertyValue("TabIndex", new Short((short)5));
		
		/*
		 * Ok Button
		 */
		log.log(Level.DEBUG, "Create the OK button model and set the properties");
		Object okModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetOk = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, okModel);
		xPSetOk.setPropertyValue("PositionX", new Integer(169));
		xPSetOk.setPropertyValue("PositionY", new Integer(256));
		xPSetOk.setPropertyValue("Width", new Integer(32));
		xPSetOk.setPropertyValue("Height", new Integer(14));
		xPSetOk.setPropertyValue("Name", _okName);
		xPSetOk.setPropertyValue("TabIndex", new Short((short)6));
		xPSetOk.setPropertyValue("Label", new String("Open"));
		
		/*
		 * Cancel Button
		 */
		Object cancelModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetCancel = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, cancelModel);
		xPSetCancel.setPropertyValue("PositionX", new Integer(207));
		xPSetCancel.setPropertyValue("PositionY", new Integer(256));
		xPSetCancel.setPropertyValue("Width", new Integer(42));
		xPSetCancel.setPropertyValue("Height", new Integer(14));
		xPSetCancel.setPropertyValue("Name", _cancelName);
		xPSetCancel.setPropertyValue("TabIndex", new Short((short)7));
		xPSetCancel.setPropertyValue("PushButtonType", new Short((short)2));
		xPSetCancel.setPropertyValue("Label", new String("Cancel"));
		
		XNameContainer xNameCont = (XNameContainer)UnoRuntime.queryInterface(
				XNameContainer.class, dialogModel);
		xNameCont.insertByName(_containerVersionName, containerServerModel);
		xNameCont.insertByName(_containerCommentName, containerProxyModel);
		xNameCont.insertByName(_listVersionName, listModel);
		xNameCont.insertByName(_listCommentName, commentListModel);
		xNameCont.insertByName(_compName, compModel);
		xNameCont.insertByName(_progressName, progressModel);
		xNameCont.insertByName(_okName, okModel);
		xNameCont.insertByName(_cancelName, cancelModel);
		Object dialog = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.UnoControlDialog", _xComponentContext);
		XControl xControl = (XControl)UnoRuntime.queryInterface(
				XControl.class, dialog);
		XControlModel xControlModel = (XControlModel)UnoRuntime.queryInterface(
				XControlModel.class, dialogModel);      
		xControl.setModel(xControlModel);
		
		/*
		 * Events
		 */
		XControlContainer xControlCont = (XControlContainer)UnoRuntime.queryInterface(
				XControlContainer.class, dialog);

		updateVersions(xControlCont);
		
		Object objectComp = xControlCont.getControl(_compName);
		XButton xComp = (XButton)UnoRuntime.queryInterface(XButton.class, objectComp);
		xComp.addActionListener(new OnCompClick(xControlCont));
		
		Object objectOk = xControlCont.getControl(_okName);
		XButton xOk = (XButton)UnoRuntime.queryInterface(XButton.class, objectOk);
		xOk.addActionListener(new OnOkClick(xControlCont));
		
		Object toolkit = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.ExtToolkit", _xComponentContext);      
		XToolkit xToolkit = (XToolkit)UnoRuntime.queryInterface(
				XToolkit.class, toolkit);
		XWindow xWindow = (XWindow)UnoRuntime.queryInterface(
				XWindow.class, xControl);
		xWindow.setVisible(false);      
		xControl.createPeer(xToolkit, null);
		xDialog = (XDialog)UnoRuntime.queryInterface(
				XDialog.class, dialog);
		xDialog.execute();
		XComponent xComponent = (XComponent)UnoRuntime.queryInterface(
				XComponent.class, dialog);
		xComponent.dispose();
	}
	
	private void updateVersions(XControlContainer c) {
		TimeOut to = new TimeOut(c);
		UpdateVersions t = new UpdateVersions(c, to);
		t.start();
	}

	/**
	 * OnCompClick - Called when the user press the "Compare" button
	 */
	public class OnCompClick implements XActionListener {
		
		public OnCompClick(XControlContainer xControlCont) {
		}
		
		public void disposing(EventObject eventObject) {
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
	
		}
	}
	
	/**
	 * OnOkClick - Called when the user press the "Ok" button
	 */
	public class OnOkClick implements XActionListener {
		
		private XControlContainer _xControlCont;
		
		public OnOkClick(XControlContainer xControlCont) {
			_xControlCont = xControlCont;
		}
		
		public void disposing(EventObject eventObject) {
			_xControlCont = null;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			XListBox x = (XListBox)UnoRuntime.queryInterface(
					XListBox.class, _xControlCont.getControl(_listVersionName));
			String version = x.getSelectedItem();
			
			String remotePath = null;
			String remoteFile = null;
			try {
				remotePath = _xDocumentInfo.getUserFieldValue((short) 1);
				remoteFile = _xDocumentInfo.getUserFieldValue((short) 2);
			} catch (ArrayIndexOutOfBoundsException e1) {
				e1.printStackTrace();
			}
			/*
			xDialog.endExecute();
			try {
				if (xcloseable != null)
					xcloseable.close(false);
				else
					xcomponent.dispose();
					
			} catch (CloseVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			WebDavStore dav = new WebDavStore();
			try {
				dav.setConnectionUrl(Settings.getServer());
				dav.setConnectionPort(Settings.getPort());
				dav.setConnectionUsername(Settings.getUser());
				dav.setConnectionPassword(Settings.getPassword());
				String path = (String)table.get(version);
				dav.setConnectionBaseDirectory(path);
				dav.connect("/");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			DownloadDoc dl = new DownloadDoc(dav, LocalFileSystem.getLocalPath(remotePath, version + "-" + remoteFile));
			dl.start();
			
			while (dl.getTotalLength() == 0 || dl.getActualLength() < dl.getTotalLength())
			{
				log.info("VersionDialog : DL " + dl.getActualLength() + " sur " + dl.getTotalLength());
			}
			
			try {
				loadFromFile(dl.getFilePath(), version);
			} catch (com.sun.star.io.IOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

			
		}
	}
	
	private class UpdateVersions implements Runnable {

		private Thread thread;
		private XControlContainer m_c;
		private TimeOut m_to;
		
		public UpdateVersions(XControlContainer c, TimeOut to) {
			m_c = c;
			m_to = to;
			thread = new Thread(this);
		}
		
		public void start() {
			thread.start();
		}
		
		public void join() {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		@SuppressWarnings("unchecked")
		public void run() {
		
			// Lancement du timeout
			m_to.start();
			
			// Disable controls
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_containerVersionName))).setEnable(false);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_containerCommentName))).setEnable(false);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_listVersionName))).setEnable(false);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_listCommentName))).setEnable(false);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_okName))).setEnable(false);
			
			
			// Mise a jour de la ListBox
			XListBox x = (XListBox)UnoRuntime.queryInterface(
					XListBox.class, m_c.getControl(_listVersionName));
			
			x.removeItems((short)0, x.getItemCount());
			
			String localFilePath = null;
			String remotePath = null;
			String remoteFile = null;
			
			try {
				localFilePath = URIUtil.decode(_xModel.getURL().replaceFirst("file:///", ""));
				remotePath = _xDocumentInfo.getUserFieldValue((short) 1);
				remoteFile = _xDocumentInfo.getUserFieldValue((short) 2);
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
					Vector myVersions = dav.getMyVersions();
					Vector<String[]> version = myVersions;
					//Collections.sort(version, new CompareVersions());
					short i = 0;
					for (String[] v : version) {
						if (v[0].indexOf("jcr:rootVersion") == -1) {
							table.put(v[0], v[3]);
							x.addItem(v[0], i++);
						}
					}

					/*
					Vector v = dav.getVersions();
					
					for (Object o : v) {
						String s = (String)o;
						x.addItem(s, i++);
					}
					*/
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				log.error(e.getLocalizedMessage());
			} catch (URIException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			x.makeVisible((short) 0);
			
			// Enable Controls
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_containerVersionName))).setEnable(true);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_containerCommentName))).setEnable(true);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_listVersionName))).setEnable(true);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_listCommentName))).setEnable(true);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_okName))).setEnable(true);
			
			// Fin du timeout
			m_to.stop();
		}
	}
	
private class TimeOut implements Runnable {
		
		private Thread thread;
		private XControlContainer m_c;
		private boolean end = false;
		
		public TimeOut(XControlContainer c) {
			m_c = c;
			thread = new Thread(this);
		}
		
		public void start() {
			thread.start();
		}
		
		public void join() {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void stop() {
			end = true;
		}
		
		public void run() {
			int timeout = 30;
			
			for (int i = 0; i < 100; i += (100/timeout)) {
				try {
					for (int j = 0; j < 10; j++)
					{
						if (end == true)
						{
							((XWindow)UnoRuntime.queryInterface(
									XWindow.class, m_c.getControl(_progressName))).setVisible(false);
							return ;
						}
						Thread.sleep(100);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				((XWindow)UnoRuntime.queryInterface(
						XWindow.class, m_c.getControl(_progressName))).setVisible(true);
				XProgressBar p = (XProgressBar)UnoRuntime.queryInterface(
						XProgressBar.class, m_c.getControl(_progressName));
				p.setValue(i);
			}
		}
	}

	class CompareVersions implements Comparator {
	    public int compare(Object obj1, Object obj2)
	    {
	    	//String[] s1 = (String[])obj2;
	    	//String[] s2 = (String[])obj1;
	
	    	return 1;
	    	/*
	    	float a = Float.parseFloat(s1[0]);
	    	float b = Float.parseFloat(s2[0]);
	    	
	    	if (a > b)
	    		return 1;
	   		return 0;
	   		*/
	    }
	}
	
	private XComponent loadFromFile(String url, String version) throws IOException, com.sun.star.io.IOException, IllegalArgumentException
	{
		PropertyValue[] loadProps = new PropertyValue[1];
		PropertyValue asTemplate = new PropertyValue();
		//asTemplate.Name = "InputStream";
		//asTemplate.Value = inputStreamToXInputStream(iStream);
		loadProps[0] = asTemplate;
		
		// Create a blank writer document
		XMultiComponentFactory xMultiComponentFactory = _xComponentContext.getServiceManager();
		Object oDesktop;
		try {
			oDesktop = xMultiComponentFactory.createInstanceWithContext(
					"com.sun.star.frame.Desktop", _xComponentContext);
			XComponentLoader oComponentLoader =
				(com.sun.star.frame.XComponentLoader)
				com.sun.star.uno.UnoRuntime.queryInterface(
						com.sun.star.frame.XComponentLoader.class,
						oDesktop);
			
			String path = com.sun.star.uri.ExternalUriReferenceTranslator.
		    					create(_xComponentContext).translateToInternal("file:///" + url.replace("\\", "/"));
			if (path.length() == 0 && url.length() != 0) {
				throw new RuntimeException();
			}
			
			XComponent xComponent =
				oComponentLoader.loadComponentFromURL(
						path,
						"_default",
						0,
						loadProps);
			
			XDocumentInfoSupplier xDocumentInfoSupplier =
				(XDocumentInfoSupplier)UnoRuntime.queryInterface(
						XDocumentInfoSupplier.class, xComponent);
			XDocumentInfo xDocumentInfo = xDocumentInfoSupplier.getDocumentInfo();
			
			filePath = _xDocumentInfo.getUserFieldValue((short) 1);
			fileName = _xDocumentInfo.getUserFieldValue((short) 2);
			
			xDocumentInfo.setUserFieldName((short) 0, "PengYouDocument");
			xDocumentInfo.setUserFieldValue((short) 0, "true");
			
			xDocumentInfo.setUserFieldName((short) 1, "PengYouLocation");
			xDocumentInfo.setUserFieldValue((short) 1, filePath);
			
			xDocumentInfo.setUserFieldName((short) 2, "PengYouFile");
			xDocumentInfo.setUserFieldValue((short) 2, fileName);
			
			/*
			xDocumentInfo.setUserFieldName((short) 3, "PengYouFileVersion");
			xDocumentInfo.setUserFieldValue((short) 3, version);
			*/
			
			xDialog.endExecute();
			
			return (xComponent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
	