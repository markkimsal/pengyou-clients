using System;
using System.Collections;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Text;
using System.Xml;

namespace DAVSharp
{

    /// <summary>
    ///     Provides functionality to connect to a WebDAV server and
    ///     perform tasks on resources.
    /// </summary>
    class DavServerConnection
    {
        /// <summary>
        ///     Create a connection to a WebDAV server. The connection should be made without
        ///     any path information; paths are passed to individual commands.
        /// </summary>
        /// <param name="connectionUri">
        ///     The Uri of the server without any path information.
        /// </param>
        public DavServerConnection( Uri connectionUri )
        {
			Console.WriteLine( "connectionUri path = " + connectionUri.AbsolutePath );
			
			if( !connectionUri.AbsolutePath.Equals( "/" ) )
			{
				throw new ArgumentException(
					"Connection URI must not contain any path information.",
					"connectionUri" );
			}
            
			serverUri = connectionUri;
        }

        /// <summary>
        ///     The URI of the server this connection goes to.
        /// </summary>
        public Uri ServerUri
        {
            get {   return serverUri;   }
        }
        
		/// <summary>
		///		The Username to use for password protected sites.
		/// </summary>
		public string Username
		{
			get {	return username;	}
			set {	username = value;	}
		}

		/// <summary>
		///		The Password to use for password protected sites.
		/// </summary>
		public string Password
		{
			get {	return password;	}
			set {	password = value;	}
		}

		/// <summary>
        ///     A general purpose test method.
        /// </summary>
        public bool Test()
        {
            try
            {
                WebDavStatusCode resultCode;
                string resultReason;
                string contentType;
                byte[] resultContent;

                bool success = GetResource( "/", out resultCode, out resultReason, out contentType, out resultContent );
                if( success )
                {
                    Console.WriteLine("Get (not monitored) result = {0}, reason = {1}, type = {2}, length = {3}",
                        resultCode, resultReason, contentType, resultContent.Length );
                    Console.WriteLine("Content = {0}",
						Encoding.UTF8.GetString( resultContent, 0, resultContent.Length ) );
                }
                
                ProgressContainer sendContainer = new ProgressContainer();
                ProgressContainer receiveContainer = new ProgressContainer();
                success = GetResource( "/putty.exe",
                    out resultCode, out resultReason, out contentType, out resultContent, sendContainer, receiveContainer );
                if( success )
                {
                    Console.WriteLine("Get (monitored) result = {0}, reason = {1}, type = {2}, length = {3}",
                        resultCode, resultReason, contentType, resultContent.Length );
                }

                return( true );
            }
            catch( SocketException e )
            {
                Console.WriteLine( "Connection error: {0}", e.Message );
                return( false );
            }
        }


        /// <summary>
        /// Retrieve the names available properties on a resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="propertyNames">
        ///     An array of property names. Just because a property is listed here does
        ///     not mean the caller has permission to retrieve the value.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="resultContent">
        ///     A hashtable with the properties of the resource or
        ///     null if there was an error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool Propnames( string path, out WebDavStatusCode resultCode,
            out string resultReason, out string[] propertyNames,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = true;
            
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to get the properties.
            try
            {
                byte[] content = DavPropertyUtil.GetPropfindNamesRequestContent();

                string[] propfindHeaders = new string[ 1 ];
                propfindHeaders[ 0 ] = "Depth: 0";

                DavResponse response = SendRequest( path, PropfindRequest,
                    propfindHeaders, XMLTextContent, content, sendProgress, receiveProgress );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                
                string contentType = (string) response.Headers[ ContentTypeHeader ];
                string resultContent =
					Encoding.UTF8.GetString( response.Content, 0, response.ContentLength );
                
                propertyNames = null;

                if( resultCode == WebDavStatusCode.MultiStatus )
                {
                    Hashtable statusTable = (Hashtable)
                        DavPropertyUtil.ParseMultiStatus( response.Content )[ path ];

                    if( statusTable.ContainsKey( WebDavStatusCode.OK ) )
                    {
                        Hashtable availableProps =
                            (Hashtable) statusTable[ WebDavStatusCode.OK ];
                        
                        // Subtract 2 keys (the result code and result string are not props)
                        propertyNames = new string[ availableProps.Count - 2 ];
                        int i = 0;

                        foreach( string prop in availableProps.Keys )
                        {
                            if( !prop.Equals( DavPropertyUtil.DAVSharpReasonCodeKey ) &&
                                !prop.Equals( DavPropertyUtil.DAVSharpReasonStringKey ) )
                            {
                                propertyNames[ i ] = prop;
                                i++;
                            }
                        }
                    }
                }
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                propertyNames = null;

                success = false;
            }
            catch( XmlException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                propertyNames = null;

                success = false;
            }

            return( success );
        }

