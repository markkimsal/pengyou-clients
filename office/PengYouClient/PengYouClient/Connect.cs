namespace PengYouClient
{
	using System;
	using Extensibility;
	using System.Runtime.InteropServices;
    using System.Reflection;
    using Microsoft.Office.Core;
    using System.IO;

	#region Read me for Add-in installation and setup information.
	// When run, the Add-in wizard prepared the registry for the Add-in.
	// At a later time, if the Add-in becomes unavailable for reasons such as:
	//   1) You moved this project to a computer other than which is was originally created on.
	//   2) You chose 'Yes' when presented with a message asking if you wish to remove the Add-in.
	//   3) Registry corruption.
	// you will need to re-register the Add-in by building the PengYouClientSetup project, 
	// right click the project in the Solution Explorer, then choose install.
	#endregion
	
	/// <summary>
	///   The object for implementing an Add-in.
	/// </summary>
	/// <seealso class='IDTExtensibility2' />
	[GuidAttribute("0CEBEDF8-C8E8-4C34-B31C-567E24EFCCF8"), ProgId("PengYouClient.Connect")]
	public class Connect : Object, Extensibility.IDTExtensibility2
	{
		/// <summary>
		///		Implements the constructor for the Add-in object.
		///		Place your initialization code within this method.
		/// </summary>
        /// 

        private CommandBarButton Open;
        private CommandBarButton Publish;
        private CommandBarButton PublishAs;
        private CommandBarPopup PengYouMenu;
        public Word._Application app;

        string filename;
        string realname;

        public string Filename
        {
            set
            {
                filename = value;
            }
        }

        public string RealFilename
        {
            set
            {
                realname = value;
            }
        }

		public Connect()
		{
		}

        public void OnConnection(object application, Extensibility.ext_ConnectMode connectMode, object addInInst, ref System.Array custom)
        {
            applicationObject = application;
            addInInstance = addInInst;   
            
            if (connectMode != Extensibility.ext_ConnectMode.ext_cm_Startup)
            {
                OnStartupComplete(ref custom);
            }

        }

        public void OnDisconnection(Extensibility.ext_DisconnectMode disconnectMode, ref System.Array custom)
        {
            if (disconnectMode != Extensibility.ext_DisconnectMode.ext_dm_HostShutdown)
            {
                OnBeginShutdown(ref custom);
            }
            applicationObject = null;
        }


        public void OnAddInsUpdate(ref System.Array custom)
        {
        }

        public void OnStartupComplete(ref System.Array custom)
        {
            CommandBars oCommandBars;
            CommandBar oStandardBar;
            object omissing = System.Reflection.Missing.Value;

            app = (Word._Application)applicationObject;

            try
            {
                oCommandBars = (CommandBars)applicationObject.GetType().InvokeMember("CommandBars", BindingFlags.GetProperty, null, applicationObject, null);
            }
            catch (Exception)
            {
                // Outlook has the CommandBars collection on the Explorer object.
                object oActiveExplorer;
                oActiveExplorer = applicationObject.GetType().InvokeMember("ActiveExplorer", BindingFlags.GetProperty, null, applicationObject, null);
                oCommandBars = (CommandBars)oActiveExplorer.GetType().InvokeMember("CommandBars", BindingFlags.GetProperty, null, oActiveExplorer, null);
            }

            // Set up a custom button on the "Standard" commandbar.
            try
            {
                oStandardBar = oCommandBars["Menu Bar"];
            }
            catch (Exception)
            {
                // Access names its main toolbar Database.
                oStandardBar = oCommandBars["Database"];
            }

            // In case the button was not deleted, use the exiting one.
            try
            {                
                PengYouMenu = (CommandBarPopup)oStandardBar.Controls["Documents distants"];
                Open = (CommandBarButton)PengYouMenu.Controls["Ouvrir..."];
                Publish = (CommandBarButton)PengYouMenu.Controls["Publier"];
                PublishAs = (CommandBarButton)PengYouMenu.Controls["Publier sous..."];
               // Publish.BeginGroup = true;

            }
            catch (Exception)
            {
                PengYouMenu = (CommandBarPopup)oStandardBar.Controls.Add(MsoControlType.msoControlPopup, omissing, omissing, omissing, true);
                PengYouMenu.Caption = "Documents distants";
                PengYouMenu.Tag = PengYouMenu.Caption;

                Open = (CommandBarButton)PengYouMenu.Controls.Add(1, omissing, omissing, omissing, omissing);
                Open.Caption = "Ouvrir...";
                Open.Tag = Open.Caption;

                Publish = (CommandBarButton)PengYouMenu.Controls.Add(1, omissing, omissing, omissing, omissing);
                Publish.Caption = "Publier";
                Publish.Tag = Publish.Caption;

                PublishAs = (CommandBarButton)PengYouMenu.Controls.Add(1, omissing, omissing, omissing, omissing);
                PublishAs.Caption = "Publier sous...";
                PublishAs.Tag = PublishAs.Caption;
                //Publish.BeginGroup = true;
            }

            Publish.Visible = true;
            Publish.Click += new Microsoft.Office.Core._CommandBarButtonEvents_ClickEventHandler(this.Publish_Click);

            PublishAs.Visible = true;
            PublishAs.Click += new Microsoft.Office.Core._CommandBarButtonEvents_ClickEventHandler(this.PublishAs_Click);

            Open.Visible = true;
            Open.Click += new Microsoft.Office.Core._CommandBarButtonEvents_ClickEventHandler(this.Open_Click);

            object oName = applicationObject.GetType().InvokeMember("Name", BindingFlags.GetProperty, null, applicationObject, null);         
            oStandardBar = null;
            oCommandBars = null;
            
        }

        public void OnBeginShutdown(ref System.Array custom)
        {
            object omissing = System.Reflection.Missing.Value;

            Open.Delete(omissing);
            Open = null;

            Publish.Delete(omissing);
            Publish = null;

            PublishAs.Delete(omissing);
            PublishAs = null;

            PengYouMenu.Delete(omissing);
            PengYouMenu = null;
        }

        private void OpenWordDocument()
        {
            string pengyoupath = @"c:\pengyou\";
            try
            {
                File.Delete(pengyoupath + realname);
                File.Copy(this.filename, pengyoupath + realname);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }

            object file = pengyoupath + realname;

            object omissing = Missing.Value;
            Word.Document doc = app.Documents.Open(ref file, ref omissing, ref omissing, ref omissing, ref omissing,
 ref omissing, ref omissing, ref omissing, ref omissing, ref omissing, ref omissing,
 ref omissing, ref omissing, ref omissing, ref omissing, ref omissing);
            doc.Activate();
        }

        private void Open_Click(CommandBarButton cmdBarbutton, ref bool cancel)
        {
           Open DialogOpen = new Open(app, this);
           DialogOpen.ShowDialog();
           OpenWordDocument();
        }

        private void Publish_Click(CommandBarButton cmdBarbutton, ref bool cancel)
        {
            Publish DialogPublish = new Publish(app);
            DialogPublish.ShowDialog();
        }

        private void PublishAs_Click(CommandBarButton cmdBarbutton, ref bool cancel)
        {
            System.Windows.Forms.MessageBox.Show("hehe");
        }
		
		private object applicationObject;
		private object addInInstance;
	}
}