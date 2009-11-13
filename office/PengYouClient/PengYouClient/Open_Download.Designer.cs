namespace PengYouClient
{
    partial class Open_Download
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.DownloadBar = new System.Windows.Forms.ProgressBar();
            this.DownloadCancelButton = new System.Windows.Forms.Button();
            this.DownloadState = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // DownloadBar
            // 
            this.DownloadBar.Location = new System.Drawing.Point(12, 12);
            this.DownloadBar.Name = "DownloadBar";
            this.DownloadBar.Size = new System.Drawing.Size(268, 23);
            this.DownloadBar.TabIndex = 0;
            // 
            // DownloadCancelButton
            // 
            this.DownloadCancelButton.Location = new System.Drawing.Point(205, 66);
            this.DownloadCancelButton.Name = "DownloadCancelButton";
            this.DownloadCancelButton.Size = new System.Drawing.Size(75, 23);
            this.DownloadCancelButton.TabIndex = 1;
            this.DownloadCancelButton.Text = "Annuler";
            this.DownloadCancelButton.UseVisualStyleBackColor = true;
            // 
            // DownloadState
            // 
            this.DownloadState.AutoSize = true;
            this.DownloadState.Location = new System.Drawing.Point(12, 38);
            this.DownloadState.Name = "DownloadState";
            this.DownloadState.Size = new System.Drawing.Size(0, 13);
            this.DownloadState.TabIndex = 2;
            // 
            // Open_Download
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(292, 101);
            this.Controls.Add(this.DownloadState);
            this.Controls.Add(this.DownloadCancelButton);
            this.Controls.Add(this.DownloadBar);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "Open_Download";
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Hide;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "Téléchargement en cours...";
            this.Shown += new System.EventHandler(this.Open_Download_Shown);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ProgressBar DownloadBar;
        private System.Windows.Forms.Button DownloadCancelButton;
        private System.Windows.Forms.Label DownloadState;
    }
}