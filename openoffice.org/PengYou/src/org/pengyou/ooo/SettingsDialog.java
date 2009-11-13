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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pengyou.ooo.utils.Settings;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XCheckBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XItemListener;
import com.sun.star.awt.XTextComponent;
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
public class SettingsDialog {
	
	private static final String _labelServerName = "labelServer";
	private static final String _labelPortName = "labelPortName";
	private static final String _labelPathName = "labelPathName";
	private static final String _labelUserName = "labelUserName";
	private static final String _labelPasswordName = "labelPasswordName";
	private static final String _txtPasswordName = "txtPasswordName";
	private static final String _txtUserName = "txtUserName";
	private static final String _txtPathName = "txtPathName";
	private static final String _txtPortName = "txtPortName";
	private static final String _txtServerName = "txtServer";
	private static final String _containerServerName = "containerServerName";
	private static final String _containerProxyName = "containerProxyName";
	private static final String _okName = "okName";
	private static final String _cancelName = "cancelName";
	//private static final String _lineServerName = "lineServer";
	private static final String _checkProxyName = "checkProxy";
	private static final String _labelServerProxyName = "labelServerProxyName";
	private static final String _labelPortProxyName = "labelPortProxyName";
	private static final String _labelUserProxyName = "labelUserProxyName";
	private static final String _labelPasswordProxyName = "labelPasswordProxyName";
	private static final String _txtServerProxyName = "txtServerProxyName";
	private static final String _txtPortProxyName = "txtPortProxyName";
	private static final String _txtUserProxyName = "txtUserProxyName";
	private static final String _txtPasswordProxyName = "txtPasswordProxyName";

	private XComponentContext _xComponentContext;
	
	private XDialog xDialog = null;
	
	private boolean useProxy = false;
	
	private static Logger log = Logger.getLogger(SettingsDialog.class);
	
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
	public SettingsDialog(XComponentContext xComponentContext) throws IOException {
		_xComponentContext = xComponentContext;
		Settings.load();
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
		xPSetDialog.setPropertyValue("Width", new Integer(175));
		xPSetDialog.setPropertyValue("Height", new Integer(260));
		xPSetDialog.setPropertyValue("Title", new String("PengYou Settings"));
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
		xPSetServerContainerDesc.setPropertyValue("Name", _containerServerName);
		xPSetServerContainerDesc.setPropertyValue("TabIndex", new Short((short)0));
		//xPSetServerContainerDesc.setPropertyValue("FontDescriptor", ((new FontDescriptor()).Strikeout = 4));
		xPSetServerContainerDesc.setPropertyValue("Label", new String("Server Settings: "));
		
		/*
		 * Server Line
		 */
		/*
		Object lineServerModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedLineModel");
		XPropertySet xPSetServerLineDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, lineServerModel);
		xPSetServerLineDesc.setPropertyValue("PositionX", new Integer(10));
		xPSetServerLineDesc.setPropertyValue("PositionY", new Integer(15));
		xPSetServerLineDesc.setPropertyValue("Width", new Integer(70));
		xPSetServerLineDesc.setPropertyValue("Height", new Integer(10));
		xPSetServerLineDesc.setPropertyValue("Name", _lineServerName);
		xPSetServerLineDesc.setPropertyValue("TabIndex", new Short((short)0));
		xPSetServerLineDesc.setPropertyValue("Orientation", new Long((long)0));
		xPSetServerLineDesc.setPropertyValue("Label", new String("Server Settings: "));
		*/
		
		/*
		 * Server Label
		 */
		Object labelServerModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetServerDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelServerModel);
		xPSetServerDesc.setPropertyValue("PositionX", new Integer(20));
		xPSetServerDesc.setPropertyValue("PositionY", new Integer(30));
		xPSetServerDesc.setPropertyValue("Width", new Integer(30));
		xPSetServerDesc.setPropertyValue("Height", new Integer(10));
		xPSetServerDesc.setPropertyValue("Align", new Short((short)0));
		xPSetServerDesc.setPropertyValue("Name", _labelServerName);
		xPSetServerDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetServerDesc.setPropertyValue("Label", new String("Host: "));
		
