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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Category;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.WriterAppender;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XItemListener;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.loader.FactoryHelper;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XSingleServiceFactory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.task.XJobExecutor;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * This class create a dialog box that display more informations about a document.
 * @author Jeremy Bethmont
 */
public class MoreInfos extends WeakBase implements XServiceInfo, XJobExecutor {

	static final String __serviceName = "com.sun.star.pengyou.MoreInfos";
	
	private static Category _cat;
	
	private String _davPath = "";
	private Vector _davFiles = null;
		
	private static final String _urlName = "Url1";
	private static final String _listName = "List1";
	private XComponentContext _xComponentContext;
	
	public void LoggerMethode(){
	      Date maDate=new Date();
	      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	     
	      //Creation de notre Layout cet fois ce sera un layout HTML
	      HTMLLayout layout = new HTMLLayout();
	      WriterAppender appender = null;

	        FileOutputStream output;
			try {
				output = new FileOutputStream("FichierLog-" + dateFormat.format(maDate)   +  ".html");
				appender = new WriterAppender(layout, output);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	        

	    
	      //Category cat = Category.getInstance(TestLogger.class.getName());  méthode identique pourquoi car Logger extends Category :)
	      _cat = Category.getInstance(MoreInfos.class.getName());
	     
	      //Ajout de notre appender a root
	      //root.addAppender(appender);    les deux méthodes sont identiques
	      _cat.addAppender(appender);
	  }
	
	public MoreInfos(XComponentContext xComponentContext) {
		LoggerMethode();
		_xComponentContext = xComponentContext;
	}
	
	// static component operations
	public static XSingleServiceFactory __getServiceFactory(String implName,
			XMultiServiceFactory multiFactory,
			XRegistryKey regKey) {
		XSingleServiceFactory xSingleServiceFactory = null;
		if (implName.equals(MoreInfos.class.getName())) {
			xSingleServiceFactory = FactoryHelper.getServiceFactory(
					MoreInfos.class, MoreInfos.__serviceName, multiFactory, regKey);
		}        
		return xSingleServiceFactory;
	}
	
	public static boolean __writeRegistryServiceInfo(XRegistryKey regKey) {
		return FactoryHelper.writeRegistryServiceInfo(
				MoreInfos.class.getName(), MoreInfos.__serviceName, regKey);
	}
	
	// XServiceInfo
	public String getImplementationName() {
		return getClass().getName();
	}
	
	// XServiceInfo
	public boolean supportsService(/*IN*/String serviceName) {
		if (serviceName.equals(__serviceName))        
			return true;
		return false;
	}
	
	// XServiceInfo
	public String[] getSupportedServiceNames() {
		String[] retValue= new String[0];
		retValue[0] = __serviceName;
		return retValue;
	}
	
	// XJobExecutor
	public void trigger(String sEvent) {
		if (sEvent.compareTo("execute") == 0) {
			try {
				createDialog();
			}
			catch (Exception e) {
				throw new com.sun.star.lang.WrappedTargetRuntimeException(e.getMessage(), this, e);
			}
		}        
	}
	
	/*
	 * Method for creating a dialog at runtime
	 */
	public void createDialog() throws com.sun.star.uno.Exception {
		_cat.log(Level.DEBUG, "createDialog();");
		// Get the service manager from the component context
		XMultiComponentFactory xMultiComponentFactory = _xComponentContext.getServiceManager();
		
		// Create the dialog model and set the properties
		_cat.log(Level.DEBUG, "Create the dialog model and set the properties");
		Object dialogModel = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.UnoControlDialogModel", _xComponentContext);
		XPropertySet xPSetDialog = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, dialogModel);
		xPSetDialog.setPropertyValue("Width", new Integer(170));
		xPSetDialog.setPropertyValue("Height", new Integer(250));
		xPSetDialog.setPropertyValue("Title", new String("More Infos"));
		
		// Create the dialog control and set the model
		_cat.log(Level.DEBUG, "Create the dialog control and set the model");
		Object dialog = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.UnoControlDialog", _xComponentContext);
		XControl xControl = (XControl)UnoRuntime.queryInterface(
				XControl.class, dialog);
		XControlModel xControlModel = (XControlModel)UnoRuntime.queryInterface(
				XControlModel.class, dialogModel);      
		xControl.setModel(xControlModel);
		
		/*
		// Events...
		XControlContainer xControlCont = (XControlContainer)UnoRuntime.queryInterface(
				XControlContainer.class, dialog); 

		//updateWebDav(xControlCont);

		// Add an action listener to the prev button control
		_cat.log(Level.DEBUG, "Add an action listener to the prev button control");
		Object objectPrev = xControlCont.getControl(_prevName);
		XButton xPrev = (XButton)UnoRuntime.queryInterface(XButton.class, objectPrev);
		xPrev.addActionListener(new OnPrevClick(xControlCont));
		// Add an action listener to the combo box control
		_cat.log(Level.DEBUG, "Add an action listener to the combo box control");
		Object objectUrl = xControlCont.getControl(_urlName);
		XComboBox xUrl = (XComboBox)UnoRuntime.queryInterface(XComboBox.class, objectUrl);
		xUrl.addItemListener(new OnUrlChange(xControlCont));
		// Add an action listener to the list box control
		_cat.log(Level.DEBUG, "Add an action listener to the list box control");
		Object objectList = xControlCont.getControl(_listName);
		XListBox xList = (XListBox)UnoRuntime.queryInterface(XListBox.class, objectList);
		xList.addActionListener(new OnListDblClick(xControlCont));
		// Add an action listener to the More Infos button control
		_cat.log(Level.DEBUG, "Add an action listener to the More Infos button control");
		Object objectMoreInfos = xControlCont.getControl(_moreName);
		XButton xMoreInfos = (XButton)UnoRuntime.queryInterface(XButton.class, objectMoreInfos);
		xMoreInfos.addActionListener(new OnMoreInfosClick(xControlCont));
		// Add an action listener to the Ok button control
		_cat.log(Level.DEBUG, "Add an action listener to the Ok button control");
		Object objectOk = xControlCont.getControl(_okName);
		XButton xOk = (XButton)UnoRuntime.queryInterface(XButton.class, objectOk);
		xOk.addActionListener(new OnOkClick(xControlCont));
		// Add an action listener to the Cancel button control
		_cat.log(Level.DEBUG, "Add an action listener to the Cancel button control");
		Object objectCancel = xControlCont.getControl(_cancelName);
		XButton xCancel = (XButton)UnoRuntime.queryInterface(XButton.class, objectCancel);
		xCancel.addActionListener(new OnCancelClick(xControlCont));
		*/
		// Create a peer
		_cat.log(Level.DEBUG, "Create a peer");
		Object toolkit = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.ExtToolkit", _xComponentContext);      
		XToolkit xToolkit = (XToolkit)UnoRuntime.queryInterface(
				XToolkit.class, toolkit);
		XWindow xWindow = (XWindow)UnoRuntime.queryInterface(
				XWindow.class, xControl);
		xWindow.setVisible(false);      
		xControl.createPeer(xToolkit, null);
		
		// Execute the dialog
		_cat.log(Level.DEBUG, "Execute the dialog");
		XDialog xDialog = (XDialog)UnoRuntime.queryInterface(
				XDialog.class, dialog);
		xDialog.execute();
		
		// Dispose the dialog
		_cat.log(Level.DEBUG, "Dispose the dialog");
		XComponent xComponent = (XComponent)UnoRuntime.queryInterface(
				XComponent.class, dialog);
		xComponent.dispose();
	}
	
		
	public void updateWebDav(XControlContainer c) {
		UpdateWebDav t = new UpdateWebDav(c);
		t.start();
	}
	

