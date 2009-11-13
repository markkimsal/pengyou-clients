using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace PengYouClient
{
    public class DavRessource
    {
        DAVSharp.DavServerConnection DAVc;

        public DavContext context;
        public string ressource;
        public bool PropFindAll_done = false;

        public ArrayList directory_listing;     
   
        // GetRessource

        public DAVSharp.ProgressContainer sendContainer;
        public DAVSharp.ProgressContainer receiveContainer;

        // 

        // Error codes

        DAVSharp.WebDavStatusCode resultCode;
        string resultReason;
        string resultContent;

        //

        public DAVSharp.WebDavStatusCode ResultCode
        {
            get
            {
                return resultCode;
            }
        }

        public string ResultReason
        {
            get
            {
                return resultReason;
            }
        }

        public string ResultContent
        {
            get
            {
                return resultContent;
            }
        }

        public ulong downloadedBytes
        {
            get
            {
                return receiveContainer.TotalBytes;
            }
        }

        public ulong fileSize
        {
            get
            {
                return receiveContainer.TotalBytesExpected;
            }
        }

        public  DavRessource(string ressource, DavContext context) {
            this.ressource = ressource;
            this.context = context;
            DAVc = new DAVSharp.DavServerConnection(context.server_uri);
            if (context.password != null)
                DAVc.Password = context.password;
            if (context.username != null)
                DAVc.Username = context.username;
        }

        public bool GetDirectoryListing(out ArrayList return_dirlist)
        {
            bool success;

            this.directory_listing = new ArrayList();
            success = DavPropFindAll(1);
            return_dirlist = this.directory_listing;
            return success;
        }

        public bool GetRessource(out FileStream Content)
        {
            sendContainer = new DAVSharp.ProgressContainer();
            receiveContainer = new DAVSharp.ProgressContainer();
            bool success = DAVc.GetResource(this.ressource,
                out resultCode, out resultReason, out resultContent, out Content, sendContainer, receiveContainer);
            Content.Close();
            return success;
        }

        public bool DavPropFindAll(int depth) {
            Hashtable prop = new Hashtable();
            string res = this.ressource;
            bool success;

            success = DAVc.PropfindAll(res, out this.resultCode, out this.resultReason, out prop);
            if (success == false)
                return success;
            
            IDictionaryEnumerator dicenum = prop.GetEnumerator();
            while (dicenum.MoveNext())
            {
                Hashtable element = new Hashtable();

                string relativename;
                string versionname;
                string last_modified;
                string creation_date;
                bool is_dir = false;                

                element.Add("ressource_name", dicenum.Key);
//                versionname = ((Hashtable)((Hashtable)(dicenum.Value))[DAVSharp.WebDavStatusCode.OK])["DAV:version-name"].ToString();
                try
                {
                    if (((Hashtable)((Hashtable)(dicenum.Value))[DAVSharp.WebDavStatusCode.OK])["DAV:resourcetype"] != null)
                    {
                        is_dir = true;
                    }
                }
                catch (Exception e)
                {
                    System.Console.WriteLine(e.Message);
                }
                try
                {
                    versionname = ((Hashtable)((Hashtable)(dicenum.Value))[DAVSharp.WebDavStatusCode.OK])["DAV:version-name"].ToString();
                }
                catch (Exception e)
                {
                    versionname = null;
                }
                last_modified = ((Hashtable)((Hashtable)(dicenum.Value))[DAVSharp.WebDavStatusCode.OK])["DAV:getlastmodified"].ToString();
                creation_date = ((Hashtable)((Hashtable)(dicenum.Value))[DAVSharp.WebDavStatusCode.OK])["DAV:creationdate"].ToString();
                if (is_dir == true)
                {
                    string temp;
                    temp = dicenum.Key.ToString().Remove(dicenum.Key.ToString().LastIndexOf("/"));
                    relativename = temp.Remove(0, temp.LastIndexOf("/") + 1);
                }
                else
                    relativename = dicenum.Key.ToString().Remove(0, dicenum.Key.ToString().LastIndexOf("/") + 1);
                element.Add("name", relativename);
                element.Add("last_author", "Roberto");
                element.Add("last_modified", last_modified);
                element.Add("is_dir", is_dir);
                element.Add("creation_date", creation_date);
                element.Add("version", versionname);
                try
                {                    
                    if (res.CompareTo(dicenum.Key.ToString()) != 0)
                        directory_listing.Add(element);
                }
                catch (Exception e)
                {
                    System.Console.WriteLine(e.Message);
                }
            }
            return success;
        }

        public bool SendRessource(byte[] uploadbyte)
        {
            sendContainer = new DAVSharp.ProgressContainer();
            receiveContainer = new DAVSharp.ProgressContainer();
            bool success = DAVc.PutResource(this.ressource, "application/msword", uploadbyte, 
                out resultCode, out resultReason, out resultContent, sendContainer, receiveContainer);
            return success;
        }
     }
}