        /// <summary>
        /// Retrieve the names available properties on a resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="propertyNames">
        ///     An array of property names. Just because a property is listed here does
        ///     not mean the caller has permission to retrieve the value.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="resultContent">
        ///     A hashtable with the properties of the resource or
        ///     null if there was an error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool Propnames( string path, out WebDavStatusCode resultCode,
            out string resultReason, out string[] propertyNames )
        {
            bool success = Propnames(
                path, out resultCode, out resultReason, out propertyNames, null, null );

            return( success );
        }

        /// <summary>
        ///     Get all properties associated with a resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="resultContent">
        ///     A hashtable with the properties of the resource or
        ///     null if there was an error.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool PropfindAll( string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out Hashtable properties,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = true;
            Hashtable prop = new Hashtable();
            
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to get the properties.
            try
            {
                byte[] content = DavPropertyUtil.GetPropfindAllRequestContent();

                string[] propfindHeaders = new string[ 1 ];
                propfindHeaders[ 0 ] = "Depth: 1";

                DavResponse response = SendRequest( path, PropfindRequest,
                    propfindHeaders, XMLTextContent, content, sendProgress, receiveProgress );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                
                string contentType = (string) response.Headers[ ContentTypeHeader ];
                string resultContent =
					Encoding.UTF8.GetString( response.Content, 0, response.ContentLength );
                
                if( resultCode == WebDavStatusCode.MultiStatus )
                {                    
                    DavPropertyUtil.ParseMultiStatus( response.Content, prop );
                    properties = prop;
                }
                else
                {
                    properties = null;
                }
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                properties = null;

                success = false;
            }
            catch (XmlException e)
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                properties = null;
                success = false;
            }
            return( success );
        }

        /// <summary>
        ///     Get all properties associated with a resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="resultContent">
        ///     A hashtable with the properties of the resource or
        ///     null if there was an error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool PropfindAll( string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out Hashtable properties )
        {
            bool success = PropfindAll( path, out resultCode, out resultReason, out properties, null, null );

            return( success );
        }

        /// <summary>
        ///     Get the specified properties associated with a
        ///     resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="propertyKeys">
        ///     The keys of the properties to be retrieved.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="properties">
        ///     A hashtable with the properties of the resource or
        ///     null if there was an error.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool Propfind( string path,
            string[] propertyKeys,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out Hashtable properties,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = true;
            
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to get the properties.
            try
            {
                byte[] content = DavPropertyUtil.GetPropfindRequestContent( propertyKeys );

                string[] propfindHeaders = new string[ 1 ];
                propfindHeaders[ 0 ] = "Depth: 1";

                DavResponse response = SendRequest( path, PropfindRequest,
                    propfindHeaders, XMLTextContent, content, sendProgress, receiveProgress );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                
                string contentType = (string) response.Headers[ ContentTypeHeader ];
                string resultContent =
					Encoding.UTF8.GetString( response.Content, 0, response.ContentLength );
                
                if( resultCode == WebDavStatusCode.MultiStatus )
                {
                    properties = (Hashtable)
                        DavPropertyUtil.ParseMultiStatus( response.Content )[ path ];
                }
                else
                {
                    properties = null;
                }
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                properties = null;

                success = false;
            }
            catch( XmlException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                properties = null;

                success = false;
            }

            return( success );
        }

        /// <summary>
        ///     Get the specified properties associated with a
        ///     resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="propertyKeys">
        ///     The keys of the properties to be retrieved.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="properties">
        ///     A hashtable with the properties of the resource or
        ///     null if there was an error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool Propfind( string path,
            string[] propertyKeys,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out Hashtable properties )
        {
            bool success = Propfind( path, propertyKeys, out resultCode, out resultReason, out properties, null, null );
            
            return( success );
        }

        /// <summary>
        ///     Get the specified properties associated with a
        ///     resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="propertyKey">
        ///     The key of the property to be retrieved.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="property">
        ///     A hashtable with the properties of the resource or
        ///     null if there was an error.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool Propfind( string path,
            string propertyKey,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out object property,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            string[] propertyList = new string[] { propertyKey };
            Hashtable properties;

            bool success = Propfind(path,
                propertyList,
                out resultCode,
                out resultReason,
                out properties,
                sendProgress,
                receiveProgress );
            
            property = null;

            if( success && ( properties != null ) )
            {
                foreach( WebDavStatusCode statusCode in properties.Keys )
                {
                    Hashtable propInfo = (Hashtable) properties[ statusCode ];
                    
                    if( propInfo.ContainsKey( propertyKey ) )
                    {
                        resultCode = (WebDavStatusCode)
                            propInfo[ DavPropertyUtil.DAVSharpReasonCodeKey ];
                        resultReason  = (string)
                            propInfo[ DavPropertyUtil.DAVSharpReasonStringKey ];
                        property = propInfo[ propertyKey ];
                    }
                }
            }

            return( success );
        }

        /// <summary>
        ///     Get the specified properties associated with a
        ///     resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="propertyKey">
        ///     The key of the property to be retrieved.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="property">
        ///     A hashtable with the properties of the resource or
        ///     null if there was an error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool Propfind( string path,
            string propertyKey,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out object property )
        {
            bool success = Propfind(path,
                propertyKey,
                out resultCode,
                out resultReason,
                out property,
                null,
                null);

            return( success );
        }

        /// <summary>
        ///     Returns the type info associated with a resource. By
        ///     default, resources have an empty resourcetype.
        ///     Collections have an empty sub-element DAV:collection
        ///     to flag the resource as a collection.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="typeInfo">
        ///     The resource type information as stored on the server.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool GetResourceType( string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out object typeInfo,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = Propfind( path, DavPropertyUtil.DavResourceTypeProp,
                out resultCode, out resultReason, out typeInfo, sendProgress, receiveProgress );

            return( success );
        }

        /// <summary>
        ///     Returns the type info associated with a resource. By
        ///     default, resources have an empty resourcetype.
        ///     Collections have an empty sub-element DAV:collection
        ///     to flag the resource as a collection.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="typeInfo">
        ///     The resource type information as stored on the server.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool GetResourceType( string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out object typeInfo )
        {
            bool success = Propfind( path, DavPropertyUtil.DavResourceTypeProp,
                out resultCode, out resultReason, out typeInfo );

            return( success );
        }

        /// <summary>
        ///     Determine if the resource at the given path is a
        ///     collection or not.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool IsCollection( string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out bool isCollection,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = false;

            object typeInfo;
            isCollection = false;
            
            if( GetResourceType( path,
                out resultCode,
                out resultReason,
                out typeInfo,
                sendProgress,
                receiveProgress ) &&
                ( typeInfo != null ) )
            {
                success = true;
                Hashtable typeInfoTable = (Hashtable) typeInfo;
                isCollection =
                    typeInfoTable.ContainsKey( DavPropertyUtil.DavCollectionProp );
            }

            return( success );
        }

        /// <summary>
        ///     Determine if the resource at the given path is a
        ///     collection or not.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool IsCollection( string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out bool isCollection )
        {
            return( IsCollection( path, out resultCode, out resultReason, out isCollection, null, null ) );
        }

        /// <summary>
        ///     Returns the list of resources in the collection.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///    exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="resources">
        ///     The resources contained in the collection, or null if
        ///     there were no resources, or the path did not resolve
        ///     to a collection itself.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool CollectionContents( string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string[] resources,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = true;
            
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to get the properties.
            try
            {
                string[] propertyKeys = new string[] { DavPropertyUtil.DavResourceTypeProp };

                byte[] content = DavPropertyUtil.GetPropfindRequestContent( propertyKeys );

                string[] propfindHeaders = new string[ 1 ];
                propfindHeaders[ 0 ] = "Depth: 1";

                DavResponse response = SendRequest( path, PropfindRequest,
                    propfindHeaders, XMLTextContent, content );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                
                string contentType = (string) response.Headers[ ContentTypeHeader ];
                string resultContent =
					Encoding.UTF8.GetString( response.Content, 0, response.ContentLength );
                
                resources = null;

                if( resultCode == WebDavStatusCode.MultiStatus )
                {
                    Hashtable resourceInfo =
                        DavPropertyUtil.ParseMultiStatus( response.Content );
                    
                    if( resourceInfo.Count > 1 )
                    {
                        // Do not return the resource path for the parent.
                        resources = new string[ resourceInfo.Count - 1 ];
                        int i = 0;

                        foreach( string resKey in resourceInfo.Keys )
                        {
                            if( !resKey.Equals( path ) )
                            {
                                resources[ i ] = resKey;
                                i++;
                            }
                        }
                    }
                }
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                resources = null;

                success = false;
            }
            catch( XmlException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                resources = null;

                success = false;
            }

            return( success );
        }

        /// <summary>
        ///     Returns the list of resources in the collection.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///    exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="resources">
        ///     The resources contained in the collection, or null if
        ///     there were no resources, or the path did not resolve
        ///     to a collection itself.
        /// </param>
        /// <param name="sizes">
        ///     The sizes of the resources in the collection.
        /// </param>
        /// <param name="dates">
        ///     The last modified dates of the resources in the collection.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool CollectionContents( string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string[] resources,
            out long[] sizes,
            out string[] dates,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = true;
            sizes = null;
            dates = null;
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to get the properties.
            try
            {
                string[] propertyKeys = new string[] { DavPropertyUtil.DavResourceTypeProp,
                                                     DavPropertyUtil.DavContentLengthProp,
                                                     DavPropertyUtil.DavLastModifiedProp};

                byte[] content = DavPropertyUtil.GetPropfindRequestContent( propertyKeys );

                string[] propfindHeaders = new string[ 1 ];
                propfindHeaders[ 0 ] = "Depth: 1";

                DavResponse response = SendRequest( path, PropfindRequest,
                    propfindHeaders, XMLTextContent, content );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                
                string contentType = (string) response.Headers[ ContentTypeHeader ];
                string resultContent =
					Encoding.UTF8.GetString( response.Content, 0, response.ContentLength );
                
                resources = null;

                if( resultCode == WebDavStatusCode.MultiStatus )
                {                    
                    Hashtable resourceInfo =
                        DavPropertyUtil.ParseMultiStatus( response.Content );
   
                    if( resourceInfo.Count > 1 )
                    {
                        // Do not return the resource path for the parent.
                        resources = new string[ resourceInfo.Count - 1 ];
                        sizes = new long[ resourceInfo.Count - 1 ];
                        dates = new string[ resourceInfo.Count - 1 ];

                        int i = 0;
                        foreach( string resKey in resourceInfo.Keys)
                        {
                            if( !resKey.Equals( path ) )
                            {                                
                                resources[ i ] = resKey;                                
                                Hashtable tab = ((Hashtable)((Hashtable)
                                    resourceInfo[resKey])[WebDavStatusCode.OK]);
                                if(tab.ContainsKey( DavPropertyUtil.DavContentLengthProp ))
                                    sizes[i] = long.Parse(tab[DavPropertyUtil.DavContentLengthProp].ToString());
                                else
                                    sizes[i] = -1;
                                if(tab.ContainsKey( DavPropertyUtil.DavLastModifiedProp ))
                                    dates[i] = tab[DavPropertyUtil.DavLastModifiedProp].ToString();
                                else
                                    dates[i] = "unknown";
                                i++;
                            }
                        }
                    }
                }
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                resources = null;

                success = false;
            }
            catch( XmlException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                resources = null;

                success = false;
            }

            return( success );
        }


        /// <summary>
        ///     Returns the list of resources in the collection.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///    exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="resources">
        ///     The resources contained in the collection, or null if
        ///     there were no resources, or the path did not resolve
        ///     to a collection itself.
        /// </param>
        /// <param name="sizes">
        ///     The sizes of the resources in the collection.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool CollectionContents( string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string[] resources,
            out long[] sizes,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = true;
            sizes = null;
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to get the properties.
            try
            {
                string[] propertyKeys = new string[] { DavPropertyUtil.DavResourceTypeProp,
                                                     DavPropertyUtil.DavContentLengthProp};

                byte[] content = DavPropertyUtil.GetPropfindRequestContent( propertyKeys );

                string[] propfindHeaders = new string[ 1 ];
                propfindHeaders[ 0 ] = "Depth: 1";

                DavResponse response = SendRequest( path, PropfindRequest,
                    propfindHeaders, XMLTextContent, content );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                
                string contentType = (string) response.Headers[ ContentTypeHeader ];
                string resultContent =
					Encoding.UTF8.GetString( response.Content, 0, response.ContentLength );
                
                resources = null;

                if( resultCode == WebDavStatusCode.MultiStatus )
                {                    
                    Hashtable resourceInfo =
                        DavPropertyUtil.ParseMultiStatus( response.Content );
   
                    if( resourceInfo.Count > 1 )
                    {
                        // Do not return the resource path for the parent.
                        resources = new string[ resourceInfo.Count - 1 ];
                        sizes = new long[ resourceInfo.Count - 1 ];

                        int i = 0;
                        foreach( string resKey in resourceInfo.Keys)
                        {
                            if( !resKey.Equals( path ) )
                            {                                
                                resources[ i ] = resKey;                                
                                Hashtable tab = ((Hashtable)((Hashtable)
                                    resourceInfo[resKey])[WebDavStatusCode.OK]);
                                if(tab.ContainsKey( DavPropertyUtil.DavContentLengthProp ))
                                    sizes[i] = long.Parse(tab[DavPropertyUtil.DavContentLengthProp].ToString());
                                else
                                    sizes[i] = -1;

                                i++;
                            }
                        }
                    }
                }
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                resources = null;

                success = false;
            }
            catch( XmlException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                resources = null;

                success = false;
            }

            return( success );
        }

        /// <summary>
        ///     Returns the list of resources in the collection.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///    exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="resources">
        ///     The resources contained in the collection, or null if
        ///     there were no resources, or the path did not resolve
        ///     to a collection itself.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool CollectionContents( string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string[] resources )
        {
            return( CollectionContents( path, out resultCode, out resultReason, out resources, null, null ) );
        }

        /// <summary>
        ///     Get a resource from the server.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned from the
        ///     server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if there was an
        ///     error.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the result content is
        ///     valid.
        /// </returns>
        public bool GetResource( string path,
                                 out WebDavStatusCode resultCode,
                                 out string resultReason,
                                 out string contentType,
                                 out byte[] resultContent,
                                 ProgressContainer sendProgress,
                                 ProgressContainer receiveProgress )
        {
            bool success = true;
            
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to get the resource.
            try
            {
                DavResponse response = SendRequest( path, GetRequest, null, null, null, sendProgress, receiveProgress );
                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                contentType = (string) response.Headers[ ContentTypeHeader ];
                resultContent = response.Content;
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                contentType = null;
                resultContent = null;

                success = false;
            }

            return( success );
        }

        /// <summary>
        ///     Get a resource from the server.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned from the
        ///     server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if there was an
        ///     error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the result content is
        ///     valid.
        /// </returns>
        public bool GetResource( string path,
                                 out WebDavStatusCode resultCode,
                                 out string resultReason,
                                 out string contentType,
                                 out byte[] resultContent )
        {
            return( GetResource( path, out resultCode, out resultReason, out contentType, out resultContent, null, null ) );
        }
        
        /// <summary>
        ///     Put a resource to the server.
        /// </summary>
        /// <param name="resourcePath">
        ///     The path the resource will be placed at. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="contentType">
        ///     The type of the content being placed on the
        ///     server.
        /// </param>
        /// <param name="uploadBytes">
        ///     The data being uploaded.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="resultContentType">
        ///     The content of the resource or null if there was an
        ///     error.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication error
        /// prevented the request from succeeding. If true, the caller should
        /// examine the result code and reason to determine whether or not the
        /// result content is valid.</returns>
        public bool PutResource( string resourcePath, string contentType, byte[] uploadBytes,
            out WebDavStatusCode resultCode, out string resultReason, out string resultContentType,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress)
        {
            bool success = true;
            
            // Make sure the input parameters are valid.
            if( ( resourcePath == null ) || ( resourcePath.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }
            
            // Try to get the resource.
            try
            {
                DavResponse response = SendRequest( resourcePath, PutRequest,
                    null, contentType, uploadBytes, sendProgress, receiveProgress);

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                resultContentType = (string) response.Headers[ ContentTypeHeader ];                
                    
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                resultContentType = null;
                return false;
            }
            return success;
        }

        /// <summary>
        ///     Put a resource to the server.
        /// </summary>
        /// <param name="resourcePath">
        ///     The path the resource will be placed at. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="contentType">
        ///     The type of the content being placed on the
        ///     server.
        /// </param>
        /// <param name="uploadBytes">
        ///     The data being uploaded.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="resultContentType">
        ///     The content of the resource or null if there was an
        ///     error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication error
        /// prevented the request from succeeding. If true, the caller should
        /// examine the result code and reason to determine whether or not the
        /// result content is valid.</returns>
        public bool PutResource( string resourcePath, string contentType, byte[] uploadBytes,
            out WebDavStatusCode resultCode, out string resultReason, out string resultContentType)
        {
            bool success = PutResource( resourcePath, contentType, uploadBytes,
                out resultCode, out resultReason, out resultContentType, null, null );

            return success;
        }


        /// <summary>
        /// Make a new collection at the specified path.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool Mkcol( string resourcePath, 
            out WebDavStatusCode resultCode, out string resultReason,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = true;
            
            // Try to get the resource.
            try
            {
                DavResponse response = SendRequest(resourcePath, MkcolRequest,
                    null, null, null, sendProgress, receiveProgress );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;

                success = false;
            }
            return success;
        }

        /// <summary>
        /// Make a new collection at the specified path.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool Mkcol( string resourcePath, 
            out WebDavStatusCode resultCode, out string resultReason )
        {
            bool success = Mkcol( resourcePath, out resultCode, out resultReason, null, null );

            return success;
        }


        /// <summary>
        /// Delete the resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool DeleteResource( string path, out WebDavStatusCode resultCode,
            out string resultReason,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = true;
            
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to get the resource.
            try
            {
                DavResponse response = SendRequest( path, DeleteRequest,
                    null, null, null, sendProgress, receiveProgress);

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;

                success = false;
            }

            return( success );
        }

        /// <summary>
        /// Delete the resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the properties are valid.
        /// </returns>
        public bool DeleteResource( string path, out WebDavStatusCode resultCode,
            out string resultReason )
        {
            bool success = DeleteResource( path, out resultCode, out resultReason, null, null );
            
            return( success );
        }

        /// <summary>
        ///     Locks a resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest
        ///     if an exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the
        ///     exception message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned
        ///     from the server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if
        ///     there was an error.
        /// </param>
        /// <param name="lockToken">
        ///     The lock token if the resource is locked or null
        ///     if the resource could not be locked.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication error
        ///     prevented the request from succeeding. If true, the caller should
        ///     examine the result code and reason to determine whether or not the
        ///     result content is valid.
        /// </returns>
        public bool LockResource(string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string contentType,
            out byte[] resultContent,
            out string lockToken,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            // Local variables
            bool success = true;
            string CR = "\n";
            string xmlFluff = XMLContentPreamble + CR
                + "<D:lockinfo xmlns:D=\"DAV:\">" + CR
                + " <D:lockscope><D:exclusive/></D:lockscope>" + CR
                + " <D:locktype><D:write/></D:locktype>" + CR
                + " <D:owner>" + CR
                + "  <D:href>http://whisper.cse.ucsc.edu:16080/csharp</D:href>" + CR
                + " </D:owner>" + CR
                + "</D:lockinfo>" + CR;
            string[] headers = new string[] {
                                                "Depth: infinity"
                                            };
            lockToken = null;
            
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to lock the resource.
            try
            {
                DavResponse response = SendRequest(path, LockRequest, null, XMLTextContent,
                    System.Text.Encoding.UTF8.GetBytes(xmlFluff), sendProgress, receiveProgress );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                contentType = (string) response.Headers[ ContentTypeHeader ];
                resultContent = response.Content;

                // Extract lock token from XML body
                if (response.StatusCode == WebDavStatusCode.OK) 
                {
                    System.Xml.XmlDocument doc = new System.Xml.XmlDocument();
                    doc.Load(new System.IO.StringReader(
						System.Text.Encoding.UTF8.GetString(
							response.Content, 0, response.ContentLength ) ) );
                    System.Xml.XmlNodeReader xmlReader = new System.Xml.XmlNodeReader(doc);
                    do { xmlReader.Read(); } while (xmlReader.Name != "D:locktoken");
                    do { xmlReader.Read(); } while (xmlReader.Name != "D:href");
                    xmlReader.Read();
                    lockToken = xmlReader.Value.Trim();
                }
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                contentType = null;
                resultContent = null;
                lockToken = null;
                success = false;
            }

            return( success );
        }

        /// <summary>
        ///     Locks a resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest
        ///     if an exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the
        ///     exception message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned
        ///     from the server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if
        ///     there was an error.
        /// </param>
        /// <param name="lockToken">
        ///     The lock token if the resource is locked or null
        ///     if the resource could not be locked.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication error
        ///     prevented the request from succeeding. If true, the caller should
        ///     examine the result code and reason to determine whether or not the
        ///     result content is valid.
        /// </returns>
        public bool LockResource(string path,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string contentType,
            out byte[] resultContent,
            out string lockToken)
        {
            // Local variables
            bool success = LockResource( path, out resultCode, out resultReason,
                out contentType, out resultContent, out lockToken, null, null );

            return( success );
        }

        /// <summary>
        ///     Refreshes the lock on a resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="token">
        ///     The resource's lock token as returned by a LOCK request.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest
        ///     if an exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the
        ///     exception message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned
        ///     from the server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if
        ///     there was an error.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication error
        ///     prevented the request from succeeding. If true, the caller should
        ///     examine the result code and reason to determine whether or not the
        ///     result content is valid.
        /// </returns>
        public bool RefreshResource(string path,
            string token,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string contentType,
            out byte[] resultContent,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            // Local variables
            bool success = true;
            string[] headers = new string[] {
                                                "Depth: infinity",
                                                "If: (<" + token + ">)"
                                            };
            
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to refresh the resource.
            try
            {
                DavResponse response = SendRequest(path, LockRequest, headers,
                    XMLTextContent, null, sendProgress, receiveProgress );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                contentType = (string) response.Headers[ ContentTypeHeader ];
                resultContent = response.Content;
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                contentType = null;
                resultContent = null;
                success = false;
            }

            return( success );
        }

        /// <summary>
        ///     Refreshes the lock on a resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="token">
        ///     The resource's lock token as returned by a LOCK request.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest
        ///     if an exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the
        ///     exception message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned
        ///     from the server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if
        ///     there was an error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication error
        ///     prevented the request from succeeding. If true, the caller should
        ///     examine the result code and reason to determine whether or not the
        ///     result content is valid.
        /// </returns>
        public bool RefreshResource(string path,
            string token,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string contentType,
            out byte[] resultContent)
        {
            // Local variables
            bool success = RefreshResource( path, token,
                out resultCode, out resultReason, out contentType, out resultContent, null, null );

            return( success );
        }

        /// <summary>
        ///     Unlocks a resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="token">
        ///     The resource's lock token as returned by a LOCK request.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest
        ///     if an exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the
        ///     exception message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned
        ///     from the server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if
        ///     there was an error.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication error
        ///     prevented the request from succeeding. If true, the caller should
        ///     examine the result code and reason to determine whether or not the
        ///     result content is valid.
        /// </returns>
        public bool UnlockResource(string path,
            string token,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string contentType,
            out byte[] resultContent,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            // Local variables
            bool success = true;
            string[] headers = new string[] {
                                                "Depth: infinity",
                                                "Lock-Token: <" + token + ">"
                                            };
            
            // Make sure the input parameters are valid.
            if( ( path == null ) || ( path.Length == 0 ) )
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to unlock the resource.
            try
            {
                DavResponse response = SendRequest(path, UnlockRequest, headers,
                    XMLTextContent, null, sendProgress, receiveProgress );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                contentType = (string) response.Headers[ ContentTypeHeader ];
                resultContent = response.Content;
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                contentType = null;
                resultContent = null;
                success = false;
            }

            return( success );
        }

        /// <summary>
        ///     Unlocks a resource.
        /// </summary>
        /// <param name="path">
        ///     The path of the resource. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="token">
        ///     The resource's lock token as returned by a LOCK request.
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest
        ///     if an exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the
        ///     exception message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned
        ///     from the server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if
        ///     there was an error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication error
        ///     prevented the request from succeeding. If true, the caller should
        ///     examine the result code and reason to determine whether or not the
        ///     result content is valid.
        /// </returns>
        public bool UnlockResource(string path,
            string token,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string contentType,
            out byte[] resultContent)
        {
            // Local variables
            bool success = UnlockResource( path, token,
                out resultCode, out resultReason, out contentType, out resultContent, null, null );

            return( success );
        }

        /// <summary>
        ///     A template for a MOVE or COPY command
        /// </summary>
        /// <param name="command">
        ///     HTTP command to send
        /// </param>
        /// <param name="source">
        ///     The source path. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="destination">
        ///     The destination path. It must be non-null and
        ///     contain at least one character.
        /// </param>
        /// <param name="allowOverwrite">
        ///     Flag to allow overwrite
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the
        ///     exception message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned from the
        ///     server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if there was an
        ///     error.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the result content is
        ///     valid.
        /// </returns>
        protected bool ChangeResourceCmd(
            string command,
            string source,
            string destination,
            bool allowOverwrite, 
            out WebDavStatusCode resultCode, 
            out string resultReason, 
            out string contentType,
            out byte[] resultContent,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            bool success = true;
            
            // Make sure the input parameters are valid.
            if( ( source == null ) || ( source.Length == 0 ) 
                || ( destination == null ) || ( destination.Length == 0 ))
            {
                throw new ArgumentException( "Invalid resource path.", "path" );
            }

            // Try to copy the resource.
            try
            {
                string[] headers = new string[3];
                destination = destination.TrimStart('/');

                headers[0] = "Host: " + this.serverUri.Host;
                headers[1] = "Destination: " + this.serverUri + destination;
                if(allowOverwrite)
                    headers[2] = "Overwrite: T";                 
                else
                    headers[2] = "Overwrite: F";

                DavResponse response = SendRequest( source, command, 
                    headers, null, null, sendProgress, receiveProgress );

                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                contentType = (string) response.Headers[ ContentTypeHeader ];
                resultContent = response.Content;
            }
            catch( SocketException e )
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                contentType = null;
                resultContent = null;

                success = false;
            }

            return( success );
        }
        
        /// <summary>
        ///     Copies a file on the server to another place on the
        ///     server
        /// </summary>
        /// <param name="source">
        ///     The source path. It must be non-null and contain at
        ///     least one character.
        /// </param>
        /// <param name="destination">
        ///     The destination path. It must be non-null and contain
        ///     at least one character.
        /// </param>
        /// <param name="allowOverwrite">
        ///     Flag to allow overwrite
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned from the
        ///     server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if there was an
        ///     error.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the result content is
        ///     valid.
        /// </returns>
        public bool CopyResource( string source,
            string destination,
            bool allowOverwrite,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string contentType,
            out byte[] resultContent,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            return ChangeResourceCmd(CopyRequest,
                source,
                destination,
                allowOverwrite,
                out resultCode,
                out resultReason,
                out contentType,
                out resultContent,
                sendProgress,
                receiveProgress );
        }

        /// <summary>
        ///     Copies a file on the server to another place on the
        ///     server
        /// </summary>
        /// <param name="source">
        ///     The source path. It must be non-null and contain at
        ///     least one character.
        /// </param>
        /// <param name="destination">
        ///     The destination path. It must be non-null and contain
        ///     at least one character.
        /// </param>
        /// <param name="allowOverwrite">
        ///     Flag to allow overwrite
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned from the
        ///     server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if there was an
        ///     error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the result content is
        ///     valid.
        /// </returns>
        public bool CopyResource( string source,
            string destination,
            bool allowOverwrite,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string contentType,
            out byte[] resultContent )
        {
            return CopyResource(
                source,
                destination,
                allowOverwrite,
                out resultCode,
                out resultReason,
                out contentType,
                out resultContent,
                null, null);
        }

        /// <summary>
        ///     Moves a file on the server to another place on the
        ///     server
        /// </summary>
        /// <param name="source">
        ///     The source path. It must be non-null and contain at
        ///     least one character.
        /// </param>
        /// <param name="destination">
        ///     The destination path. It must be non-null and contain
        ///     at least one character.
        /// </param>
        /// <param name="allowOverwrite">
        ///     Flag to allow overwrite
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned from the
        ///     server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if there was an
        ///     error.
        /// </param>
        /// <param name="sendProgress">
        ///     Used to monitor the progress of sending the request.
        /// </param>
        /// <param name="receiveProgress">
        ///     Used to monitor the progress of receiving the response.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the result content is
        ///     valid.
        /// </returns>
        public bool MoveResource( string source,
            string destination,
            bool allowOverwrite,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string contentType,
            out byte[] resultContent,
            ProgressContainer sendProgress,
            ProgressContainer receiveProgress )
        {
            return ChangeResourceCmd(MoveRequest,
                source,
                destination,
                allowOverwrite,
                out resultCode,
                out resultReason,
                out contentType,
                out resultContent,
                sendProgress,
                receiveProgress );
        }

        /// <summary>
        ///     Moves a file on the server to another place on the
        ///     server
        /// </summary>
        /// <param name="source">
        ///     The source path. It must be non-null and contain at
        ///     least one character.
        /// </param>
        /// <param name="destination">
        ///     The destination path. It must be non-null and contain
        ///     at least one character.
        /// </param>
        /// <param name="allowOverwrite">
        ///     Flag to allow overwrite
        /// </param>
        /// <param name="resultCode">
        ///     The result code from the server or BadRequest if an
        ///     exception was thrown.
        /// </param>
        /// <param name="resultReason">
        ///     The result message from the server or the exception
        ///     message if an exception was thrown.
        /// </param>
        /// <param name="contentType">
        ///     The reported type of the content returned from the
        ///     server.
        /// </param>
        /// <param name="resultContent">
        ///     The content of the resource or null if there was an
        ///     error.
        /// </param>
        /// <returns>
        ///     true if the call succeeded, false if a communication
        ///     error prevented the request from succeeding. If true,
        ///     the caller should examine the result code and reason
        ///     to determine whether or not the result content is
        ///     valid.
        /// </returns>
        public bool MoveResource( string source,
            string destination,
            bool allowOverwrite,
            out WebDavStatusCode resultCode,
            out string resultReason,
            out string contentType,
            out byte[] resultContent )
        {
            return MoveResource(
                source,
                destination,
                allowOverwrite,
                out resultCode,
                out resultReason,
                out contentType,
                out resultContent,
                null, null );
        }

        /// <summary>
        ///     Send a request to the server. This is a low level call
        ///     for callers who need (or want) access to all headers
        ///     returned by the server.
        /// </summary>
        /// <param name="resourcePath">
        ///     The full path to the resource being acted upon.
        /// </param>
        /// <param name="method">
        ///     The name of the method to use (a valid HTTP or
        ///     WebDAV command.
        /// </param>
        /// <param name="extraHeaders">
        ///     An array of extra headers that should be sent in the
        ///     request.
        /// </param>
        /// <param name="contentType">
        ///     The type of data being sent with the request.
        /// </param>
        /// <param name="content">
        ///     The content body of the request.
        /// </param>
        /// <param name="sendProgress">
        ///     The number of bytes sent so far.
        /// </param>
        /// <param name="receiveProgress">
        ///     The number of bytes received so far.
        /// </param>
        /// <returns>
        ///     The server's response.
        /// </returns>
        protected DavResponse SendRequest( string resourcePath,
                                           string method,
                                           string[] extraHeaders,
                                           string contentType,
                                           byte[] content,
                                           ProgressContainer sendProgress,
                                           ProgressContainer receiveProgress )
        {
            // Make sure required parameters are valid.
            if( ( resourcePath == null ) || ( resourcePath.Length == 0 ) )
            {
                throw new ArgumentException(
                    "The requested resource is invalid.", "resourcePath" );
            }

            if( Array.IndexOf( SupportedMethods, method, 0, SupportedMethods.Length ) < 0 )
            {
                throw new ArgumentException(
                    "The method " + method + " is not supported.", "method" );
            }
            
            // Determine if the message will include some type of content.
            bool msgHasContents = ( contentType != null ) &&
                ( content != null ) && ( content.Length > 0 );

            // Start the headers with the HTTP method, resource path, and protocol
            string headers = method + " " + resourcePath + " " + ProtocolVersion + HeaderTerminator;
            headers += "Host: " + this.ServerUri.Authority + HeaderTerminator;

			// Send authorization credentials if available.
			if( ( username != null ) || ( password != null ) )
			{
				string credentialsUser = username;
				string credentialsPass = password;

				if( credentialsUser == null )
				{
					credentialsUser = "";
				}
				
				if( credentialsPass == null )
				{
					credentialsPass = "";
				}

				byte[] credentials = Encoding.UTF8.GetBytes( username + ":" + password );

				Console.WriteLine( "Credentials = " + credentials );

				string encodedCredentials = System.Convert.ToBase64String(
					 credentials , 0, credentials.Length );

				Console.WriteLine( "encoded credentials = " + encodedCredentials );

				headers = headers + BasicCredentialsHeader + ": " +
					BasicCredentialsType + " " + encodedCredentials + HeaderTerminator;

				Console.WriteLine( "headers = " + headers );
			}

            // Add content headers, if any
            if( msgHasContents )
            {
                headers = headers + ContentLengthHeader + ": " + content.Length + HeaderTerminator;
                headers = headers + ContentTypeHeader + ": " + contentType + HeaderTerminator;
            }
            
            // Add user specified headers
            if( ( extraHeaders != null ) && ( extraHeaders.Length > 0 ) )
            {
                foreach( string header in extraHeaders )
                {
                    headers = headers + header + HeaderTerminator;
                }
            }
            
            // Finish the headers section
            headers = headers + HeaderTerminator;
            
            // Translate the headers into a byte array.
            byte[] headerBytes = Encoding.ASCII.GetBytes( headers );
            
            // Set the total message length to the length of the headers.
            int msgLength = headerBytes.Length;
            
            // If there is any content in the message, add the size of the content to
            // the total message size.
            if( msgHasContents )
            {
                msgLength = msgLength + content.Length;
            }
            
            // Create the message array
            byte[] msg = new byte[ msgLength ];
            
            // Copy the header bytes.
            Array.Copy( headerBytes, 0, msg, 0, headerBytes.Length );

            // If the message has contents, copy those bytes.
            if( msgHasContents )
            {
                Array.Copy( content, 0, msg, headerBytes.Length, content.Length );
            }
            
            //Uncomment the following call to see the message before it is sent.
            /*
               Console.WriteLine( "sending message:\n||{0}||\n",
                Encoding.UTF8.GetString( msg ) );
            */

            // Create a new TCP client to send the message and get a response.
            TcpClient comm = new TcpClient();
            
            Console.WriteLine("connecting to host {0} at port {1}",
                ServerUri.Host, ServerUri.Port );
            // Connect to the server
            comm.Connect( serverUri.Host, ServerUri.Port );
            
            NetworkStream stream = comm.GetStream();

            // Send the message.
            if( sendProgress != null )
            {
                sendProgress.TotalBytesExpected = (ulong) msg.Length;
            }
            
            int sentBytes;
            int bytesToSend;

            for( sentBytes = 0; sentBytes < msg.Length; sentBytes = sentBytes + bytesToSend )
            {
                bytesToSend = comm.SendBufferSize;

                if( sentBytes + bytesToSend > msg.Length )
                {
                    bytesToSend = msg.Length - sentBytes;
                }

                stream.Write( msg, sentBytes, bytesToSend );

                if( sendProgress != null )
                {
                    sendProgress.TotalBytes = (ulong) sentBytes;
                }
            }
            
            if( sendProgress != null )
            {
                sendProgress.TotalBytes = (ulong) sentBytes;
            }

            // Prepare to receive a response
            byte[] responseBuffer = new byte[ comm.ReceiveBufferSize ];            

            int bytesRead = stream.Read( responseBuffer, 0, responseBuffer.Length );
            
            bool finishedHeaders = false;

            DavResponse response = null;

            // Keep reading bytes from the stream until there are none left. Each time
            // bytes are read, convert them into a string.
            while( bytesRead > 0 )
            {
                if( !finishedHeaders )
                {
                    string responseString =
                        Encoding.UTF8.GetString( responseBuffer, 0, bytesRead );
            
                    int currentIndex = 0;
                    int endHeaderLine = responseString.IndexOf( HeaderTerminator );
                    int endOfHeaders = responseString.IndexOf( HeaderTerminator + HeaderTerminator );

                    if( endOfHeaders == -1 )
                    {
                        endOfHeaders = responseString.Length;
                    }

                    string currentHeader = null;

                    while( ( currentIndex < endOfHeaders ) &&
                        ( currentIndex < responseString.Length ) &&
                        ( endHeaderLine >= 0 ) )
                    {
                        string line = responseString.Substring(
                            currentIndex, endHeaderLine - currentIndex );

                        // If the response hasn't been created, the first line is
                        // the status code and needs to be passed to the constructor.
                        if( response == null )
                        {
                            response = new DavResponse( line );
                        }
                        // If there is no current header, the line becomes the current
                        // header.
                        else if( currentHeader == null )
                        {
                            currentHeader = line;
                        }
                        // If the current header is not null and the new line starts
                        // with a space or tab character, it is a continuation of the
                        // current header.
                        else if( ( currentHeader != null ) &&
                            ( line.StartsWith( " " ) || line.StartsWith( "\t" ) ) )
                        {
                            currentHeader = currentHeader + line;
                        }
                        // Otherwise, the line is a new header.
                        else
                        {
                            response.AddHeader( currentHeader );
                            currentHeader = line;
                        }

                        currentIndex =
                            currentIndex + ( endHeaderLine - currentIndex ) + HeaderTerminator.Length;
                        endHeaderLine = responseString.IndexOf( HeaderTerminator, currentIndex );

                        if( ( endHeaderLine > endOfHeaders ) &&
                            ( currentIndex > endOfHeaders ) )
                        {
                            currentIndex = currentIndex + HeaderTerminator.Length;
                        }
                    }
                    
                    // Add the last header.
                    if( currentHeader != null )
                    {
                        response.AddHeader( currentHeader );
                        finishedHeaders = true;

                        if( receiveProgress != null )
                        {
                            receiveProgress.TotalBytesExpected = (ulong) response.ExpectedContentLength;
                        }
                    }
                    
                    // If there are additional characters after the headers that have
                    // been unhandled and either the response includes a content or
                    // a Content-Length header was not provided (ie unknown content
                    // length), add whatever bytes that were read but not processed
                    // to the response content.
                    if( ( currentIndex < responseString.Length ) &&
                        ( ( response.ExpectedContentLength > 0 ) ||
                            ( response.ExpectedContentLength == -1 ) ) )
                    {
                        byte[] processedBytes = Encoding.UTF8.GetBytes(
                            responseString.Substring( 0, currentIndex ) );

                        int contentBeginIndex = processedBytes.Length;

                        response.AppendContent( responseBuffer,
                            contentBeginIndex, bytesRead - contentBeginIndex );

                        if( receiveProgress != null )
                        {
                            receiveProgress.TotalBytes =
                                receiveProgress.TotalBytes + (ulong) ( bytesRead - contentBeginIndex );
                        }
                    }
                }
                else
                {
                    response.AppendContent( responseBuffer, bytesRead );

                    if( receiveProgress != null )
                    {
                        receiveProgress.TotalBytes = receiveProgress.TotalBytes + (ulong) bytesRead;
                    }
                }
                
                // Try to read more bytes from the stream.
                bytesRead = stream.Read( responseBuffer, 0, responseBuffer.Length );
            }

            // Close the connection
            comm.Close();
            
            //Console.WriteLine( "Receieved response:\n{0}\n--------------------\n\n",
            //    response );

            return( response );
        }

        /// <summary>
        ///     Send a request to the server. This is a low level call
        ///     for callers who need (or want) access to all headers
        ///     returned by the server.
        /// </summary>
        /// <param name="resourcePath">
        ///     The full path to the resource being acted upon.
        /// </param>
        /// <param name="method">
        ///     The name of the method to use (a valid HTTP or
        ///     WebDAV command.
        /// </param>
        /// <param name="extraHeaders">
        ///     An array of extra headers that should be sent in the
        ///     request.
        /// </param>
        /// <param name="contentType">
        ///     The type of data being sent with the request.
        /// </param>
        /// <param name="content">
        ///     The content body of the request.
        /// </param>
        /// <returns>
        ///     The server's response.
        /// </returns>
        protected DavResponse SendRequest( string resourcePath,
            string method,
            string[] extraHeaders,
            string contentType,
            byte[] content )
        {
            return SendRequest( resourcePath, method, extraHeaders, contentType, content, null, null );
        }
        
        /// <summary>
        ///     Send a simple request to the server.
        /// </summary>
        /// <param name="resourcePath">
        ///     The full path to the resource being acted upon.
        /// </param>
        /// <param name="method">
        ///     The name of the method to use (a valid HTTP or WebDAV
        ///     command.
        /// </param>
        /// <returns>
        ///     The server's response.
        /// </returns>
        protected DavResponse SendRequest( string resourcePath,
                                           string method )
        {
            return SendRequest( resourcePath, method, null, null, null, null, null );
        }


        /// <summary>
        ///     Checks to see if a given command is supported by
        ///     DavSharp
        /// </summary>
        /// <param name="command">
        ///     The command to test
        /// </param>
        /// <returns>
        ///     true if command is supported
        /// </returns>
        public static bool SupportedCommand(string command)
        {
            foreach( string str in SupportedMethods)
            {
                if(str.Equals(command))
                {
                    return true;
                }
            }
            return false;
        }

        /// <summary>
        ///     The HEAD method.
        /// </summary>
        private const string HeadRequest = "HEAD";

        /// <summary>
        ///     The OPTIONS method.
        /// </summary>
        private const string OptionsRequest = "OPTIONS";

        /// <summary>
        ///     The GET method.
        /// </summary>
        private const string GetRequest = "GET";

        /// <summary>
        ///     The PUT method
        /// </summary>
        private const string PutRequest = "PUT";

        /// <summary>
        ///     The LOCK method
        /// </summary>
        private const string LockRequest = "LOCK";

        /// <summary>
        ///     The UNLOCK method
        /// </summary>
        private const string UnlockRequest = "UNLOCK";

        /// <summary>
        ///     The PROPFIND method.
        /// </summary>
        private const string PropfindRequest = "PROPFIND";

        /// <summary>
        ///     The COPY method.
        /// </summary>
        private const string CopyRequest = "COPY";

        /// <summary>
        ///     The MOVE method.
        /// </summary>
        private const string MoveRequest = "MOVE";

        /// <summary>
        ///     The DELETE method.
        /// </summary>
        private const string DeleteRequest = "DELETE";
        
        ///<summary>
        ///     The Make Collection method
        /// </summary>
    
        private const string MkcolRequest = "MKCOL";

        /// <summary>
        ///     All methods supported by this class.
        /// </summary>
        private static string[] SupportedMethods = new string[]
        {
            CopyRequest,
            DeleteRequest,
            GetRequest,
            HeadRequest,
            LockRequest,
            MkcolRequest,
            MoveRequest,
            OptionsRequest,
            PropfindRequest,
            PutRequest,
            UnlockRequest
        };

        /// <summary>
        ///     The HTTP protocol and version used (1.0).
        /// </summary>
        private const string ProtocolVersion = "HTTP/1.0";

        /// <summary>
        ///     The string that terminates a HTTP header CRLF ("\r\n").
        /// </summary>
        private const string HeaderTerminator = "\r\n";

        /// <summary>
        ///     The header key for the content type.
        /// </summary>
        private const string ContentTypeHeader = "Content-Type";

        /// <summary>
        ///     The header key for the content length.
        /// </summary>
        private const string ContentLengthHeader = "Content-Length";

		/// <summary>
		///		Authorization header for username and password credentials.
		/// </summary>
		private const string BasicCredentialsHeader = "Authorization";

        /// <summary>
        ///     Content-Type header value for XML text content.
        /// </summary>
        private const string XMLTextContent = "text/xml; charset=\"utf-8\"";

        /// <summary>
        ///     Content-Type header value for plain text content.
        /// </summary>
        private const string PlainTextContent = "text/plain; charset=\"utf-8\"";

        /// <summary>
        ///     Content-Type header value for HTML text content.
        /// </summary>
        private const string HTMLTextContent = "text/html; charset=\"utf-8\"";

		/// <summary>
		///		The type of credentials used for Basic HTTP authorization.
		/// </summary>
		private const string BasicCredentialsType = "Basic";

        /// <summary>
        ///     The standard preamble for XML content, specifying
        ///     version (1.0) and encoding (utf-8).
        /// </summary>
        private const string XMLContentPreamble =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        
        /// <summary>
        ///     The standard HTTP port.
        /// </summary>
        private const int HTTPPort = 80;

        /// <summary>
        ///     The timeout for server responses in milliseconds.
        /// </summary>
        private const int ReceiveTimeout = 6000;

        /// <summary>
        ///     The timeout for server requests in milliseconds.
        /// </summary>
        private const int SendTimeout = 6000;

		/// <summary>
		/// The username needed to access a secure realm.
		/// </summary>
		private string username;

		/// <summary>
		/// The password needed to access a secure realm.
		/// </summary>
		private string password;

        /// <summary>
        ///     The Uri of the server this instance is connected to.
        /// </summary>
        private Uri serverUri;

        public bool GetResource(string path,
                         out WebDavStatusCode resultCode,
                         out string resultReason,
                         out string contentType,
                         out FileStream resultContent,
                         ProgressContainer sendProgress,
                         ProgressContainer receiveProgress)
        {
            bool success = true;

            // Make sure the input parameters are valid.
            if ((path == null) || (path.Length == 0))
            {
                throw new ArgumentException("Invalid resource path.", "path");
            }

            // Try to get the resource.
            try
            {
                DavResponse response = SendRequest(path, GetRequest, null, null, null, sendProgress, receiveProgress);
                // Extract the response data for the caller.
                resultCode = response.StatusCode;
                resultReason = response.StatusReason;
                contentType = (string)response.Headers[ContentTypeHeader];
                resultContent = response.fs;
            }
            catch (SocketException e)
            {
                // Flag an exception as a bad request and send the exception message
                // back to the caller.
                resultCode = WebDavStatusCode.BadRequest;
                resultReason = e.Message;
                contentType = null;
                resultContent = null;

                success = false;
            }

            return (success);
        }
    }

}
