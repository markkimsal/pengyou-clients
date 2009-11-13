using System;
using System.IO;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;
using System.Reflection;

namespace PengYouClient
{
    public partial class Open : Form
    {
        delegate void DelegateIncrementProgressBar();
        delegate void DelegateDrawFileList();
        delegate void DelegateRemoveProgressBar();
        delegate void DelegateCreateProgressBar();
        delegate void DelegateInhibControl_Off();
        delegate void DelegateInhibControl_On();
        DelegateIncrementProgressBar m_DelegateIncrementProgressBar;
        DelegateDrawFileList m_DelegateDrawFileList;
        DelegateRemoveProgressBar m_DelegateRemoveProgressBar;
        DelegateCreateProgressBar m_DelegateCreateProgressBar;
        DelegateInhibControl_Off m_DelegateInhibControl_Off;
        DelegateInhibControl_On m_DelegateInhibControl_On;

        public DavContext context;
        ArrayList directory_listing;
        SortDirection direction;
        string CurrentDir;
        int ComboIndex = 0;
        public bool getlisting;
        bool ProgressBarExists = false;

        bool getDirectoryListing_success;
        string error_reason;

        public bool GetRemoteFile = false;
        public FileStream RemoteFile;

        Thread FillThread;
        Thread ProgressBarThread;

        Word._Application app;
        Connect connect;

        string basedir = "http://192.168.3.31:8080/py/repository/default/";

        public Open(object app, Connect connect)
        {
            m_DelegateIncrementProgressBar = new DelegateIncrementProgressBar(this.IncrementProgressBar);
            m_DelegateDrawFileList = new DelegateDrawFileList(this.DrawFileList);
            m_DelegateRemoveProgressBar = new DelegateRemoveProgressBar(this.RemoveProgressBar);
            m_DelegateCreateProgressBar = new DelegateCreateProgressBar(this.CreateProgressBar);
            m_DelegateInhibControl_Off = new DelegateInhibControl_Off(this.InhibControls_Off);
            m_DelegateInhibControl_On = new DelegateInhibControl_On(this.InhibControls_On);

            this.app = (Word._Application)app;
            this.connect = connect;

            InitializeComponent();
        }

        public void CreateProgressBar()
        {
            this.GetDavState = new System.Windows.Forms.ProgressBar();
            this.Controls.Add(this.GetDavState);

            this.GetDavState.Location = new System.Drawing.Point(93, 290);
            this.GetDavState.Name = "GetDavState";
            this.GetDavState.Size = new System.Drawing.Size(347, 23);
            this.GetDavState.TabIndex = 8;
            this.ProgressBarExists = true;
        }

        public void IncrementProgressBar()
        {
            this.GetDavState.Increment(1);
        }

        public void RemoveProgressBar()
        {
            if (this.ProgressBarExists == true)
                this.GetDavState.Visible = false;
        }

        private void InhibControls_On()
        {
            this.MoreInfoButton.Enabled = false;
            this.OkButton.Enabled = false;
            this.PathListCombo.Enabled = false;
            this.FileList.Enabled = false;
            this.UpButton.Enabled = false;
        }

        private void InhibControls_Off()
        {
            this.MoreInfoButton.Enabled = true;
            this.OkButton.Enabled = true;
            this.PathListCombo.Enabled = true;
            this.FileList.Enabled = true;
            this.UpButton.Enabled = true;
        }

        public class FillFileListThread
        {
            object open;      

            public FillFileListThread(object open)
            {
                this.open = open;
            }

            public void FillThreadRun()
            {
                ((Open)open).getlisting = false;  
                ((Open)open).FillFileList(((Open)open).CurrentDir);
                ((Open)open).getlisting = true;
                ((Open)open).Invoke(((Open)open).m_DelegateDrawFileList);
            }
        }

        public class IncrementProgressBarThread
        {
            object open;

