using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;

namespace PengYouClient
{
    public partial class Open_Download : Form 
    {
        delegate void SetDownloadState();
        delegate void CloseWindows();
        SetDownloadState d_SetDownloadState;
        CloseWindows d_CloseWindows;

        Open open;
        DavRessource ress;
        Thread OpenRemoteDocumentThread;
        Thread ShowDownloadStateThread;
        public string label;

        public Open_Download(Open open, DavRessource ress)
        {
            this.open = open;
            this.ress = ress;
            d_SetDownloadState = new SetDownloadState(this.SetDownloadStateMethod);
            d_CloseWindows = new CloseWindows(this.CloseWindowsMethod);
            InitializeComponent();
        }

        public void SetDownloadStateMethod()
        {
            ulong fileSize = ress.fileSize;
            ulong downloadedBytes = ress.downloadedBytes;

            string label;
            label = ress.receiveContainer.TotalBytes.ToString();
            label += " octets sur ";
            label += ress.receiveContainer.TotalBytesExpected.ToString();

            ulong value_long = (downloadedBytes * 100) / fileSize;
            int value = Convert.ToInt32(value_long);
            this.DownloadBar.Value = value;

            this.DownloadState.Text = label;
        }

        public void CloseWindowsMethod()
        {
            OpenRemoteDocumentThread.Abort();
            ShowDownloadStateThread.Abort();
            this.Close();
        }

        public class OpenRemoteDocument
        {
            Open open;
            DavRessource ress;

            public OpenRemoteDocument(Open open, DavRessource ress)
            {
                this.open = open;
                this.ress = ress;
            }

            public void LaunchThread()
            {
                bool success;
                success = ((DavRessource)ress).GetRessource(out open.RemoteFile);
                open.GetRemoteFile = success;
            }
        }

        public class ShowDownloadState
        {
            Open open;
            Open_Download op;

            public ShowDownloadState(Open open, Open_Download op)
            {
                this.open = open;
                this.op = op;
            }

            public void LauchThread()
            {
                Thread.Sleep(500);
                while (open.GetRemoteFile == false)
                {
                    Thread.Sleep(1000);
                    op.Invoke(op.d_SetDownloadState); 
                }
                op.Invoke(op.d_CloseWindows);
            }
        }

        private void Open_Download_Shown(object sender, EventArgs e)
        {
            OpenRemoteDocument m_th = new OpenRemoteDocument(open, ress);
            OpenRemoteDocumentThread = new Thread(new ThreadStart(m_th.LaunchThread));
            OpenRemoteDocumentThread.Start();

            ShowDownloadState n_th = new ShowDownloadState(open, this);
            ShowDownloadStateThread = new Thread(new ThreadStart(n_th.LauchThread));
            ShowDownloadStateThread.Start();
        }     
    }
}