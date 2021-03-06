using System;
using System.Collections;

namespace PengYouClient
{
    partial class Open
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
        //

        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Open));
            this.DFile = new System.Windows.Forms.ColumnHeader("(none)");
            this.DLastAuthor = new System.Windows.Forms.ColumnHeader();
            this.DCreationDate = new System.Windows.Forms.ColumnHeader("(none)");
            this.DLastEdition = new System.Windows.Forms.ColumnHeader();
            this.DDescription = new System.Windows.Forms.ColumnHeader();
            this.imageList1 = new System.Windows.Forms.ImageList(this.components);
            this.PathListCombo = new System.Windows.Forms.ComboBox();
            this.UpButton = new System.Windows.Forms.Button();
            this.OkButton = new System.Windows.Forms.Button();
            this.CancelButton = new System.Windows.Forms.Button();
            this.MoreInfoButton = new System.Windows.Forms.Button();
            this.FileList = new System.Windows.Forms.ListView();
            this.SuspendLayout();
            // 
            // DFile
            // 
            this.DFile.Text = "Fichier";
            this.DFile.Width = 135;
            // 
            // DLastAuthor
            // 
            this.DLastAuthor.Text = "Dernier auteur";
            this.DLastAuthor.Width = 101;
            // 
            // DCreationDate
            // 
            this.DCreationDate.Text = "Création";
            this.DCreationDate.Width = 115;
            // 
            // DLastEdition
            // 
            this.DLastEdition.Text = "Dernière édition";
            this.DLastEdition.Width = 115;
            // 
            // DDescription
            // 
            this.DDescription.Text = "Description";
            this.DDescription.Width = 120;
            // 
            // imageList1
            // 
            this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
            this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
            this.imageList1.Images.SetKeyName(0, "folder.png");
            this.imageList1.Images.SetKeyName(1, "x-office-document.png");
            // 
            // PathListCombo
            // 
            this.PathListCombo.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.PathListCombo.Location = new System.Drawing.Point(40, 12);
            this.PathListCombo.Name = "PathListCombo";
            this.PathListCombo.Size = new System.Drawing.Size(562, 21);
            this.PathListCombo.TabIndex = 7;
            this.PathListCombo.SelectionChangeCommitted += new System.EventHandler(this.PathListCombo_SelectionChangeCommitted);
            // 
            // UpButton
            // 
            this.UpButton.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("UpButton.BackgroundImage")));
            this.UpButton.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.UpButton.FlatAppearance.BorderSize = 0;
            this.UpButton.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.UpButton.ForeColor = System.Drawing.SystemColors.Control;
            this.UpButton.Location = new System.Drawing.Point(12, 11);
            this.UpButton.Name = "UpButton";
            this.UpButton.Size = new System.Drawing.Size(22, 22);
            this.UpButton.TabIndex = 3;
            this.UpButton.UseVisualStyleBackColor = true;
            this.UpButton.MouseLeave += new System.EventHandler(this.UpButton_MouseLeave);
            this.UpButton.MouseClick += new System.Windows.Forms.MouseEventHandler(this.UpButton_MouseClick);
            this.UpButton.MouseEnter += new System.EventHandler(this.UpButton_MouseEnter);
            // 
            // OkButton
            // 
            this.OkButton.Location = new System.Drawing.Point(446, 290);
            this.OkButton.Name = "OkButton";
            this.OkButton.Size = new System.Drawing.Size(75, 23);
            this.OkButton.TabIndex = 4;
            this.OkButton.Text = "Ok";
            this.OkButton.UseVisualStyleBackColor = true;
            // 
            // CancelButton
            // 
            this.CancelButton.Location = new System.Drawing.Point(527, 290);
            this.CancelButton.Name = "CancelButton";
            this.CancelButton.Size = new System.Drawing.Size(75, 23);
            this.CancelButton.TabIndex = 5;
            this.CancelButton.Text = "Annuler";
            this.CancelButton.UseVisualStyleBackColor = true;
            this.CancelButton.Click += new System.EventHandler(this.CancelButton_Click);
            // 
            // MoreInfoButton
            // 
            this.MoreInfoButton.Location = new System.Drawing.Point(12, 290);
            this.MoreInfoButton.Name = "MoreInfoButton";
            this.MoreInfoButton.Size = new System.Drawing.Size(75, 23);
            this.MoreInfoButton.TabIndex = 6;
            this.MoreInfoButton.Text = "Plus d\'infos";
            this.MoreInfoButton.UseVisualStyleBackColor = true;
            this.MoreInfoButton.Click += new System.EventHandler(this.MoreInfoButton_Click);
            // 
            // FileList
            // 
            this.FileList.Alignment = System.Windows.Forms.ListViewAlignment.Default;
            this.FileList.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.DFile,
            this.DLastAuthor,
            this.DCreationDate,
            this.DLastEdition,
            this.DDescription});
            this.FileList.FullRowSelect = true;
            this.FileList.Location = new System.Drawing.Point(12, 40);
            this.FileList.Name = "FileList";
            this.FileList.Size = new System.Drawing.Size(590, 244);
            this.FileList.SmallImageList = this.imageList1;
            this.FileList.TabIndex = 0;
            this.FileList.UseCompatibleStateImageBehavior = false;
            this.FileList.View = System.Windows.Forms.View.Details;
            this.FileList.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.FileList_MouseDoubleClick);
            this.FileList.ColumnClick += new System.Windows.Forms.ColumnClickEventHandler(this.FileList_ColumnClick);
            // 
            // Open
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackgroundImageLayout = System.Windows.Forms.ImageLayout.None;
            this.ClientSize = new System.Drawing.Size(614, 325);
            this.Controls.Add(this.MoreInfoButton);
            this.Controls.Add(this.CancelButton);
            this.Controls.Add(this.OkButton);
            this.Controls.Add(this.UpButton);
            this.Controls.Add(this.PathListCombo);
            this.Controls.Add(this.FileList);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "Open";
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Hide;
            this.Text = "Ouvrir un document";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Open_FormClosing);
            this.Load += new System.EventHandler(this.Open_Load);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.ListView FileList;
        private System.Windows.Forms.ColumnHeader DFile;
        private System.Windows.Forms.ColumnHeader DLastAuthor;
        private System.Windows.Forms.ColumnHeader DCreationDate;
        private System.Windows.Forms.ColumnHeader DLastEdition;
        private System.Windows.Forms.ComboBox PathListCombo;
        private System.Windows.Forms.Button UpButton;
        private System.Windows.Forms.ImageList imageList1;
        private System.Windows.Forms.Button OkButton;
        private System.Windows.Forms.Button CancelButton;
        private System.Windows.Forms.Button MoreInfoButton;
        private System.Windows.Forms.ColumnHeader DDescription;
        private System.Windows.Forms.ProgressBar GetDavState;








    }
}