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

import org.apache.log4j.Logger;

import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XProgressBar;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public class ProgressBox implements Runnable {

	private XComponentContext m_xComponentContext;
	private XDialog _xDialog;
	private String m_title;
	private static final String _progressName = "progress0";
	private static final String _labelStatusName = "status0";
	private static final String _cancelName = "cancel0";
	private XControlContainer _xControlCont;
	
	private static Logger log = Logger.getLogger(ProgressBox.class);
	
	private Thread thread;
	public void start() {
		thread.start();
	}
	
	@SuppressWarnings("deprecation")
	public void stop() {
		if (_xDialog != null) {
			_xDialog.endExecute();
		}
		thread.stop();
	}
	
	public void join() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	public void run() {
		try {
			createDialog();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}		
	}
	
	public ProgressBox(XComponentContext xComponentContext, String title)
	{
		m_xComponentContext = xComponentContext;
		m_title = title;
		thread = new Thread(this);
	}
	
	private void createDialog() throws Exception
	{
		XMultiComponentFactory xMultiComponentFactory = m_xComponentContext.getServiceManager();
			
		// Create the dialog model and set the properties
		Object dialogModel = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.UnoControlDialogModel", m_xComponentContext);
		XPropertySet xPSetDialog = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, dialogModel);
		xPSetDialog.setPropertyValue("Width", new Integer(195));
		xPSetDialog.setPropertyValue("Height", new Integer(75));
		xPSetDialog.setPropertyValue("Title", m_title);

	
		// Get the service manager from the dialog model
		XMultiServiceFactory xMultiServiceFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
				XMultiServiceFactory.class, dialogModel);
		
		// Status
		Object labelStatusModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetStatusDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelStatusModel);
		xPSetStatusDesc.setPropertyValue("PositionX", new Integer(10));
		xPSetStatusDesc.setPropertyValue("PositionY", new Integer(10));
		xPSetStatusDesc.setPropertyValue("Width", new Integer(175));
		xPSetStatusDesc.setPropertyValue("Height", new Integer(10));
		xPSetStatusDesc.setPropertyValue("Align", new Short((short)0));
		xPSetStatusDesc.setPropertyValue("Name", _labelStatusName);
		xPSetStatusDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetStatusDesc.setPropertyValue("Label", new String(""));
		
		// Create the Progress bar and set the properties
		Object progressModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlProgressBarModel");
		XPropertySet xPSetProgress = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, progressModel);
		xPSetProgress.setPropertyValue("PositionX", new Integer(10));
		xPSetProgress.setPropertyValue("PositionY", new Integer(25));
		xPSetProgress.setPropertyValue("Width", new Integer(175));
		xPSetProgress.setPropertyValue("Height", new Integer(14));
		xPSetProgress.setPropertyValue("Name", _progressName);
		xPSetProgress.setPropertyValue("TabIndex", new Short((short)1));

		// Create the Cancel button model and set the properties
		Object cancelModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetCancel = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, cancelModel);
		xPSetCancel.setPropertyValue("PositionX", new Integer(143));
		xPSetCancel.setPropertyValue("PositionY", new Integer(51));
		xPSetCancel.setPropertyValue("Width", new Integer(42));
		xPSetCancel.setPropertyValue("Height", new Integer(14));
		xPSetCancel.setPropertyValue("Name", _cancelName);
		xPSetCancel.setPropertyValue("TabIndex", new Short((short)2));
		xPSetCancel.setPropertyValue("PushButtonType", new Short((short)2));
		xPSetCancel.setPropertyValue("Label", new String("Cancel"));
		
        // Insert the control models into the dialog model
		XNameContainer xNameCont = (XNameContainer)UnoRuntime.queryInterface(
				XNameContainer.class, dialogModel);
		xNameCont.insertByName(_progressName, progressModel);
		xNameCont.insertByName(_labelStatusName, labelStatusModel);
		xNameCont.insertByName(_cancelName, cancelModel);

		// Create the dialog control and set the model
		Object dialog = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.UnoControlDialog", m_xComponentContext);
		XControl xControl = (XControl)UnoRuntime.queryInterface(
				XControl.class, dialog);
		XControlModel xControlModel = (XControlModel)UnoRuntime.queryInterface(
				XControlModel.class, dialogModel);      
		xControl.setModel(xControlModel);
		
        //	Events handling
		_xControlCont = (XControlContainer)UnoRuntime.queryInterface(
				XControlContainer.class, dialog); 
		
		// Create a peer
		Object toolkit = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.ExtToolkit", m_xComponentContext);      
		XToolkit xToolkit = (XToolkit)UnoRuntime.queryInterface(
				XToolkit.class, toolkit);
		XWindow xWindow = (XWindow)UnoRuntime.queryInterface(
				XWindow.class, xControl);
		xWindow.setVisible(false);
		xControl.createPeer(xToolkit, null);
		
		// Execute the dialog
		_xDialog = (XDialog)UnoRuntime.queryInterface(
				XDialog.class, dialog);
		_xDialog.execute();

		// Dispose the dialog
		XComponent xComponent = (XComponent)UnoRuntime.queryInterface(
				XComponent.class, dialog);
		xComponent.dispose();
	}
	
	public boolean update(String description, int percent)
	{
		if (_xControlCont.getControl(_progressName) == null)
			return false;
		XProgressBar pb = (XProgressBar)UnoRuntime.queryInterface(
				XProgressBar.class, _xControlCont.getControl(_progressName));
		XFixedText l = (XFixedText)UnoRuntime.queryInterface(
				XFixedText.class, _xControlCont.getControl(_labelStatusName));
		pb.setValue(percent);
		l.setText(description);
		log.info(description + " -> " + percent + "%");
		return true;
	}
}
