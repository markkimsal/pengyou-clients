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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pengyou.ooo.utils.LocalFileSystem;
import org.pengyou.ooo.utils.Ressources;
import org.pengyou.ooo.utils.Settings;
import org.pengyou.ooo.utils.WebDavStore;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.FontDescriptor;
import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.WindowAttribute;
import com.sun.star.awt.WindowClass;
import com.sun.star.awt.WindowDescriptor;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XComboBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XItemListener;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XProgressBar;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.ArrayIndexOutOfBoundsException;
import com.sun.star.lang.EventObject;
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
public class PublishAsDialog {
	
	private String _davServer = "";
	private int _davPort = 80;
	private String _davBasePath = "/";
	private String _davPath = "";
	private String _davUser = "";
	private String _davPass = "";
	private Vector _davFiles = null;
		
	private static final String _prevName = "Button1";
	private static final String _urlName = "Url1";
	private static final String _labelFileName = "Label1";
	private static final String _labelAuthorName = "Label2";
	private static final String _labelDateCreaName = "Label3";
	private static final String _labelDateEditName = "Label4";
	private static final String _labelDescName = "Label5";
	private static final String _listName = "List1";
	private static final String _moreName = "Button2";
	private static final String _progressName = "Progress";
	private static final String _okName = "Button3";
	private static final String _cancelName = "Button4";
	private static final String _textFileNameName = "Text0";
	private static final String _labelFileNameName = "Label6";
	
	private XComponentContext _xComponentContext;
	
	private Object _oDesktop;
	private XDesktop _xDesktop;
	private XComponent _xComponent;
	private XStorable _xStorable;
	private XDocumentInfo _xDocumentInfo;
	private XModel _xModel;
	
	private XDialog xDialog = null;
	
	private static XToolkit _xToolkit;
	private static XFrame _xFrame;
	
	private static Logger log = Logger.getLogger(PublishAsDialog.class);
	
	/**
	 * PublishAsDialog constructor
	 * @param XComponentContext xComponentContext
	 * @param String davServer
	 * @param int davPort
	 * @param String davPath
	 * @param String davUser
	 * @param String davPass
	 * @return OpenDialog
	 * @throws IOException 
	 */
	public PublishAsDialog(XToolkit xToolkit, XFrame xFrame, XComponentContext xComponentContext) throws IOException {
		_xToolkit = xToolkit;
		_xFrame = xFrame;
		_xComponentContext = xComponentContext;
		_davServer = Settings.getServer();
		_davPort = Settings.getPort();
		_davBasePath = Settings.getPath();
		_davPath = Settings.getPath();
		_davUser = Settings.getUser();
		_davPass = Settings.getPassword();
		
		_xComponentContext = xComponentContext;
		
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
		
		
		_xStorable = (XStorable)UnoRuntime.queryInterface(
	            XStorable.class, _xComponent);
			
		XDocumentInfoSupplier xDocumentInfoSupplier =
			(XDocumentInfoSupplier)UnoRuntime.queryInterface(
					XDocumentInfoSupplier.class, _xComponent);
		
		_xDocumentInfo = xDocumentInfoSupplier.getDocumentInfo();
		
		_xModel = (XModel)UnoRuntime.queryInterface(XModel.class,
				_xComponent);
	}
	

	/**
	 * Method for creating a the dialog box
	 */
	@SuppressWarnings("deprecation")
	public void createDialog() throws com.sun.star.uno.Exception {
		log.log(Level.DEBUG, "createDialog();");
		// Get the service manager from the component context
		XMultiComponentFactory xMultiComponentFactory = _xComponentContext.getServiceManager();
		
		// Create the dialog model and set the properties
		log.log(Level.DEBUG, "Create the dialog model and set the properties");
		Object dialogModel = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.UnoControlDialogModel", _xComponentContext);
		XPropertySet xPSetDialog = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, dialogModel);
		xPSetDialog.setPropertyValue("Width", new Integer(343));
		xPSetDialog.setPropertyValue("Height", new Integer(214));
		xPSetDialog.setPropertyValue("Title", new String("Open a remote document"));
		