	/*
	 * OnPrevClick
	 */
	public class OnPrevClick implements XActionListener {
		
		private XControlContainer _xControlCont;
		
		public OnPrevClick(XControlContainer xControlCont) {
			_xControlCont = xControlCont;
		}
		
		// XEventListener
		public void disposing(EventObject eventObject) {
			_xControlCont = null;
		}
		
		// XActionListener
		public void actionPerformed(ActionEvent actionEvent) {
			_cat.log(Level.DEBUG, "-> OnPrevClick");
			int i = _davPath.lastIndexOf('/');
			if (i == 0 || i == -1)
				_davPath = "/";
			else
				_davPath = _davPath.substring(0, i);
			updateWebDav(_xControlCont);
		}
	}
	
	/*
	 * OnUrlChange
	 */
	public class OnUrlChange implements XItemListener {

		private XControlContainer _xControlCont;
		
		public OnUrlChange(XControlContainer xControlCont) {
			_xControlCont = xControlCont;
		}
		
		// XEventListener
		public void disposing(EventObject eventObject) {
			_xControlCont = null;
		}
		
		public void itemStateChanged(ItemEvent arg0) {
			
			XTextComponent t = (XTextComponent)UnoRuntime.queryInterface(
					XTextComponent.class, _xControlCont.getControl(_urlName));
			_davPath = t.getText();
			updateWebDav(_xControlCont);
		}

	}
	
