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

import org.pengyou.ooo.utils.Ressources;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * This class create a dialog box that allow the user to open a remote document.
 * @author Jeremy Bethmont
 */
public class AboutBox {

	private static final String _okName = "okName";
	private static final String _imageName = "imageName";
	private static final String _labelAboutName = "labelAboutName";


	private XComponentContext _xComponentContext;
	
	private XDialog xDialog = null;
	
	
	public AboutBox(XComponentContext xComponentContext) {
		_xComponentContext = xComponentContext;
	}
	

	public void createDialog() throws com.sun.star.uno.Exception {
		XMultiComponentFactory xMultiComponentFactory = _xComponentContext.getServiceManager();
		Object dialogModel = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.UnoControlDialogModel", _xComponentContext);
		XPropertySet xPSetDialog = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, dialogModel);
		xPSetDialog.setPropertyValue("Width", new Integer(126));
		xPSetDialog.setPropertyValue("Height", new Integer(130));
		xPSetDialog.setPropertyValue("Title", new String("About PengYou"));
		XMultiServiceFactory xMultiServiceFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
				XMultiServiceFactory.class, dialogModel);

		/*
		 * Image
		 */
		Object imageModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlImageControlModel");
		XPropertySet xPSetImage = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, imageModel);
		xPSetImage.setPropertyValue("PositionX", new Integer(0));
		xPSetImage.setPropertyValue("PositionY", new Integer(0));
		xPSetImage.setPropertyValue("Width", new Integer(126));
		xPSetImage.setPropertyValue("Height", new Integer(47));
		xPSetImage.setPropertyValue("Name", _imageName);
		xPSetImage.setPropertyValue("TabIndex", new Short((short)4));
		xPSetImage.setPropertyValue("ImageURL", new String("file:///" + Ressources.getImage("logo.png")));
		
		/*
		 * Label
		 */
		Object labelAboutModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetAboutDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelAboutModel);
		xPSetAboutDesc.setPropertyValue("PositionX", new Integer(5));
		xPSetAboutDesc.setPropertyValue("PositionY", new Integer(52));
		xPSetAboutDesc.setPropertyValue("Width", new Integer(106));
		xPSetAboutDesc.setPropertyValue("Height", new Integer(50));
		xPSetAboutDesc.setPropertyValue("Align", new Short((short)0));
		xPSetAboutDesc.setPropertyValue("Name", _labelAboutName);
		xPSetAboutDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetAboutDesc.setPropertyValue("Label", new String("PengYou OpenOffice.org Extension.\n(c) Copright 2006 - The PengYou team.\n\nAuthor: Jérémy Bethmont.\n              jeremy.bethmont@gmail.com"));
		
		/*
		 * Ok Button
		 */
		Object okModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetOk = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, okModel);
		xPSetOk.setPropertyValue("PositionX", new Integer(84));
		xPSetOk.setPropertyValue("PositionY", new Integer(111));
		xPSetOk.setPropertyValue("Width", new Integer(32));
		xPSetOk.setPropertyValue("Height", new Integer(14));
		xPSetOk.setPropertyValue("Name", _okName);
		xPSetOk.setPropertyValue("TabIndex", new Short((short)4));
		xPSetOk.setPropertyValue("Label", new String("Ok"));
		

		
		XNameContainer xNameCont = (XNameContainer)UnoRuntime.queryInterface(
				XNameContainer.class, dialogModel);
		xNameCont.insertByName(_imageName, imageModel);
		xNameCont.insertByName(_labelAboutName, labelAboutModel);
		xNameCont.insertByName(_okName, okModel);
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
	
	/**
	 * OnOkClick - Called when the user press the "Ok" button
	 */
	public class OnOkClick implements XActionListener {
		
		public OnOkClick(XControlContainer xControlCont) {
		}
		
		public void disposing(EventObject eventObject) {
		}
		
		public void actionPerformed(ActionEvent actionEvent) {

			xDialog.endExecute();
		}
	}

}
	