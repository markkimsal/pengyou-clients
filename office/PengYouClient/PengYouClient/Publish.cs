using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Threading;

namespace PengYouClient
{
    public partial class Publish : Form
    {
        delegate void SetDownloadState();
        SetDownloadState d_SetDownloadState;
        delegate void CloseWindows();
        CloseWindows d_CloseWindows;
        delegate void ErrorWindow();
        ErrorWindow d_ErrorWindow;

        DavContext context;
        DavRessource ressource;

        Word._Application app;
        string publishpath;
        string ress_name;

        bool publishsuccess = false;
        bool error_send = false;
        string error_reason;

        Thread PublishDocumentThread;
        Thread ShowDownloadStateThread;

        FileStream fs;

        string basedir = "http://192.168.3.31:8080/py/repository/default/";

        public Publish(object app)
        {
            d_SetDownloadState = new SetDownloadState(this.SetDownloadStateMethod);
            d_CloseWindows = new CloseWindows(this.CloseWindowsMethod);
            d_ErrorWindow = new ErrorWindow(this.LaunchErrorWindow);
            this.app = (Word._Application)app;

            if (IsPengYouDocument() == true && GetPublishPath() == true)
            {
                PublishDoc();
            }
            else
            {
                error_reason = "This document is not a valid PengYou Document";
                LaunchErrorWindow();
            }

            InitializeComponent();
        }

        private void LaunchErrorWindow()
        {
            string caption = "Connection error";
            MessageBoxButtons buttons = MessageBoxButtons.OK;
            DialogResult result;

            result = MessageBox.Show(this, this.error_reason, caption, buttons);
            if (result == DialogResult.OK)
            {
                this.PublishDocumentThread.Abort();
                this.ShowDownloadStateThread.Abort();
                this.Close();
            }
        }

        public void CloseWindowsMethod()
        {
            PublishDocumentThread.Abort();
            ShowDownloadStateThread.Abort();
            this.Close();
        }

        bool GetWordDocumentPropertie(string propName, out string prop)
        {
            object customPropertiesObject = app.ActiveDocument.CustomDocumentProperties;
            Type propertyType = customPropertiesObject.GetType();
            prop = null;

            try
            {
                object property = propertyType.InvokeMember("Item",
                System.Reflection.BindingFlags.Default |
                System.Reflection.BindingFlags.GetProperty, null, customPropertiesObject,
                new object[] { propName });
                Type validatedType = property.GetType();
                string propValue = validatedType.InvokeMember("Value",
                System.Reflection.BindingFlags.Default |
                System.Reflection.BindingFlags.GetProperty, null, property, new object[] { }).ToString();
                prop = propValue;
                return true;
            }
            catch (Exception)
            {
                return false;
            }            
        }

        bool GetPublishPath()
        {
            bool success = GetWordDocumentPropertie("PengYouBaseDir", out publishpath);
            return success;
        }

        bool IsPengYouDocument()
        {
            string prop;

            bool success = GetWordDocumentPropertie("PengYou", out prop);
            return success;
        }

        public void SetDownloadStateMethod()
        {
            ulong fileSize = (ulong)fs.Length;
            ulong sendedBytes = ressource.sendContainer.TotalBytes;
            string state;

            state = sendedBytes.ToString();
            state += " octets sur ";
            state += fileSize.ToString();

            ulong value_long = (sendedBytes * 100) / fileSize;
            int value = Convert.ToInt32(value_long);
            this.DownloadBar.Value = value;

            this.DownloadState.Text = state;
        }

        public class ShowDownloadState
        {
            Publish pub;

            public ShowDownloadState(Publish pub)
            {
                this.pub = pub;
            }

            public void LauchThread()
            {
                Thread.Sleep(500);
                while (pub.publishsuccess == false && pub.error_send == false)
                {
                    pub.Invoke(pub.d_SetDownloadState);
                    Thread.Sleep(1000);
                }
                if (pub.publishsuccess == true && pub.error_send == false)
                    pub.Invoke(pub.d_CloseWindows);
                else if (pub.publishsuccess == false && pub.error_send == true)
                    pub.Invoke(pub.d_ErrorWindow);                   
            }
        }

        public class PublishDocument
        {
            Publish pub;

            public PublishDocument(Publish pub)
            {
                this.pub = pub;
            }

            public void LaunchThread()
            {              
                pub.context = new DavContext("http://192.168.3.31", 8080, "fr", "fr");
                pub.ressource = new DavRessource(pub.ress_name, pub.context);

                int lenght = (int)pub.fs.Length;
                byte[] bytetab = new byte[lenght];
                pub.fs.Read(bytetab, 0, lenght);
                if (pub.ressource.SendRessource(bytetab) == false)
                {
                    pub.error_send = true;
                    pub.error_reason = pub.ressource.ResultReason;                    
                }
                else
                    pub.publishsuccess = true;
            }
        }

        void PublishDoc()
        {
            try
            {
                app.ActiveDocument.Save();
                string docpath = app.ActiveDocument.Path + "\\" + app.ActiveDocument.Name;
                string docname = app.ActiveDocument.Name;

                this.fs = new FileStream(docpath, FileMode.Open, FileAccess.Read, FileShare.ReadWrite);
                this.ress_name = this.basedir + this.publishpath + docname;

            }
            catch (Exception)
            {

            }

            PublishDocument m_th = new PublishDocument(this);
            PublishDocumentThread = new Thread(new ThreadStart(m_th.LaunchThread));
            PublishDocumentThread.Start();

            ShowDownloadState n_th = new ShowDownloadState(this);
            ShowDownloadStateThread = new Thread(new ThreadStart(n_th.LauchThread));
            ShowDownloadStateThread.Start();
        }
    }
}