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


import java.io.IOException;

import org.pengyou.ooo.AboutBox;
import org.pengyou.ooo.OpenDialog;
import org.pengyou.ooo.Publish;
import org.pengyou.ooo.PublishAsDialog;
import org.pengyou.ooo.SelectVersion;
import org.pengyou.ooo.SettingsDialog;
import org.pengyou.ooo.utils.ProgressBox;

import com.sun.star.awt.Rectangle;
import com.sun.star.awt.WindowAttribute;
import com.sun.star.awt.WindowClass;
import com.sun.star.awt.WindowDescriptor;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.frame.DispatchDescriptor;
import com.sun.star.frame.XDispatch;
import com.sun.star.frame.XDispatchProvider;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XStatusListener;
import com.sun.star.lang.XInitialization;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public class ProtocolHandlerAddon {
	
	/** This class implements the component. At least the interfaces XServiceInfo,
	 * XTypeProvider, and XInitialization should be provided by the service.
	 */
	public static class ProtocolHandlerAddonImpl extends WeakBase implements
			XDispatchProvider,
			XDispatch,                  
			XInitialization,
			XServiceInfo {
		
		/** The service name, that must be used to get an instance of this service.
		 */
		static private final String[] m_serviceNames = { "com.sun.star.frame.ProtocolHandler" };
    
		/** The component context, that gives access to the service manager and all registered services.
		 */
		private XComponentContext m_xCmpCtx;

		/** The toolkit, that we can create UNO dialogs.
         */
		private static XToolkit m_xToolkit;

        /** The frame where the addon depends on.
         */
		private static XFrame m_xFrame;
		
        /** The constructor of the inner class has a XMultiServiceFactory parameter.
         * @param xmultiservicefactoryInitialization A special service factory
         * could be introduced while initializing.
         */
		public ProtocolHandlerAddonImpl(XComponentContext xComponentContext) {
			m_xCmpCtx = xComponentContext;
		}
    
        /** This method is a member of the interface for initializing an object
         * directly after its creation.
         * @param object This array of arbitrary objects will be passed to the
         * component after its creation.
         * @throws Exception Every exception will not be handled, but will be
         * passed to the caller.
         */
        public void initialize(Object[] object)
            throws com.sun.star.uno.Exception {

            if (object.length > 0)
            {
                m_xFrame = (XFrame)UnoRuntime.queryInterface(
                    XFrame.class, object[0]);
            }

            // Create the toolkit to have access to it later
            m_xToolkit = (XToolkit)UnoRuntime.queryInterface(
            		XToolkit.class,
            		m_xCmpCtx.getServiceManager().createInstanceWithContext("com.sun.star.awt.Toolkit",
            				m_xCmpCtx));
            /*
            if (firstTime == true)
            {
            	showMessageBox("initialize", "initialize");
            	firstTime = false;
            }
            */
        }
    
        /** This method returns an array of all supported service names.
         * @return Array of supported service names.
         */
        public String[] getSupportedServiceNames() {
        	return getServiceNames();
        }
        
        public static String[] getServiceNames() {
        	return m_serviceNames;
        }
        
        /** This method returns true, if the given service will be
         * supported by the component.
         * @param stringService Service name.
         * @return True, if the given service name will be supported.
         */
        public boolean supportsService(String sService) {
            int len = m_serviceNames.length;
        
            for (int i=0; i < len; i++) {
                if (sService.equals(m_serviceNames[i]))
                    return true;
            }
        
            return false;
        }
    
        /** Return the class name of the component.
         * @return Class name of the component.
         */
        public String getImplementationName() {
            return ProtocolHandlerAddonImpl.class.getName();
        }
        
        // XDispatchProvider
        public XDispatch queryDispatch(com.sun.star.util.URL aURL,
        		String sTargetFrameName,
        		int iSearchFlags) {
        	
        	XDispatch xRet = null;
        	if (aURL.Protocol.compareTo("org.openoffice.Office.addon.pengyou:") == 0)
        	{
        		if (aURL.Path.compareTo("Open") == 0)
        			xRet = this;
        		if (aURL.Path.compareTo("Publish") == 0)
        			xRet = this;
        		if (aURL.Path.compareTo("PublishAs") == 0)
            		xRet = this;
            	if (aURL.Path.compareTo("OpenAtPreviousDate") == 0)
            		xRet = this;
            	if (aURL.Path.compareTo("EditTags") == 0)
            		xRet = this;
            	if (aURL.Path.compareTo("EditTheDescription") == 0)
            		xRet = this;
            	if (aURL.Path.compareTo("RightsManagement") == 0)
            		xRet = this;
            	if (aURL.Path.compareTo("Export") == 0)
            		xRet = this;
            	if (aURL.Path.compareTo("Settings") == 0)
            		xRet = this;
            	if (aURL.Path.compareTo("Help") == 0)
            		xRet = this;
            	if (aURL.Path.compareTo("About") == 0)
            		xRet = this;
             }
            return xRet;
        }
        
        public XDispatch[] queryDispatches(DispatchDescriptor[] seqDescripts) {
        	int nCount = seqDescripts.length;
        	XDispatch[] lDispatcher = new XDispatch[nCount];
        	
        	for (int i=0; i<nCount; ++i)
        		lDispatcher[i] = queryDispatch(seqDescripts[i].FeatureURL,
        				seqDescripts[i].FrameName,
        				seqDescripts[i].SearchFlags);

        	return lDispatcher;           
        }
        
        // XDispatch
        public void dispatch(com.sun.star.util.URL aURL,
        		com.sun.star.beans.PropertyValue[] aArguments) {
        	
        	if (aURL.Protocol.compareTo("org.openoffice.Office.addon.pengyou:") == 0)
        	{      	
        		if (aURL.Path.compareTo("Open") == 0)
        		{
        				OpenDialog s = null;
						try {
							s = new OpenDialog(m_xCmpCtx);
							s.createDialog();
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
        		}
        		else if (aURL.Path.compareTo("Publish") == 0)
            	{
            		Publish p = new Publish(m_xCmpCtx);
            		if (p.checkDoc())
            		{
            			p.storeLocal();
            			try {
							p.storeRemote();
						} catch (IOException e) {
							e.printStackTrace();
						}
            		}
            		else
            			showMessageBox("Error", "This document is not a PengYou document");
            	}
        		else if (aURL.Path.compareTo("PublishAs") == 0)
        		{
    				PublishAsDialog pa = null;
					try {
						pa = new PublishAsDialog(m_xToolkit, m_xFrame, m_xCmpCtx);
	            		//if (pa.checkDoc())
	            		//{	
	            			pa.createDialog();
	            		//}
	            		//else
	            			//showMessageBox("Publish as...", "You must save the document before.");
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
        		}
        		else if (aURL.Path.compareTo("OpenAtPreviousDate") == 0) {
            		SelectVersion s = null;
            		try {
            			s = new SelectVersion(m_xCmpCtx);
            			if (s.checkDoc()) {
            				s.createDialog();
            			}
            			else
            				showMessageBox("Error", "This document is not a PengYou document");
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
            	}
        		else if (aURL.Path.compareTo("EditTags") == 0)
            		showMessageBox("EditTags", "EditTags");
        		else if (aURL.Path.compareTo("EditTheDescription") == 0)
            		showMessageBox("EditTheDescription", "EditTheDescription");
        		else if (aURL.Path.compareTo("RightsManagement") == 0)
            		showMessageBox("RightsManagement", "RightsManagement");
        		else if (aURL.Path.compareTo("Export") == 0)
            		showMessageBox("Export", "Export");
        		else if (aURL.Path.compareTo("Settings") == 0) {
            		SettingsDialog s = null;
            		try {
            			s = new SettingsDialog(m_xCmpCtx);
						s.createDialog();
					} catch (IOException e) {
							e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
            	}
        		else if (aURL.Path.compareTo("Help") == 0) {
        			ProgressBox pb = new ProgressBox(m_xCmpCtx, "Downloading document...");
        			pb.start();
        			for (int i = 0; i < 100; i++) {
        				try {
							Thread.sleep((long)100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (pb.update("Coucou " + (i+1) + "% de la tralala ouplou.", i) == false) {
							break;
						}
        			}
        			pb.stop();
        		} else if (aURL.Path.compareTo("About") == 0) {
            		AboutBox ab;
					ab = new AboutBox(m_xCmpCtx);
            		try {
						ab.createDialog();
					} catch (Exception e) {
						e.printStackTrace();
					}
               	}
            }
        }
        
        public void addStatusListener(XStatusListener xControl,
                                      com.sun.star.util.URL aURL) {
        }
        
        public void removeStatusListener(XStatusListener xControl,
                                         com.sun.star.util.URL aURL) {
        }

        public static void showMessageBox(String sTitle, String sMessage) {
            try {
                if (null != m_xFrame && null != m_xToolkit) {

                    // describe window properties.
                    WindowDescriptor aDescriptor = new WindowDescriptor();
                    aDescriptor.Type              = WindowClass.MODALTOP;
                    aDescriptor.WindowServiceName = new String("infobox");
                    aDescriptor.ParentIndex       = -1;
                    aDescriptor.Parent            = (XWindowPeer)UnoRuntime.queryInterface(
                    		XWindowPeer.class, m_xFrame.getContainerWindow());
                    aDescriptor.Bounds            = new Rectangle(0,0,300,200);
                    aDescriptor.WindowAttributes  = WindowAttribute.BORDER |
                    WindowAttribute.MOVEABLE |
                    WindowAttribute.CLOSEABLE;
                    
                    XWindowPeer xPeer = m_xToolkit.createWindow(aDescriptor);
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
 	}
            
	/** Gives a factory for creating the service.
	 * This method is called by the <code>JavaLoader</code>
     * <p>
     * @return Returns a <code>XSingleServiceFactory</code> for creating the
     * component.
     * @see com.sun.star.comp.loader.JavaLoader#
     * @param stringImplementationName The implementation name of the component.
     * @param xmultiservicefactory The service manager, who gives access to every
     * known service.
     * @param xregistrykey Makes structural information (except regarding tree
     * structures) of a single
     * registry key accessible.
     */
	public static XSingleComponentFactory __getComponentFactory(String sImplementationName) {
		XSingleComponentFactory xFactory = null;
    	
		if (sImplementationName.equals( ProtocolHandlerAddonImpl.class.getName()))
			xFactory = Factory.createComponentFactory(ProtocolHandlerAddonImpl.class,
					ProtocolHandlerAddonImpl.getServiceNames());
		
		return xFactory;
	}
	
    /** Writes the service information into the given registry key.
     * This method is called by the <code>JavaLoader</code>.
     * @return returns true if the operation succeeded
     * @see com.sun.star.comp.loader.JavaLoader#
     * @see com.sun.star.lib.uno.helper.Factory#
     * @param xregistrykey Makes structural information (except regarding tree
     * structures) of a single
     * registry key accessible.
     */
	public static boolean __writeRegistryServiceInfo(XRegistryKey xRegistryKey) {
		return Factory.writeRegistryServiceInfo(
				ProtocolHandlerAddonImpl.class.getName(),
				ProtocolHandlerAddonImpl.getServiceNames(),
				xRegistryKey);
	}
}