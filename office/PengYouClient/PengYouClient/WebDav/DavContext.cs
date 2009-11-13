using System;
using System.Collections.Generic;
using System.Text;

namespace PengYouClient
{
    public class DavContext
    {
        public Uri server_uri;
        public int port;
        public string username;
        public string password;

        public  DavContext(string serverUri_str, int port) {
            this.server_uri = new Uri(serverUri_str + ":" + port.ToString());
        }

        public DavContext(string serverUri_str, int port, 
        string username, string password)
        {
            this.server_uri = new Uri(serverUri_str + ":" + port.ToString());
            this.username = username;
            this.password = password;
        }
    }
}