	/*
	 * OnListDblClick
	 */
	public class OnListDblClick implements XActionListener {
		
		private XControlContainer _xControlCont;
		
		public OnListDblClick(XControlContainer xControlCont) {
			_xControlCont = xControlCont;
		}
		
		// XEventListener
		public void disposing(EventObject eventObject) {
			_xControlCont = null;
		}
		
		// XActionListener
		public void actionPerformed(ActionEvent actionEvent) {
			XListBox x = (XListBox)UnoRuntime.queryInterface(
					XListBox.class, _xControlCont.getControl(_listName));
			_cat.log(Level.DEBUG, "-> OnListDblClick");
			
			String[] s = ((String[])_davFiles.elementAt(x.getSelectedItemPos()));
			
			if (s[2] == "COLLECTION")
			{
				_davPath += "/" + s[4];
				updateWebDav(_xControlCont);
			}
		}
	}
	
	/*
	 * OnMoreInfosClick
	 */
	public class OnMoreInfosClick implements XActionListener {
		
		//private XControlContainer _xControlCont;
		
		public OnMoreInfosClick(XControlContainer xControlCont) {
			//_xControlCont = xControlCont;
		}
		
		// XEventListener
		public void disposing(EventObject eventObject) {
			//_xControlCont = null;
		}
		
		// XActionListener
		public void actionPerformed(ActionEvent actionEvent) {
			int t;
			
			t = 0;
			if (t == 0)
				t = 1;
		}
	}
	
	/*
	 * OnOkClick
	 */
	public class OnOkClick implements XActionListener {
		
		//private XControlContainer _xControlCont;
		
		public OnOkClick(XControlContainer xControlCont) {
			//_xControlCont = xControlCont;
		}
		
		// XEventListener
		public void disposing(EventObject eventObject) {
			//_xControlCont = null;
		}
		
		// XActionListener
		public void actionPerformed(ActionEvent actionEvent) {
		}
	}	
	
	/*
	 * OnCancelClick
	 */
	public class OnCancelClick implements XActionListener {
		
		//private XControlContainer _xControlCont;
		
		public OnCancelClick(XControlContainer xControlCont) {
			//_xControlCont = xControlCont;
		}
		
		// XEventListener
		public void disposing(EventObject eventObject) {
			//_xControlCont = null;
		}
		
		// XActionListener
		public void actionPerformed(ActionEvent actionEvent) {
		}
	}
	
	class Comparer implements Comparator {
        public int compare(Object obj1, Object obj2)
        {
        	String[] s1 = (String[])obj2;
        	String[] s2 = (String[])obj1;

        	if (s1[2] == "COLLECTION" && s2[2] != "COLLECTION")
        		return 1;
        	if (s2[2] == "COLLECTION" && s1[2] != "COLLECTION")
        		return 0;
        	_cat.log(Level.DEBUG, "SORT : \"" + s2[4] + "\".compareToIgnoreCase(\"" + s1[4] + "\") = " + String.valueOf(s2[4].compareToIgnoreCase(s1[4])));
        	return s2[4].compareToIgnoreCase(s1[4]);
        }
	}

	private class UpdateWebDav implements Runnable {

		private Thread thread;
		public UpdateWebDav(XControlContainer c) {
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
		
		public void run() {
					}
	}
}