            public IncrementProgressBarThread(object open)
            {
                this.open = open;
            }

            public void IncrementProgressBarThreadRun()
            {
                Thread.Sleep(500);
                if (((Open)open).getlisting == false)
                {
                    ((Open)open).Invoke(((Open)open).m_DelegateInhibControl_On);
                    ((Open)open).Invoke(((Open)open).m_DelegateCreateProgressBar);
                }
                while (((Open)open).getlisting == false)
                {
                    Thread.Sleep(500);
                    ((Open)open).Invoke(((Open)open).m_DelegateIncrementProgressBar);                    
                }
                ((Open)open).Invoke(((Open)open).m_DelegateRemoveProgressBar);
                ((Open)open).Invoke(((Open)open).m_DelegateInhibControl_Off);
            }
        }

        private void DrawComboPwd(string pwd)
        {
            this.PathListCombo.Items.Add(pwd);
            this.PathListCombo.SelectedIndex = this.ComboIndex;
            this.ComboIndex++;
        }

        public enum SortDirection
        {
            Ascending,
            Descending
        }

        public class FileComparer : IComparer
        {
            private SortDirection m_direction = SortDirection.Ascending;

            public FileComparer() : base() { }

            public FileComparer(SortDirection direction)
            {
                this.m_direction = direction;
            }

            int IComparer.Compare(object a, object b)
            {
                Hashtable x = (Hashtable)a;
                Hashtable y = (Hashtable)b;

                if (m_direction == SortDirection.Ascending)
                {
                    if (((bool)x["is_dir"]) == true && ((bool)y["is_dir"]) == false)
                        return -1;
                    if (((bool)x["is_dir"]) == false && ((bool)y["is_dir"]) == true)
                        return 0;
                }
                else
                {
                    if (((bool)x["is_dir"]) == true && ((bool)y["is_dir"]) == false)
                        return 0;
                    if (((bool)x["is_dir"]) == false && ((bool)y["is_dir"]) == true)
                        return -1;
                }
                return (((string)x["name"]).CompareTo((string)y["name"]));
            }
        }

        private void FillFileList_LaunchThread()
        {
            FillFileListThread h_fill_th = new FillFileListThread(this);
            this.FillThread = new Thread(new ThreadStart(h_fill_th.FillThreadRun));
            FillThread.Start();
        }

        private void FillFileList(string ressource)
        {
            bool success;

            DavRessource Dav = new DavRessource(ressource, this.context);
            success = Dav.GetDirectoryListing(out this.directory_listing);
            this.direction = SortDirection.Ascending;

            this.error_reason = Dav.ResultReason;
            this.getDirectoryListing_success = success;
        }

        private void IncrementProgressBar_LaunchThread()
        {
            IncrementProgressBarThread h_progress_th = new IncrementProgressBarThread(this);
            this.ProgressBarThread = new Thread(new ThreadStart(h_progress_th.IncrementProgressBarThreadRun));
            this.ProgressBarThread.Start();
        }

        private void LaunchErrorWindow()
        {
            string caption = "Connection error";
            MessageBoxButtons buttons = MessageBoxButtons.OK;
            DialogResult result;

            result = MessageBox.Show(this, this.error_reason, caption, buttons);
            if (result == DialogResult.OK)
            {
                this.ProgressBarThread.Abort();
                this.FillThread.Abort();
                this.Close();
            }
        }

        private void DrawFileList()
        {
            if (getDirectoryListing_success == false)
                LaunchErrorWindow();
            this.FileList.Items.Clear();
            this.directory_listing.Sort(new FileComparer(this.direction));
            IEnumerator DicEnum = this.directory_listing.GetEnumerator();
            while (DicEnum.MoveNext())
            {
                int image;
                if (((bool)((Hashtable)DicEnum.Current)["is_dir"]) == true)
                    image = 0;
                else
                    image = 1;

                System.Windows.Forms.ListViewItem Ligne = new System.Windows.Forms.ListViewItem(new string[] {
                ((Hashtable)DicEnum.Current)["name"].ToString(),
                ((Hashtable)DicEnum.Current)["last_author"].ToString(),
                ((Hashtable)DicEnum.Current)["creation_date"].ToString(),
                ((Hashtable)DicEnum.Current)["last_modified"].ToString(),
                "Répertoire source"}, image);

                this.FileList.Items.Add(Ligne);
            }
        }
        
