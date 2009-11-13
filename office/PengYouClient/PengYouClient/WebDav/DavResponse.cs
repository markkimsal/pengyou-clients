using System;
using System.Collections;
using System.Text;
using System.Net;
using System.IO;

namespace DAVSharp
{

    /// <summary>
    ///     Encapsulates a response from a WebDAV server, in a way
    ///     somewhat similar to System.Net.HttpWebResponse.
    /// </summary>
    public class DavResponse
    {
        /// <summary>
        ///     Create a new DavResponse with the status line
        ///     returned by the server.
        /// </summary>
        /// <param name="statusLine">
        ///     The status line returned by the server; usually
        ///     the first line before the headers.
        /// </param>
        public DavResponse( string statusLine )
        {
            headers = new Hashtable();

            expectedContentLength = -1;
            totalContentBytes = 0;
            content = null;
            fs = new FileStream(Path.GetTempFileName(), FileMode.Create);

            DAVSharpUtil.ParseStatusLine( statusLine,
                                          out responseProtocol,
                                          out statusCode,
                                          out statusReason );
        }

        /// <summary>
        ///     The protocol used by the server to respond.
        /// </summary>
        public string ResponseProtocol
        {
            get {   return responseProtocol;    }
        }

        /// <summary>
        ///     The status code given by the server in response to the
        ///     request.
        /// </summary>
        public WebDavStatusCode StatusCode
        {
            get {   return statusCode;  }
        }

        /// <summary>
        ///     The reason for the particular status code.
        /// </summary>
        public string StatusReason
        {
            get {   return statusReason;    }
        }

        /// <summary>
        ///     A hash table of the headers returned by the server.
        /// </summary>
        public Hashtable Headers
        {
            get {   return headers; }
        }

        /// <summary>
        ///     The content buffer returned by the server (if any).
        ///     Note that not all bytes in the buffer are guaranteed
        ///     to be used. The ContentLength property contains the
        ///     number of useful bytes in the Content buffer.
        /// </summary>
        public byte[] Content
        {
            get {   return content; }
        }

        /// <summary>
        ///     The length (in bytes) of the content (may be less than
        ///     the length of the content buffer. While the response
        ///     is still being read from the server this value might
        ///     be less than the total content length that will be
        ///     read. See ExpectedContentLength for the value of the
        ///     Content-Length header.
        /// </summary>
        public int ContentLength
        {
            get {   return totalContentBytes;   }
        }

        /// <summary>
        ///     The number of bytes expected to be used by content as
        ///     specified by the Content-Length header, or -1 if no
        ///     Content-Length header was given.
        /// </summary>
        public int ExpectedContentLength
        {
            get {   return expectedContentLength;   }
        }

        /// <summary>
        ///     Add a header to the response.
        /// </summary>
        /// <param name="header">
        ///     An unparsed header line. All text before the first
        ///     instance of a colon (':') will be considered the key,
        ///     and all text after will be considered the value. There
        ///     must be at least one character before the colon.
        /// </param>
        public void AddHeader( string header )
        {
            int splitPosition = header.IndexOf( ':' );

            if( splitPosition < 1 )
            {
                throw new ArgumentException( "Invalid HTTP Header" + header );
            }

            string key = header.Substring( 0, splitPosition );
            string val = header.Substring( splitPosition + 1,
                                           header.Length - splitPosition - 1 );

            AddHeader( key, val );
        }

        /// <summary>
        ///     Add a header to the response. If either the key or val
        ///     parameters are null the call will be ignored, no data
        ///     will be added to the headers, but no exception will be
        ///     thrown.
        /// </summary>
        /// <param name="key">
        ///     The key of the header.
        /// </param>
        /// <param name="val">
        ///     The value of the header.
        /// </param>
        public void AddHeader( string key,
                               string val )
        {
            // Ignore the call when either the key or value is null.
            if( ( key != null ) || ( val != null ) )
            {
                headers.Add( key, val );

                if( key.ToLower().CompareTo( ContentLengthHeader ) == 0 )
                {
                    expectedContentLength = int.Parse( val );
                    EnsureContentBufferSize( expectedContentLength );
                }
            }
        }

