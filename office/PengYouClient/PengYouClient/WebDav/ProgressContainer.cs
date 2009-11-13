using System;

namespace DAVSharp
{
	/// <summary>
	/// Summary description for ProgressContainer.
	/// </summary>
    public class ProgressContainer
    {
        public ProgressContainer() : this( 0 )
        {
        }

        public ProgressContainer( ulong totalBytesExpected )
        {
            this.totalBytesExpected = totalBytesExpected;
            this.totalBytes = 0;
        }

        public ulong TotalBytesExpected
        {
            get { return totalBytesExpected; }
            set { totalBytesExpected = value; }
        }

        public ulong TotalBytes
        {
            get { return totalBytes; }
            set { totalBytes = value; }
        }

        private ulong totalBytesExpected;
        private ulong totalBytes;        
	}
}