		/*
		 * Port Label
		 */
		Object labelPortModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetPortDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelPortModel);
		xPSetPortDesc.setPropertyValue("PositionX", new Integer(20));
		xPSetPortDesc.setPropertyValue("PositionY", new Integer(50));
		xPSetPortDesc.setPropertyValue("Width", new Integer(30));
		xPSetPortDesc.setPropertyValue("Height", new Integer(10));
		xPSetPortDesc.setPropertyValue("Align", new Short((short)0));
		xPSetPortDesc.setPropertyValue("Name", _labelPortName);
		xPSetPortDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetPortDesc.setPropertyValue("Label", new String("Port: "));
		
		/*
		 * Path Label
		 */
		Object labelPathModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetPathDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelPathModel);
		xPSetPathDesc.setPropertyValue("PositionX", new Integer(20));
		xPSetPathDesc.setPropertyValue("PositionY", new Integer(70));
		xPSetPathDesc.setPropertyValue("Width", new Integer(30));
		xPSetPathDesc.setPropertyValue("Height", new Integer(10));
		xPSetPathDesc.setPropertyValue("Align", new Short((short)0));
		xPSetPathDesc.setPropertyValue("Name", _labelPathName);
		xPSetPathDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetPathDesc.setPropertyValue("Label", new String("Path: "));
		
		/*
		 * Username Label
		 */
		Object labelUsernameModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetUsernameDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelUsernameModel);
		xPSetUsernameDesc.setPropertyValue("PositionX", new Integer(20));
		xPSetUsernameDesc.setPropertyValue("PositionY", new Integer(90));
		xPSetUsernameDesc.setPropertyValue("Width", new Integer(30));
		xPSetUsernameDesc.setPropertyValue("Height", new Integer(10));
		xPSetUsernameDesc.setPropertyValue("Align", new Short((short)0));
		xPSetUsernameDesc.setPropertyValue("Name", _labelUserName);
		xPSetUsernameDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetUsernameDesc.setPropertyValue("Label", new String("Username: "));
		
		/*
		 * Password Label
		 */
		Object labelPasswordModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetPasswordDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelPasswordModel);
		xPSetPasswordDesc.setPropertyValue("PositionX", new Integer(20));
		xPSetPasswordDesc.setPropertyValue("PositionY", new Integer(110));
		xPSetPasswordDesc.setPropertyValue("Width", new Integer(30));
		xPSetPasswordDesc.setPropertyValue("Height", new Integer(10));
		xPSetPasswordDesc.setPropertyValue("Align", new Short((short)0));
		xPSetPasswordDesc.setPropertyValue("Name", _labelPasswordName);
		xPSetPasswordDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetPasswordDesc.setPropertyValue("Label", new String("Password: "));
		
		/*
		 * Server Text
		 */
		Object txtServerModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetServertxtDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, txtServerModel);
		xPSetServertxtDesc.setPropertyValue("PositionX", new Integer(50));
		xPSetServertxtDesc.setPropertyValue("PositionY", new Integer(30));
		xPSetServertxtDesc.setPropertyValue("Width", new Integer(110));
		xPSetServertxtDesc.setPropertyValue("Height", new Integer(12));
		xPSetServertxtDesc.setPropertyValue("Align", new Short((short)0));
		xPSetServertxtDesc.setPropertyValue("Name", _txtServerName);
		xPSetServertxtDesc.setPropertyValue("TabIndex", new Short((short)0));
		xPSetServertxtDesc.setPropertyValue("MultiLine", new Boolean("false"));
		xPSetServertxtDesc.setPropertyValue("Text", Settings.getServer());
		
		/*
		 * Port Text
		 */
		Object txtPortModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetPorttxtDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, txtPortModel);
		xPSetPorttxtDesc.setPropertyValue("PositionX", new Integer(50));
		xPSetPorttxtDesc.setPropertyValue("PositionY", new Integer(50));
		xPSetPorttxtDesc.setPropertyValue("Width", new Integer(110));
		xPSetPorttxtDesc.setPropertyValue("Height", new Integer(12));
		xPSetPorttxtDesc.setPropertyValue("Align", new Short((short)0));
		xPSetPorttxtDesc.setPropertyValue("Name", _txtPortName);
		xPSetPorttxtDesc.setPropertyValue("TabIndex", new Short((short)0));
		xPSetPorttxtDesc.setPropertyValue("MultiLine", new Boolean("false"));
		if (Settings.getPort() != -1)
			xPSetPorttxtDesc.setPropertyValue("Text", Integer.toString(Settings.getPort()));
		
		/*
		 * Path Text
		 */
		Object txtPathModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetPathtxtDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, txtPathModel);
		xPSetPathtxtDesc.setPropertyValue("PositionX", new Integer(50));
		xPSetPathtxtDesc.setPropertyValue("PositionY", new Integer(70));
		xPSetPathtxtDesc.setPropertyValue("Width", new Integer(110));
		xPSetPathtxtDesc.setPropertyValue("Height", new Integer(12));
		xPSetPathtxtDesc.setPropertyValue("Align", new Short((short)0));
		xPSetPathtxtDesc.setPropertyValue("Name", _txtPathName);
		xPSetPathtxtDesc.setPropertyValue("TabIndex", new Short((short)0));
		xPSetPathtxtDesc.setPropertyValue("MultiLine", new Boolean("false"));
		xPSetPathtxtDesc.setPropertyValue("Text", Settings.getPath());
		
		/*
		 * Username Text
		 */
		Object txtUsernameModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetUsernametxtDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, txtUsernameModel);
		xPSetUsernametxtDesc.setPropertyValue("PositionX", new Integer(50));
		xPSetUsernametxtDesc.setPropertyValue("PositionY", new Integer(90));
		xPSetUsernametxtDesc.setPropertyValue("Width", new Integer(110));
		xPSetUsernametxtDesc.setPropertyValue("Height", new Integer(12));
		xPSetUsernametxtDesc.setPropertyValue("Align", new Short((short)0));
		xPSetUsernametxtDesc.setPropertyValue("Name", _txtUserName);
		xPSetUsernametxtDesc.setPropertyValue("TabIndex", new Short((short)0));
		xPSetUsernametxtDesc.setPropertyValue("MultiLine", new Boolean("false"));
		xPSetUsernametxtDesc.setPropertyValue("Text", Settings.getUser());
		
		/*
		 * Password Text
		 */
		Object txtPasswordModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetPasswordtxtDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, txtPasswordModel);
		xPSetPasswordtxtDesc.setPropertyValue("PositionX", new Integer(50));
		xPSetPasswordtxtDesc.setPropertyValue("PositionY", new Integer(110));
		xPSetPasswordtxtDesc.setPropertyValue("Width", new Integer(110));
		xPSetPasswordtxtDesc.setPropertyValue("Height", new Integer(12));
		xPSetPasswordtxtDesc.setPropertyValue("Align", new Short((short)0));
		xPSetPasswordtxtDesc.setPropertyValue("Name", _txtPasswordName);
		xPSetPasswordtxtDesc.setPropertyValue("TabIndex", new Short((short)0));
		xPSetPasswordtxtDesc.setPropertyValue("MultiLine", new Boolean("false"));
		xPSetPasswordtxtDesc.setPropertyValue("Text", Settings.getPassword());
		xPSetPasswordtxtDesc.setPropertyValue("EchoChar", new Short((short)'*'));

		/*
		 * Proxy
		 */
		useProxy = Settings.getProxy();
		/*
		 * Proxy Container
		 */
		Object containerProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetProxyContainerDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, containerProxyModel);
		xPSetProxyContainerDesc.setPropertyValue("PositionX", new Integer(10));
		xPSetProxyContainerDesc.setPropertyValue("PositionY", new Integer(135));
		xPSetProxyContainerDesc.setPropertyValue("Width", new Integer(70));
		xPSetProxyContainerDesc.setPropertyValue("Height", new Integer(10));
		xPSetProxyContainerDesc.setPropertyValue("Align", new Short((short)0));
		xPSetProxyContainerDesc.setPropertyValue("Name", _containerProxyName);
		xPSetProxyContainerDesc.setPropertyValue("TabIndex", new Short((short)0));
		//xPSetServerContainerDesc.setPropertyValue("FontDescriptor", ((new FontDescriptor()).Strikeout = 4));
		xPSetProxyContainerDesc.setPropertyValue("Label", new String("Proxy Settings: "));
	
		/*
		 * Proxy Check Box
		 */
		Object checkProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlCheckBoxModel");
		XPropertySet xPSetProxyCheckDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, checkProxyModel);
		xPSetProxyCheckDesc.setPropertyValue("PositionX", new Integer(20));
		xPSetProxyCheckDesc.setPropertyValue("PositionY", new Integer(150));
		xPSetProxyCheckDesc.setPropertyValue("Width", new Integer(70));
		xPSetProxyCheckDesc.setPropertyValue("Height", new Integer(10));
		xPSetProxyCheckDesc.setPropertyValue("Align", new Short((short)0));
		xPSetProxyCheckDesc.setPropertyValue("Name", _checkProxyName);
		xPSetProxyCheckDesc.setPropertyValue("TabIndex", new Short((short)0));
		if (useProxy)
			xPSetProxyCheckDesc.setPropertyValue("State", new Short((short)1));
		else
			xPSetProxyCheckDesc.setPropertyValue("State", new Short((short)0));
		
		xPSetProxyCheckDesc.setPropertyValue("Label", new String("Use a proxy server"));
		
		/*
		 * Server Proxy Label
		 */
		Object labelServerProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetServerProxyDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelServerProxyModel);
		xPSetServerProxyDesc.setPropertyValue("PositionX", new Integer(20));
		xPSetServerProxyDesc.setPropertyValue("PositionY", new Integer(170));
		xPSetServerProxyDesc.setPropertyValue("Width", new Integer(30));
		xPSetServerProxyDesc.setPropertyValue("Height", new Integer(10));
		xPSetServerProxyDesc.setPropertyValue("Align", new Short((short)0));
		xPSetServerProxyDesc.setPropertyValue("Name", _labelServerProxyName);
		xPSetServerProxyDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetServerProxyDesc.setPropertyValue("Label", new String("Host: "));
		if (!useProxy)
			xPSetServerProxyDesc.setPropertyValue("Enabled", new Boolean("false"));
		
		/*
		 * Port Proxy Label
		 */
		Object labelPortProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetPortProxyDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelPortProxyModel);
		xPSetPortProxyDesc.setPropertyValue("PositionX", new Integer(115));
		xPSetPortProxyDesc.setPropertyValue("PositionY", new Integer(170));
		xPSetPortProxyDesc.setPropertyValue("Width", new Integer(15));
		xPSetPortProxyDesc.setPropertyValue("Height", new Integer(10));
		xPSetPortProxyDesc.setPropertyValue("Align", new Short((short)0));
		xPSetPortProxyDesc.setPropertyValue("Name", _labelPortProxyName);
		xPSetPortProxyDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetPortProxyDesc.setPropertyValue("Label", new String("Port: "));
		if (!useProxy)
			xPSetPortProxyDesc.setPropertyValue("Enabled", new Boolean("false"));
		
		/*
		 * Username Proxy Label
		 */
		Object labelUsernameProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetUsernameProxyDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelUsernameProxyModel);
		xPSetUsernameProxyDesc.setPropertyValue("PositionX", new Integer(20));
		xPSetUsernameProxyDesc.setPropertyValue("PositionY", new Integer(190));
		xPSetUsernameProxyDesc.setPropertyValue("Width", new Integer(30));
		xPSetUsernameProxyDesc.setPropertyValue("Height", new Integer(10));
		xPSetUsernameProxyDesc.setPropertyValue("Align", new Short((short)0));
		xPSetUsernameProxyDesc.setPropertyValue("Name", _labelUserProxyName);
		xPSetUsernameProxyDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetUsernameProxyDesc.setPropertyValue("Label", new String("Username: "));
		if (!useProxy)
			xPSetUsernameProxyDesc.setPropertyValue("Enabled", new Boolean("false"));
		
		/*
		 * Password Proxy Label
		 */
		Object labelPasswordProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetPasswordProxyDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelPasswordProxyModel);
		xPSetPasswordProxyDesc.setPropertyValue("PositionX", new Integer(20));
		xPSetPasswordProxyDesc.setPropertyValue("PositionY", new Integer(210));
		xPSetPasswordProxyDesc.setPropertyValue("Width", new Integer(30));
		xPSetPasswordProxyDesc.setPropertyValue("Height", new Integer(10));
		xPSetPasswordProxyDesc.setPropertyValue("Align", new Short((short)0));
		xPSetPasswordProxyDesc.setPropertyValue("Name", _labelPasswordProxyName);
		xPSetPasswordProxyDesc.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetPasswordProxyDesc.setPropertyValue("Label", new String("Password: "));
		if (!useProxy)
			xPSetPasswordProxyDesc.setPropertyValue("Enabled", new Boolean("false"));
		
		/*
		 * Server Proxy Text
		 */
		Object txtServerProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetServertxtProxyDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, txtServerProxyModel);
		xPSetServertxtProxyDesc.setPropertyValue("PositionX", new Integer(50));
		xPSetServertxtProxyDesc.setPropertyValue("PositionY", new Integer(170));
		xPSetServertxtProxyDesc.setPropertyValue("Width", new Integer(60));
		xPSetServertxtProxyDesc.setPropertyValue("Height", new Integer(12));
		xPSetServertxtProxyDesc.setPropertyValue("Align", new Short((short)0));
		xPSetServertxtProxyDesc.setPropertyValue("Name", _txtServerProxyName);
		xPSetServertxtProxyDesc.setPropertyValue("TabIndex", new Short((short)0));
		xPSetServertxtProxyDesc.setPropertyValue("MultiLine", new Boolean("false"));
		xPSetServertxtProxyDesc.setPropertyValue("Text", Settings.getProxyHost());
		if (!useProxy)
			xPSetServertxtProxyDesc.setPropertyValue("Enabled", new Boolean("false"));
		
		/*
		 * Port Proxy Text
		 */
		Object txtPortProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetPorttxtProxyDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, txtPortProxyModel);
		xPSetPorttxtProxyDesc.setPropertyValue("PositionX", new Integer(135));
		xPSetPorttxtProxyDesc.setPropertyValue("PositionY", new Integer(170));
		xPSetPorttxtProxyDesc.setPropertyValue("Width", new Integer(25));
		xPSetPorttxtProxyDesc.setPropertyValue("Height", new Integer(12));
		xPSetPorttxtProxyDesc.setPropertyValue("Align", new Short((short)1));
		xPSetPorttxtProxyDesc.setPropertyValue("Name", _txtPortProxyName);
		xPSetPorttxtProxyDesc.setPropertyValue("TabIndex", new Short((short)0));
		xPSetPorttxtProxyDesc.setPropertyValue("MultiLine", new Boolean("false"));
		if (Settings.getProxyPort() != -1)
			xPSetPorttxtProxyDesc.setPropertyValue("Text", Integer.toString(Settings.getProxyPort()));
		if (!useProxy)
			xPSetPorttxtProxyDesc.setPropertyValue("Enabled", new Boolean("false"));
		
		/*
		 * Username Proxy Text
		 */
		Object txtUsernameProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetUsernametxtProxyDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, txtUsernameProxyModel);
		xPSetUsernametxtProxyDesc.setPropertyValue("PositionX", new Integer(50));
		xPSetUsernametxtProxyDesc.setPropertyValue("PositionY", new Integer(190));
		xPSetUsernametxtProxyDesc.setPropertyValue("Width", new Integer(110));
		xPSetUsernametxtProxyDesc.setPropertyValue("Height", new Integer(12));
		xPSetUsernametxtProxyDesc.setPropertyValue("Align", new Short((short)0));
		xPSetUsernametxtProxyDesc.setPropertyValue("Name", _txtUserProxyName);
		xPSetUsernametxtProxyDesc.setPropertyValue("TabIndex", new Short((short)0));
		xPSetUsernametxtProxyDesc.setPropertyValue("MultiLine", new Boolean("false"));
		xPSetUsernametxtProxyDesc.setPropertyValue("Text", Settings.getProxyUser());
		if (!useProxy)
			xPSetUsernametxtProxyDesc.setPropertyValue("Enabled", new Boolean("false"));
		
		/*
		 * Password Proxy Text
		 */
		Object txtPasswordProxyModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetPasswordtxtProxyDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, txtPasswordProxyModel);
		xPSetPasswordtxtProxyDesc.setPropertyValue("PositionX", new Integer(50));
		xPSetPasswordtxtProxyDesc.setPropertyValue("PositionY", new Integer(210));
		xPSetPasswordtxtProxyDesc.setPropertyValue("Width", new Integer(110));
		xPSetPasswordtxtProxyDesc.setPropertyValue("Height", new Integer(12));
		xPSetPasswordtxtProxyDesc.setPropertyValue("Align", new Short((short)0));
		xPSetPasswordtxtProxyDesc.setPropertyValue("Name", _txtPasswordProxyName);
		xPSetPasswordtxtProxyDesc.setPropertyValue("TabIndex", new Short((short)0));
		xPSetPasswordtxtProxyDesc.setPropertyValue("MultiLine", new Boolean("false"));
		xPSetPasswordtxtProxyDesc.setPropertyValue("Text", Settings.getProxyPassword());
		xPSetPasswordtxtProxyDesc.setPropertyValue("EchoChar", new Short((short)'*'));
		if (!useProxy)
			xPSetPasswordtxtProxyDesc.setPropertyValue("Enabled", new Boolean("false"));
		
		/*
		 * Ok Button
		 */
		log.log(Level.DEBUG, "Create the OK button model and set the properties");
		Object okModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetOk = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, okModel);
		xPSetOk.setPropertyValue("PositionX", new Integer(84));
		xPSetOk.setPropertyValue("PositionY", new Integer(236));
		xPSetOk.setPropertyValue("Width", new Integer(32));
		xPSetOk.setPropertyValue("Height", new Integer(14));
		xPSetOk.setPropertyValue("Name", _okName);
		xPSetOk.setPropertyValue("TabIndex", new Short((short)4));
		xPSetOk.setPropertyValue("Label", new String("Ok"));
		
		/*
		 * Cancel Button
		 */
		Object cancelModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetCancel = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, cancelModel);
		xPSetCancel.setPropertyValue("PositionX", new Integer(123));
		xPSetCancel.setPropertyValue("PositionY", new Integer(236));
		xPSetCancel.setPropertyValue("Width", new Integer(42));
		xPSetCancel.setPropertyValue("Height", new Integer(14));
		xPSetCancel.setPropertyValue("Name", _cancelName);
		xPSetCancel.setPropertyValue("TabIndex", new Short((short)5));
		xPSetCancel.setPropertyValue("PushButtonType", new Short((short)2));
		xPSetCancel.setPropertyValue("Label", new String("Cancel"));
		
		XNameContainer xNameCont = (XNameContainer)UnoRuntime.queryInterface(
				XNameContainer.class, dialogModel);
		xNameCont.insertByName(_containerServerName, containerServerModel);
		//xNameCont.insertByName(_lineServerName, lineServerModel);
		xNameCont.insertByName(_labelServerName, labelServerModel);
		xNameCont.insertByName(_labelPortName, labelPortModel);
		xNameCont.insertByName(_labelPathName, labelPathModel);
		xNameCont.insertByName(_labelUserName, labelUsernameModel);
		xNameCont.insertByName(_labelPasswordName, labelPasswordModel);
		xNameCont.insertByName(_txtServerName, txtServerModel);
		xNameCont.insertByName(_txtPortName, txtPortModel);
		xNameCont.insertByName(_txtPathName, txtPathModel);
		xNameCont.insertByName(_txtUserName, txtUsernameModel);
		xNameCont.insertByName(_txtPasswordName, txtPasswordModel);
		xNameCont.insertByName(_containerProxyName, containerProxyModel);
		xNameCont.insertByName(_checkProxyName, checkProxyModel);
		xNameCont.insertByName(_labelServerProxyName, labelServerProxyModel);
		xNameCont.insertByName(_labelPortProxyName, labelPortProxyModel);
		xNameCont.insertByName(_labelUserProxyName, labelUsernameProxyModel);
		xNameCont.insertByName(_labelPasswordProxyName, labelPasswordProxyModel);
		xNameCont.insertByName(_txtServerProxyName, txtServerProxyModel);
		xNameCont.insertByName(_txtPortProxyName, txtPortProxyModel);
		xNameCont.insertByName(_txtUserProxyName, txtUsernameProxyModel);
		xNameCont.insertByName(_txtPasswordProxyName, txtPasswordProxyModel);
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

		Object objectOk = xControlCont.getControl(_okName);
		XButton xOk = (XButton)UnoRuntime.queryInterface(XButton.class, objectOk);
		xOk.addActionListener(new OnOkClick(xControlCont));
		
		Object objectCheck = xControlCont.getControl(_checkProxyName);
		XCheckBox xProxy = (XCheckBox)UnoRuntime.queryInterface(XCheckBox.class, objectCheck);
		xProxy.addItemListener(new OnCheckClick(xControlCont));
		
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
		
		private XControlContainer _xControlCont;
		
		public OnOkClick(XControlContainer xControlCont) {
			_xControlCont = xControlCont;
		}
		
		public void disposing(EventObject eventObject) {
			_xControlCont = null;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			XTextComponent t = null;
			try {
				t = (XTextComponent)UnoRuntime.queryInterface(
						XTextComponent.class, _xControlCont.getControl(_txtServerName));
				Settings.setServer(t.getText());
				t = (XTextComponent)UnoRuntime.queryInterface(
						XTextComponent.class, _xControlCont.getControl(_txtPortName));
				String p = t.getText();
				if (p.length() > 0)
				Settings.setPort(Integer.parseInt(p));
				t = (XTextComponent)UnoRuntime.queryInterface(
						XTextComponent.class, _xControlCont.getControl(_txtPathName));
				Settings.setPath(t.getText());
				t = (XTextComponent)UnoRuntime.queryInterface(
						XTextComponent.class, _xControlCont.getControl(_txtUserName));
				Settings.setUser(t.getText());
				t = (XTextComponent)UnoRuntime.queryInterface(
						XTextComponent.class, _xControlCont.getControl(_txtPasswordName));
				Settings.setPassword(t.getText());
				
				t = (XTextComponent)UnoRuntime.queryInterface(
						XTextComponent.class, _xControlCont.getControl(_txtServerProxyName));
				Settings.setProxyHost(t.getText());
				t = (XTextComponent)UnoRuntime.queryInterface(
						XTextComponent.class, _xControlCont.getControl(_txtPortProxyName));
				String p1 = t.getText();
				if (p1.length() > 0)
					Settings.setProxyPort(Integer.parseInt(p1));
				t = (XTextComponent)UnoRuntime.queryInterface(
						XTextComponent.class, _xControlCont.getControl(_txtUserProxyName));
				Settings.setProxyUser(t.getText());
				t = (XTextComponent)UnoRuntime.queryInterface(
						XTextComponent.class, _xControlCont.getControl(_txtPasswordProxyName));
				Settings.setProxyPassword(t.getText());
				Settings.setProxy(useProxy);
				
				Settings.store();
				Settings.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			xDialog.endExecute();
		}
	}
	/**
	 * OnCheckClick - Called when the user press the "use proxy" Check Box
	 */
	public class OnCheckClick implements XItemListener {
		
		private XControlContainer _xControlCont;
		
		public OnCheckClick(XControlContainer xControlCont) {
			_xControlCont = xControlCont;
		}
		
		public void disposing(EventObject eventObject) {
			_xControlCont = null;
		}
		
		public void itemStateChanged(ItemEvent arg0) {
			useProxy = !useProxy;
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, _xControlCont.getControl(_txtServerProxyName))).setEnable(useProxy);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, _xControlCont.getControl(_txtPortProxyName))).setEnable(useProxy);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, _xControlCont.getControl(_txtUserProxyName))).setEnable(useProxy);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, _xControlCont.getControl(_txtPasswordProxyName))).setEnable(useProxy);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, _xControlCont.getControl(_labelServerProxyName))).setEnable(useProxy);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, _xControlCont.getControl(_labelPortProxyName))).setEnable(useProxy);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, _xControlCont.getControl(_labelUserProxyName))).setEnable(useProxy);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, _xControlCont.getControl(_labelPasswordProxyName))).setEnable(useProxy);
		}
	}
}
	