		// Get the service manager from the dialog model
		log.log(Level.DEBUG, "Get the service manager from the dialog model");
		XMultiServiceFactory xMultiServiceFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
				XMultiServiceFactory.class, dialogModel);
		
		// Create the prev button model and set the properties
		log.log(Level.DEBUG, "Create the prev button model and set the properties");
		Object buttonModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetButton = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, buttonModel);
		xPSetButton.setPropertyValue("PositionX", new Integer(6));
		xPSetButton.setPropertyValue("PositionY", new Integer(5));
		xPSetButton.setPropertyValue("Width", new Integer(14));
		xPSetButton.setPropertyValue("Height", new Integer(16));
		xPSetButton.setPropertyValue("Name", _prevName);
		xPSetButton.setPropertyValue("TabIndex", new Short((short)0));        
		xPSetButton.setPropertyValue("Label", new String(""));
		xPSetButton.setPropertyValue("ImageURL", new String("file:///" + Ressources.getImage("go-up.png")));
				
		
		// Create the combo list model and set the properties
		log.log(Level.DEBUG, "Create the combo list model and set the properties");
		Object urlModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlComboBoxModel");
		XPropertySet xPSetUrl = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, urlModel);
		xPSetUrl.setPropertyValue("PositionX", new Integer(21));
		xPSetUrl.setPropertyValue("PositionY", new Integer(6));
		xPSetUrl.setPropertyValue("Width", new Integer(316));
		xPSetUrl.setPropertyValue("Height", new Integer(14));
		xPSetUrl.setPropertyValue("Name", _urlName);
		xPSetUrl.setPropertyValue("TabIndex", new Short((short)1));
		xPSetUrl.setPropertyValue("Dropdown", new Boolean("true"));
		
		
		// Create the labels of the list model and set the properties
		log.log(Level.DEBUG, "Create the labels of the list model and set the properties");
		// Name
		Object labelFileModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetLabelFile = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelFileModel);
		xPSetLabelFile.setPropertyValue("PositionX", new Integer(6));
		xPSetLabelFile.setPropertyValue("PositionY", new Integer(23));
		xPSetLabelFile.setPropertyValue("Width", new Integer(81));
		xPSetLabelFile.setPropertyValue("Height", new Integer(13));
		xPSetLabelFile.setPropertyValue("Align", new Short((short)0));
		xPSetLabelFile.setPropertyValue("Name", _labelFileName);
		xPSetLabelFile.setPropertyValue("TabIndex", new Short((short)99));        
		xPSetLabelFile.setPropertyValue("Label", new String(" Name"));      
		// Last Author
		Object labelAuthorModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetLabelAuthor = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelAuthorModel);
		xPSetLabelAuthor.setPropertyValue("PositionX", new Integer(86));
		xPSetLabelAuthor.setPropertyValue("PositionY", new Integer(23));
		xPSetLabelAuthor.setPropertyValue("Width", new Integer(61));
		xPSetLabelAuthor.setPropertyValue("Height", new Integer(13));
		xPSetLabelAuthor.setPropertyValue("Align", new Short((short)0));
		xPSetLabelAuthor.setPropertyValue("Name", _labelAuthorName);
		xPSetLabelAuthor.setPropertyValue("TabIndex", new Short((short)99));        
		xPSetLabelAuthor.setPropertyValue("Label", new String(" Last Author"));
		// Creation Date
		Object labelDateCreaModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetLabelDateCrea = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelDateCreaModel);
		//xPSetLabelDateCrea.setPropertyValue("PositionX", new Integer(142));
		xPSetLabelDateCrea.setPropertyValue("PositionX", new Integer(146));
		//xPSetLabelDateCrea.setPropertyValue("PositionY", new Integer(18));
		xPSetLabelDateCrea.setPropertyValue("PositionY", new Integer(23));
		xPSetLabelDateCrea.setPropertyValue("Width", new Integer(51));
		xPSetLabelDateCrea.setPropertyValue("Height", new Integer(13));
		xPSetLabelDateCrea.setPropertyValue("Align", new Short((short)0));
		xPSetLabelDateCrea.setPropertyValue("Name", _labelDateCreaName);
		xPSetLabelDateCrea.setPropertyValue("TabIndex", new Short((short)99));        
		xPSetLabelDateCrea.setPropertyValue("Label", new String(" Creation Date"));
		// Edition Date
		Object labelDateEditModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetLabelDateEdit = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelDateEditModel);
		xPSetLabelDateEdit.setPropertyValue("PositionX", new Integer(197));
		xPSetLabelDateEdit.setPropertyValue("PositionY", new Integer(23));
		xPSetLabelDateEdit.setPropertyValue("Width", new Integer(51));
		xPSetLabelDateEdit.setPropertyValue("Height", new Integer(13));
		xPSetLabelDateEdit.setPropertyValue("Align", new Short((short)0));
		xPSetLabelDateEdit.setPropertyValue("Name", _labelDateEditName);
		xPSetLabelDateEdit.setPropertyValue("TabIndex", new Short((short)99));        
		xPSetLabelDateEdit.setPropertyValue("Label", new String(" Edition Date"));
		// Description
		Object labelDescModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetLabelDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelDescModel);
		xPSetLabelDesc.setPropertyValue("PositionX", new Integer(247));
		xPSetLabelDesc.setPropertyValue("PositionY", new Integer(23));
		xPSetLabelDesc.setPropertyValue("Width", new Integer(90));
		xPSetLabelDesc.setPropertyValue("Height", new Integer(13));
		xPSetLabelDesc.setPropertyValue("Align", new Short((short)0));
		xPSetLabelDesc.setPropertyValue("Name", _labelDescName);
		xPSetLabelDesc.setPropertyValue("TabIndex", new Short((short)99));        
		xPSetLabelDesc.setPropertyValue("Label", new String(" Description"));
		
		// Create the list model and set the properties
		log.log(Level.DEBUG, "Create the list model and set the properties");
		Object listModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlListBoxModel");
		XPropertySet xPSetList = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, listModel);
		xPSetList.setPropertyValue("PositionX", new Integer(6));
		xPSetList.setPropertyValue("PositionY", new Integer(35));
		xPSetList.setPropertyValue("Width", new Integer(331));
		xPSetList.setPropertyValue("Height", new Integer(155));
		xPSetList.setPropertyValue("Name", _listName);
		FontDescriptor f = new FontDescriptor();
		f.Name = new String("Bitstream Vera Sans Mono");
		xPSetList.setPropertyValue("FontDescriptor", f);
		xPSetList.setPropertyValue("TabIndex", new Short((short)3));
		
		// Create the More Infos button and set the properties
		log.log(Level.DEBUG, "Create the More Infos button and set the properties");
		Object moreModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetMore = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, moreModel);
		xPSetMore.setPropertyValue("PositionX", new Integer(6));
		xPSetMore.setPropertyValue("PositionY", new Integer(194));
		xPSetMore.setPropertyValue("Width", new Integer(62));
		xPSetMore.setPropertyValue("Height", new Integer(14));
		xPSetMore.setPropertyValue("Name", _moreName);
		xPSetMore.setPropertyValue("TabIndex", new Short((short)5));
		xPSetMore.setPropertyValue("Label", new String("More options"));
		
		// Create the Progress bar and set the properties
		log.log(Level.DEBUG, "Create the Progress bar and set the properties");
		Object progressModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlProgressBarModel");
		XPropertySet xPSetProgress = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, progressModel);
		xPSetProgress.setPropertyValue("PositionX", new Integer(72));
		xPSetProgress.setPropertyValue("PositionY", new Integer(194));
		xPSetProgress.setPropertyValue("Width", new Integer(185));
		xPSetProgress.setPropertyValue("Height", new Integer(14));
		xPSetProgress.setPropertyValue("Name", _progressName);
		xPSetProgress.setPropertyValue("TabIndex", new Short((short)6));

		// Create the filename label
		Object labelFileNameModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlFixedTextModel");
		XPropertySet xPSetLabelFileNameDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, labelFileNameModel);
		xPSetLabelFileNameDesc.setPropertyValue("PositionX", new Integer(73));
		xPSetLabelFileNameDesc.setPropertyValue("PositionY", new Integer(197));
		xPSetLabelFileNameDesc.setPropertyValue("Width", new Integer(28));
		xPSetLabelFileNameDesc.setPropertyValue("Height", new Integer(10));
		xPSetLabelFileNameDesc.setPropertyValue("Align", new Short((short)0));
		xPSetLabelFileNameDesc.setPropertyValue("Name", _labelFileNameName);
		xPSetLabelFileNameDesc.setPropertyValue("TabIndex", new Short((short)20));        
		xPSetLabelFileNameDesc.setPropertyValue("Label", new String("File name: "));
		
		// Create the filename textbox
		Object textFileNameModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlEditModel");
		XPropertySet xPSetFileNameDesc = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, textFileNameModel);
		xPSetFileNameDesc.setPropertyValue("PositionX", new Integer(103));
		xPSetFileNameDesc.setPropertyValue("PositionY", new Integer(194));
		xPSetFileNameDesc.setPropertyValue("Width", new Integer(152));
		xPSetFileNameDesc.setPropertyValue("Height", new Integer(14));
		xPSetFileNameDesc.setPropertyValue("Align", new Short((short)0));
		xPSetFileNameDesc.setPropertyValue("Name", _textFileNameName);
		xPSetFileNameDesc.setPropertyValue("TabIndex", new Short((short)21));
		if (_xStorable.hasLocation()) {
			String localFilePath = URLDecoder.decode(_xModel.getURL().replaceFirst("file:///", ""));
			File file = new File(localFilePath);
			String fileName = file.getName();
			xPSetFileNameDesc.setPropertyValue("Text", fileName);
		}
		
		// Create the OK button model and set the properties
		log.log(Level.DEBUG, "Create the OK button model and set the properties");
		Object okModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetOk = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, okModel);
		xPSetOk.setPropertyValue("PositionX", new Integer(261));
		xPSetOk.setPropertyValue("PositionY", new Integer(194));
		xPSetOk.setPropertyValue("Width", new Integer(32));
		xPSetOk.setPropertyValue("Height", new Integer(14));
		xPSetOk.setPropertyValue("Name", _okName);
		xPSetOk.setPropertyValue("TabIndex", new Short((short)4));
		xPSetOk.setPropertyValue("Label", new String("Ok"));
		
		// Create the Cancel button model and set the properties
		log.log(Level.DEBUG, "Create the Cancel button model and set the properties");
		Object cancelModel = xMultiServiceFactory.createInstance(
		"com.sun.star.awt.UnoControlButtonModel");
		XPropertySet xPSetCancel = (XPropertySet)UnoRuntime.queryInterface(
				XPropertySet.class, cancelModel);
		xPSetCancel.setPropertyValue("PositionX", new Integer(295));
		xPSetCancel.setPropertyValue("PositionY", new Integer(194));
		xPSetCancel.setPropertyValue("Width", new Integer(42));
		xPSetCancel.setPropertyValue("Height", new Integer(14));
		xPSetCancel.setPropertyValue("Name", _cancelName);
		xPSetCancel.setPropertyValue("TabIndex", new Short((short)5));
		xPSetCancel.setPropertyValue("PushButtonType", new Short((short)2));
		xPSetCancel.setPropertyValue("Label", new String("Cancel"));
		
		// Insert the control models into the dialog model
		log.log(Level.DEBUG, "Insert the control models into the dialog model");
		XNameContainer xNameCont = (XNameContainer)UnoRuntime.queryInterface(
				XNameContainer.class, dialogModel);
		xNameCont.insertByName(_prevName, buttonModel);
		xNameCont.insertByName(_urlName, urlModel);
		xNameCont.insertByName(_labelFileName, labelFileModel);
		xNameCont.insertByName(_labelAuthorName, labelAuthorModel);
		xNameCont.insertByName(_labelDateCreaName, labelDateCreaModel);
		xNameCont.insertByName(_labelDateEditName, labelDateEditModel);
		xNameCont.insertByName(_labelDescName, labelDescModel);
		xNameCont.insertByName(_listName, listModel);
		xNameCont.insertByName(_progressName, progressModel);
		xNameCont.insertByName(_labelFileNameName, labelFileNameModel);
		xNameCont.insertByName(_textFileNameName, textFileNameModel);
		xNameCont.insertByName(_moreName, moreModel);
		xNameCont.insertByName(_okName, okModel);
		xNameCont.insertByName(_cancelName, cancelModel);
		
		// Create the dialog control and set the model
		log.log(Level.DEBUG, "Create the dialog control and set the model");
		Object dialog = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.UnoControlDialog", _xComponentContext);
		XControl xControl = (XControl)UnoRuntime.queryInterface(
				XControl.class, dialog);
		XControlModel xControlModel = (XControlModel)UnoRuntime.queryInterface(
				XControlModel.class, dialogModel);      
		xControl.setModel(xControlModel);
		
		// Events...
		XControlContainer xControlCont = (XControlContainer)UnoRuntime.queryInterface(
				XControlContainer.class, dialog);

		updateWebDav(xControlCont);

		// Add an action listener to the prev button control
		log.log(Level.DEBUG, "Add an action listener to the prev button control");
		Object objectPrev = xControlCont.getControl(_prevName);
		XButton xPrev = (XButton)UnoRuntime.queryInterface(XButton.class, objectPrev);
		xPrev.addActionListener(new OnPrevClick(xControlCont));
		// Add an action listener to the combo box control
		log.log(Level.DEBUG, "Add an action listener to the combo box control");
		Object objectUrl = xControlCont.getControl(_urlName);
		XComboBox xUrl = (XComboBox)UnoRuntime.queryInterface(XComboBox.class, objectUrl);
		xUrl.addItemListener(new OnUrlChange(xControlCont));
		// Add an action listener to the list box control
		log.log(Level.DEBUG, "Add an action listener to the list box control");
		Object objectList = xControlCont.getControl(_listName);
		XListBox xList = (XListBox)UnoRuntime.queryInterface(XListBox.class, objectList);
		xList.addActionListener(new OnListDblClick(xControlCont));
		// Add an action listener to the More Infos button control
		log.log(Level.DEBUG, "Add an action listener to the More Infos button control");
		Object objectMoreInfos = xControlCont.getControl(_moreName);
		XButton xMoreInfos = (XButton)UnoRuntime.queryInterface(XButton.class, objectMoreInfos);
		xMoreInfos.addActionListener(new OnMoreInfosClick(xControlCont));
		// Add an action listener to the Ok button control
		log.log(Level.DEBUG, "Add an action listener to the Ok button control");
		Object objectOk = xControlCont.getControl(_okName);
		XButton xOk = (XButton)UnoRuntime.queryInterface(XButton.class, objectOk);
		xOk.addActionListener(new OnOkClick(xControlCont));
		// Add an action listener to the Cancel button control
		log.log(Level.DEBUG, "Add an action listener to the Cancel button control");
		Object objectCancel = xControlCont.getControl(_cancelName);
		XButton xCancel = (XButton)UnoRuntime.queryInterface(XButton.class, objectCancel);
		xCancel.addActionListener(new OnCancelClick(xControlCont));
		
		// Create a peer
		log.log(Level.DEBUG, "Create a peer");
		Object toolkit = xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.awt.ExtToolkit", _xComponentContext);      
		XToolkit xToolkit = (XToolkit)UnoRuntime.queryInterface(
				XToolkit.class, toolkit);
		XWindow xWindow = (XWindow)UnoRuntime.queryInterface(
				XWindow.class, xControl);
		xWindow.setVisible(false);      
		xControl.createPeer(xToolkit, null);
		
		// Execute the dialog
		log.log(Level.DEBUG, "Execute the dialog");
		xDialog = (XDialog)UnoRuntime.queryInterface(
				XDialog.class, dialog);
		xDialog.execute();
		
		// Dispose the dialog
		log.log(Level.DEBUG, "Dispose the dialog");
		XComponent xComponent = (XComponent)UnoRuntime.queryInterface(
				XComponent.class, dialog);
		xComponent.dispose();
	}
	
	public boolean checkDoc() {
		return _xStorable.hasLocation();
		//return false;
	}
	
	private void updateWebDav(XControlContainer c) {
		TimeOut to = new TimeOut(c);
		UpdateWebDav t = new UpdateWebDav(c, to);
		t.start();
	}
	
	private void goIntoDav(XControlContainer c) {
		XListBox x = (XListBox)UnoRuntime.queryInterface(
				XListBox.class, c.getControl(_listName));
		log.log(Level.DEBUG, "-> OnListDblClick");
		
		String[] s = ((String[])_davFiles.elementAt(x.getSelectedItemPos()));
		
		if (s[2] == "COLLECTION") {
			_davPath += "/" + s[4];
			updateWebDav(c);
		} else {
			XTextComponent t = null;
			t = (XTextComponent)UnoRuntime.queryInterface(
					XTextComponent.class, c.getControl(_textFileNameName));
			t.setText(new String(s[4]));
		}
	}
	
	@SuppressWarnings("deprecation")
	private void saveIntoDav(XControlContainer c, String fileName, String path) {
		try {
			//String localFilePath = URLDecoder.decode(_xModel.getURL().replaceFirst("file:///", ""));
			WebDavStore dav = new WebDavStore();
			dav.setConnectionUrl(Settings.getServer());
			dav.setConnectionPort(Settings.getPort());
			dav.setConnectionBaseDirectory(_davPath);
			dav.setConnectionUsername(Settings.getUser());
			dav.setConnectionPassword(Settings.getPassword());
			//File f = new File(localFilePath);
			//String fileName = f.getName();
			dav.connect(fileName);
			dav.upload(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (java.io.IOException e) {
			log.error(e.getMessage());
		}
	}
	
	public void storeLocal(String url)
	{
		try {
			String path = com.sun.star.uri.ExternalUriReferenceTranslator.
			create(_xComponentContext).translateToInternal("file:///" + url.replace("\\", "/"));
			if (path.length() == 0) {
				throw new RuntimeException();
			}
			//_xStorable.store();
			PropertyValue[] loadProps = new PropertyValue[1];
			PropertyValue asTemplate = new PropertyValue();
			//asTemplate.Name = "InputStream";
			//asTemplate.Value = inputStreamToXInputStream(iStream);
			loadProps[0] = asTemplate;
			_xStorable.storeAsURL(path, loadProps);
		} catch (com.sun.star.io.IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private String formatList(String []t) {
		String res = new String();
		
		if (t[2] == "COLLECTION")
		{
			if (t[4].length() > 21)
				res = t[4].substring(0, 20) + "…";
			else
				res = t[4];
			//res = "► " + res; 
			res = "‣ " + res;
		}
		else
		{
			if (t[4].length() > 23)
				res = t[4].substring(0, 22) + "…";
			else
				res = t[4];
		}

		//int l = res.length();
		while (res.length() < 25)
			res += " ";
		
		res += t[6];
		while (res.length() < 42)
			res += " ";


		Locale locale = Locale.FRENCH;
		Date date = null;
		String s = null;

		try {
			date = DateFormat.getDateTimeInstance().parse(t[5]);
			s = DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(date);
			res+= s;
			while (res.length() < 57)
				res += " ";
		} catch (ParseException e) {
			e.printStackTrace();
			log.log(Level.DEBUG, e.getLocalizedMessage());
		}
		date = null;
		s = null;
		try {
			date = DateFormat.getDateTimeInstance().parse(t[3]);
			s = DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(date);
			res+= s;
			while (res.length() < 71)
				res += " ";
		} catch (ParseException e) {
			e.printStackTrace();
			log.log(Level.DEBUG, e.getLocalizedMessage());
		}

		return res;
	}
	
	public static void showMessageBox(String sTitle, String sMessage) {
        try {
            if (null != _xFrame && null != _xToolkit) {

                // describe window properties.
                WindowDescriptor aDescriptor = new WindowDescriptor();
                aDescriptor.Type              = WindowClass.MODALTOP;
                aDescriptor.WindowServiceName = new String("infobox");
                aDescriptor.ParentIndex       = -1;
                aDescriptor.Parent            = (XWindowPeer)UnoRuntime.queryInterface(
                		XWindowPeer.class, _xFrame.getContainerWindow());
                aDescriptor.Bounds            = new Rectangle(0,0,350,250);
                aDescriptor.WindowAttributes  = WindowAttribute.BORDER |
                WindowAttribute.MOVEABLE |
                WindowAttribute.CLOSEABLE;
                
                XWindowPeer xPeer = _xToolkit.createWindow(aDescriptor);
                if (null != xPeer) {
                	XMessageBox xMsgBox = (XMessageBox)UnoRuntime.queryInterface(
                			XMessageBox.class, xPeer);
                	if (null != xMsgBox)
                	{
                		xMsgBox.setCaptionText(sTitle);
                		xMsgBox.setMessageText(sMessage);
                		xMsgBox.execute();
                	}
                }
            }
        } catch (com.sun.star.uno.Exception e) {
        	// do your error handling 
        }
    }
	
	/**
	 * OnPrevClick - Called when the user press the "Prev" button in the Open Document dialog
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
			log.log(Level.DEBUG, "-> OnPrevClick");
			int i = _davPath.lastIndexOf('/');
			if (i == 0 || i == -1)
				_davPath = "/";
			else
				_davPath = _davPath.substring(0, i);
			updateWebDav(_xControlCont);
		}
	}
	
	/**
	 * OnUrlChange - Called when the user change the Url in the Open Document dialog
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
	
	/**
	 * OnListDblClick - Called when the user double click on a documment in the Open Document dialog
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
			goIntoDav(_xControlCont);
		}
	}
	
	/**
	 * OnMoreInfosClick - Called when the user press the "More Infos" button in the Open Document dialog
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
			MoreInfos m = new MoreInfos(_xComponentContext);
			try {
				m.createDialog();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * OnOkClick - Called when the user press the "Ok" button in the Open Document dialog
	 */
	public class OnOkClick implements XActionListener {
		
		private XControlContainer _xControlCont;
		
		public OnOkClick(XControlContainer xControlCont) {
			_xControlCont = xControlCont;
		}
		
		// XEventListener
		public void disposing(EventObject eventObject) {
			_xControlCont = null;
		}
		
		// XActionListener
		public void actionPerformed(ActionEvent actionEvent) {
			XTextComponent t = null;
			t = (XTextComponent)UnoRuntime.queryInterface(
					XTextComponent.class, _xControlCont.getControl(_textFileNameName));
			if (t.getText().length() == 0) {
				showMessageBox("Error", "You must enter a file name before publishing your document.");
				return ;
			}
			String path = new String(LocalFileSystem.getLocalPath(_davPath, t.getText()));
			
			//t.setText();
			storeLocal(path);
			
			
			saveIntoDav(_xControlCont, t.getText(), path);

			try {
				_xDocumentInfo.setUserFieldName((short) 0, "PengYouDocument");
				_xDocumentInfo.setUserFieldValue((short) 0, "true");
				_xDocumentInfo.setUserFieldName((short) 1, "PengYouLocation");
				_xDocumentInfo.setUserFieldValue((short) 1, _davPath);
				_xDocumentInfo.setUserFieldName((short) 2, "PengYouFile");
				_xDocumentInfo.setUserFieldValue((short) 2, t.getText());
				_xStorable.store();
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (com.sun.star.io.IOException e) {
				e.printStackTrace();
			}
			xDialog.endExecute();
		}
	}	
	
	/**
	 * OnCancelClick - Called when the user press the "Cancel" button in the Open Document dialog
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
        	log.log(Level.DEBUG, "SORT : \"" + s2[4] + "\".compareToIgnoreCase(\"" + s1[4] + "\") = " + String.valueOf(s2[4].compareToIgnoreCase(s1[4])));
        	return s2[4].compareToIgnoreCase(s1[4]);
        }
	}

	private class UpdateWebDav implements Runnable {

		private Thread thread;
		private XControlContainer m_c;
		private TimeOut m_to;
		
		public UpdateWebDav(XControlContainer c, TimeOut to) {
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
			
			log.log(Level.DEBUG, "updateWebDav();");
			
			// Lancement du timeout
			m_to.start();
			
			// Disable PERV, URLLIST, LIST, MOREINFO, OK
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_prevName))).setEnable(false);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_urlName))).setEnable(false);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_listName))).setEnable(false);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_moreName))).setEnable(false);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_okName))).setEnable(false);
			
			// Mise a jour de l'URL
			XComboBox u = (XComboBox)UnoRuntime.queryInterface(
					XComboBox.class, m_c.getControl(_urlName));
			u.addItem(_davPath, (short) 0);
			XTextComponent t = (XTextComponent)UnoRuntime.queryInterface(
					XTextComponent.class, m_c.getControl(_urlName));
			t.setText(_davPath);
			
			// Mise a jour de la ListBox
			XListBox x = (XListBox)UnoRuntime.queryInterface(
					XListBox.class, m_c.getControl(_listName));
			
			log.log(Level.DEBUG, "x.removeItems((short)0, " + String.valueOf(x.getItemCount()) + ")");
			x.removeItems((short)0, x.getItemCount());
			
			log.log(Level.DEBUG, "new DavContext('" + _davServer + "', " + String.valueOf(_davPort) + ", '" + _davPath + "');");
			
			WebDavStore dav = new WebDavStore();
			dav.setConnectionUrl(_davServer);
			dav.setConnectionPort(_davPort);
			dav.setConnectionUsername(_davUser);
			dav.setConnectionPassword(_davPass);
			dav.setConnectionBaseDirectory(_davPath);
			
			dav.connect("");
			
			log.log(Level.DEBUG, "new DavResource(\"/\", context);");
			log.log(Level.DEBUG, "dav.isCollection();");
			
			if (dav.isCollection())
				log.log(Level.DEBUG, "dav.isCollection() = true -> Repertoire");
			else
				log.log(Level.DEBUG, "dav.isCollection() = false -> Fichier");
			
			log.log(Level.DEBUG, "_davFiles = dav.listBasic();");
			
			try {
				_davFiles = dav.listBasic();
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			log.log(Level.DEBUG, "_davFiles.size() = " + _davFiles.size());
			Collections.sort(_davFiles, new Comparer());
			Iterator it = _davFiles.iterator();
			short i = 0;
			while (it.hasNext())
			{
				String[] s = (String[]) it.next();
				if (s[4].indexOf(".") == 0) {
					it.remove();
					continue ;
				}
				s[6] = new String("Jérémy");
				x.addItem(formatList(s), i++);
			}
			
			
			x.makeVisible((short) 0);
			
			// Enable PERV, URLLIST, LIST, MOREINFO, OK
			if (_davPath.compareTo(_davBasePath) != 0)
			{
				((XWindow)UnoRuntime.queryInterface(
						XWindow.class, m_c.getControl(_prevName))).setEnable(true);
			}
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_urlName))).setEnable(true);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_listName))).setEnable(true);
			((XWindow)UnoRuntime.queryInterface(
					XWindow.class, m_c.getControl(_moreName))).setEnable(true);
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

}
	