        /// <summary>
        ///     Append content to the content buffer.
        /// </summary>
        /// <param name="additionalContent">
        ///     The bytes of the additional content.
        /// </param>
        /// <param name="start">
        ///     What byte to begin copying on.
        /// </param>
        /// <param name="length">
        ///     The number of bytes to copy.
        /// </param>
        public void AppendContent( byte[] additionalContent,
                                   int start,
                                   int length )
        {
            // If the bytes read will overflow the total response
            // buffer, reallocate it and make the new buffer size
            // double the old size, or the old size plus the size of
            // the bytes just read, whichever is greater.
            fs.Write(additionalContent, start, length);
            
            if( ( content == null ) ||
                ( totalContentBytes + length > content.Length ) )
            {
                long newLength = totalContentBytes + length;

                if( ( content != null ) && ( content.Length * 2 > newLength ) )
                {
                    newLength = content.Length * 2;
                }
                
                byte[] newContent = new byte[ newLength ];

                if( content != null )
                {
                    Array.Copy( content,
                                0,
                                newContent,
                                0,
                                totalContentBytes );
                }

                content = newContent;
            }

            // Copy the bytes most recently read into the total response buffer.
            Array.Copy( additionalContent,
                        start,
                        content,
                        totalContentBytes,
                        length );
            totalContentBytes = totalContentBytes + length;
        }

        /// <summary>
        ///     Append additional bytes to the content buffer.
        /// </summary>
        /// <param name="additionalContent">
        ///     The bytes of the additional content.
        /// </param>
        /// <param name="contentSize">
        ///     The number of bytes to read from the additionalContent
        ///     array (starting at byte 0).
        /// </param>
        public void AppendContent( byte[] additionalContent,
                                   int contentSize )
        {
            AppendContent( additionalContent, 0, contentSize );
        }

        /// <summary>
        ///     Append additional bytes to the content buffer.
        /// </summary>
        /// <param name="additionalContent">
        ///     The bytes of teh additional content. All bytes in the
        ///     array will be added.
        /// </param>
        public void AppendContent( byte[] additionalContent )
        {
            AppendContent( additionalContent, 0, additionalContent.Length );
        }

        /// <summary>
        ///     Make sure the content buffer is at least a certain
        ///     size. If the buffer is unallocated it will be
        ///     allocated at the given size. If the buffer is
        ///     allocated and its length is less than the given size,
        ///     it will be reallocated with all bytes in the existing
        ///     buffer copied to the start of the new one.
        /// </summary>
        /// <param name="minimumContentSize">
        ///     The new minimum size of the content buffer.
        /// </param>
        public void EnsureContentBufferSize( int minimumContentSize )
        {
            if( ( minimumContentSize > 0 ) &&
                ( ( content == null ) ||
                    ( minimumContentSize > content.Length ) ) )
            {
                byte[] newContent = new byte[ minimumContentSize ];

                if( content != null )
                {
                    Array.Copy( content, 0, newContent, 0, totalContentBytes );
                }

                content = newContent;
            }
        }

        /// <summary>
        ///     The protocol used by the server to respond.
        /// </summary>
        private string responseProtocol;

        /// <summary>
        ///     The status code given by the server.
        /// </summary>
        private WebDavStatusCode statusCode;
        
        /// <summary>
        ///     The reason for the status code.
        /// </summary>
        private string statusReason;

        /// <summary>
        ///     A hashtable of headers.
        /// </summary>
        private Hashtable headers;

        /// <summary>
        ///     The expected length of the content as given by the
        ///     Content-Length header.
        /// </summary>
        private int expectedContentLength;

        /// <summary>
        ///     The content buffer.
        /// </summary>
        private byte[] content;

        /// <summary>
        ///     The total number of used bytes in the content buffer.
        /// </summary>
        private int totalContentBytes;

        /// <summary>
        ///     The "content-length" header key.
        /// </summary>
        private const string ContentLengthHeader = "content-length";

        public FileStream fs;
        public string filename;
    }

}