        private void CancelButton_Click(object sender, EventArgs e)
        {
            this.ProgressBarThread.Abort();
            this.FillThread.Abort();
            this.Close();
        }

        private void UpButton_MouseEnter(object sender, EventArgs e)
        {
            this.UpButton.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
        }

        private void UpButton_MouseLeave(object sender, EventArgs e)
        {
            this.UpButton.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
        }

        private void FileList_ColumnClick(object sender, ColumnClickEventArgs e)
        {
            if (this.direction == SortDirection.Ascending)
                this.direction = SortDirection.Descending;
            else
                this.direction = SortDirection.Ascending;            
            DrawFileList();
        }

        private void FileList_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            int item_index = ((ListView)sender).FocusedItem.Index;
            Hashtable item = ((Hashtable)this.directory_listing[item_index]);

            if (((bool)item["is_dir"]) == true)
            {
                this.CurrentDir = item["ressource_name"].ToString();
                DrawComboPwd(CurrentDir);
                FillFileList_LaunchThread();
                IncrementProgressBar_LaunchThread();
            }
            else
            {
                OpenWordDocument(item["ressource_name"].ToString());
            }
        }

        private void OpenWordDocument(string ressource)
        {
            object omissing = Missing.Value;
            DavRessource Dav = new DavRessource(ressource, this.context);
            
            Open_Download Dialog = new Open_Download(this, Dav);
            Dialog.ShowDialog();

            if (GetRemoteFile == false)
            {
                error_reason = Dav.ResultReason;
                LaunchErrorWindow();
            }
            else
            {
                connect.Filename = RemoteFile.Name;
                connect.RealFilename = ressource.Remove(0, ressource.LastIndexOf("/") + 1);
            }
            this.Close();
        }

        private void UpButton_MouseClick(object sender, MouseEventArgs e)
        {
            if (this.CurrentDir.CompareTo(this.basedir) != 0)
            {
                this.CurrentDir = this.CurrentDir.Remove(this.CurrentDir.LastIndexOf("/"));
                this.CurrentDir = this.CurrentDir.Remove(this.CurrentDir.LastIndexOf("/") + 1);

                DrawComboPwd(this.CurrentDir);
                FillFileList_LaunchThread();
                IncrementProgressBar_LaunchThread();
            }
        }

        private void PathListCombo_SelectionChangeCommitted(object sender, EventArgs e)
        {
            string ItemMsg;

            ItemMsg = ((ComboBox)sender).SelectedItem.ToString();
            if (ItemMsg.CompareTo(this.CurrentDir) != 0)
            {
                this.CurrentDir = ItemMsg;
                FillFileList_LaunchThread();
                IncrementProgressBar_LaunchThread();
            }
        }

        private void Open_Load(object sender, EventArgs e)
        {
            DrawComboPwd(basedir);
            this.context = new DavContext("http://192.168.3.31", 8080, "fr", "fr");
            this.CurrentDir = basedir;
            
            FillFileList_LaunchThread();
            IncrementProgressBar_LaunchThread();
        }

        private void Open_FormClosing(object sender, FormClosingEventArgs e)
        {
            this.ProgressBarThread.Abort();
            this.FillThread.Abort();
        }

        private void MoreInfoButton_Click(object sender, EventArgs e)
        {
            OpenMoreInfos MoreInfosWindows = new OpenMoreInfos();
            MoreInfosWindows.ShowDialog();
        }
    }
}