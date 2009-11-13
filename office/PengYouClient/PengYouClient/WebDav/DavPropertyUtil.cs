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
    ///     Provides utilities to parse or otherwise process data
    ///     related to WebDAV properties.
    /// </summary>
    class DavPropertyUtil
    {
        public static byte[] GetPropfindAllRequestContent()
        {
            MemoryStream xmlBuffer = new MemoryStream( 1024 );
            XmlTextWriter writer = new XmlTextWriter( xmlBuffer, Encoding.UTF8 );
                
            writer.WriteStartDocument();
            writer.WriteStartElement( "D", "propfind", "DAV:" );
            writer.WriteStartElement( "allprop", "DAV:" );
            writer.WriteEndElement();
            writer.WriteEndElement();
            writer.Flush();

            byte[] preambleBytes = Encoding.UTF8.GetBytes( XMLContentPreamble );
            int preambleIndex = DAVSharpUtil.IndexOfArrayInArray(
                xmlBuffer.GetBuffer(), 0, preambleBytes, 0, preambleBytes.Length );

            byte[] content = new byte[ xmlBuffer.Length - preambleIndex ];
            Array.Copy( xmlBuffer.GetBuffer(), preambleIndex,
                content, 0, (int) xmlBuffer.Length - preambleIndex );

            return( content );
        }

        public static byte[] GetPropfindNamesRequestContent()
        {
            MemoryStream xmlBuffer = new MemoryStream( 1024 );
            XmlTextWriter writer = new XmlTextWriter( xmlBuffer, Encoding.UTF8 );
                
            writer.WriteStartDocument();
            writer.WriteStartElement( "D", "propfind", "DAV:" );
            writer.WriteStartElement( "propname", "DAV:" );
            writer.WriteEndElement();
            writer.WriteEndElement();
            writer.Flush();

            byte[] preambleBytes = Encoding.UTF8.GetBytes( XMLContentPreamble );
            int preambleIndex = DAVSharpUtil.IndexOfArrayInArray(
                xmlBuffer.GetBuffer(), 0, preambleBytes, 0, preambleBytes.Length );

            byte[] content = new byte[ xmlBuffer.Length - preambleIndex ];
            Array.Copy( xmlBuffer.GetBuffer(), preambleIndex,
                content, 0, (int) xmlBuffer.Length - preambleIndex );

            return( content );
        }

        public static byte[] GetPropfindRequestContent( string[] properties )
        {
            MemoryStream xmlBuffer = new MemoryStream( 1024 );
            XmlTextWriter writer = new XmlTextWriter( xmlBuffer, Encoding.UTF8 );

            writer.WriteStartDocument();
            writer.WriteStartElement( "D", "propfind", "DAV:" );
            writer.WriteStartElement( "prop", "DAV:" );
            
            foreach( string prop in properties )
            {
                string propName;
                string propNamespace;

                ParseNamespace( prop, out propName, out propNamespace );
                
                writer.WriteStartElement( propName, propNamespace );
                writer.WriteEndElement();
            }

            writer.WriteEndElement();
            writer.WriteEndElement();
            writer.Flush();

            byte[] preambleBytes = Encoding.UTF8.GetBytes( XMLContentPreamble );
            int preambleIndex = DAVSharpUtil.IndexOfArrayInArray(
                xmlBuffer.GetBuffer(), 0, preambleBytes, 0, preambleBytes.Length );

            byte[] content = new byte[ xmlBuffer.Length - preambleIndex ];
            Array.Copy( xmlBuffer.GetBuffer(), preambleIndex,
                content, 0, (int) xmlBuffer.Length - preambleIndex );
            
            return( content );
        }

        public static Hashtable ParseMultiStatus( byte[] content )
        {
           return ParseMultiStatus(content, new Hashtable());
        }

        public static Hashtable ParseMultiStatus( byte[] content, Hashtable allResponses)
        {
          //  Hashtable allResponses = new Hashtable();

            XmlTextReader propReader =
                new XmlTextReader( new MemoryStream( content ) );
            propReader.Namespaces = true;
            
            bool foundMultiStatusStart = false;
            bool foundMultiStatusEnd = false;

            while( propReader.Read() )
            {
                switch( propReader.NodeType )
                {
                    case XmlNodeType.Element:
                    {
                        if( propReader.Name.EndsWith( ResponseXMLNode ) )
                        {
                            Hashtable responseProperties = null;

                            string responseHref = ParseResponse(
                                propReader, out responseProperties );
                            
                            allResponses.Add( responseHref, responseProperties );
                        }
                        else if( propReader.Name.EndsWith( MultiStatusXMLNode ) )
                        {
                            foundMultiStatusStart = true;
                        }
                        else
                        {
                            throw new XmlException( 
                                "Invalid PROPFIND response: expected multistatus, got " +
                                propReader.Name + " at line " + propReader.LineNumber +
								":" + propReader.LinePosition, null );
                        }
                        break;
                    }

                    case XmlNodeType.EndElement:
                    {
                        if( propReader.Name.EndsWith( MultiStatusXMLNode ) )
                        {
                            foundMultiStatusEnd = true;
                            propReader.Close();
                        }
                        else
                        {
							throw new XmlException( "Malformed content at line " +
								propReader.LineNumber + ":" + propReader.LinePosition, null );
                        }

                        if( foundMultiStatusStart != foundMultiStatusEnd )
                        {
                            throw new XmlException( "Malformed content at line " +
								propReader.LineNumber + ":" + propReader.LinePosition, null );
                        }
                        break;
                    }
                }
            }
            return( allResponses );
        }

        private static string ParseResponse(
            XmlTextReader reader, out Hashtable properties )
        {
            string responseHref = null;
            properties = new Hashtable();
            bool foundResponseEnd = false;

            while( !foundResponseEnd )
            {
                reader.Read();

                switch( reader.NodeType )
                {
                    case XmlNodeType.Element:
                    {
                        if( reader.Name.EndsWith( HrefXMLNode ) )
                        {
                            responseHref = ParseHref( reader );
                        }
                        else if( reader.Name.EndsWith( PropstatXMLNode ) )
                        {
                            Hashtable props = new Hashtable();
                            WebDavStatusCode propertyStatus =
                                ParsePropstat( reader, ref props );

                            properties.Add( propertyStatus, props );
                        }
                        else if( reader.Name.EndsWith( ResponseDescriptionXMLNode ) )
                        {
                            string description = ParseResponseDescription( reader );
                            properties.Add( "description", description );
                        }
                        else
                        {
                            throw new XmlException( "Malformed response at line " +
								reader.LineNumber + ":" + reader.LinePosition, null );
                        }
                        break;
                    }

                    case XmlNodeType.EndElement:
                    {
                        if( reader.Name.EndsWith( ResponseXMLNode ) )
                        {
                            foundResponseEnd = true;
                        }
                        else
                        {
                            throw new XmlException( "Malformed response at line " +
								reader.LineNumber + ":" + reader.LinePosition, null );
                        }
                        break;
                    }
                }
            }

            if( responseHref == null )
            {
                throw new XmlException( "Malformed response at line " +
					reader.LineNumber + ":" + reader.LinePosition, null );
            }

            return( responseHref );
        }

        private static string ParseHref( XmlTextReader reader )
        {
            bool foundHrefEnd = false;
            string href = null;

            while( !foundHrefEnd )
            {
                reader.Read();

                switch( reader.NodeType )
                {
                    case XmlNodeType.Text:
                    case XmlNodeType.CDATA:
                    {
                        href = reader.Value;
                        break;
                    }

                    case XmlNodeType.EndElement:
                    {
                        if( reader.Name.EndsWith( HrefXMLNode ) )
                        {
                            foundHrefEnd = true;
                        }
                        else
                        {
                            throw new XmlException( "Malformed response at line " +
								reader.LineNumber + ":" + reader.LinePosition, null );
                        }

                        break;
                    }
                }
            }

            return( href );
        }

        private static WebDavStatusCode ParsePropstat(
            XmlTextReader reader, ref Hashtable properties )
        {
            bool foundPropstatEnd = false;
            WebDavStatusCode propStatus = WebDavStatusCode.NotImplemented;

            while( !foundPropstatEnd )
            {
                reader.Read();

                switch( reader.NodeType )
                {
                    case XmlNodeType.Element:
                    {
                        if( reader.Name.EndsWith( PropXMLNode ) )
                        {
                            ParseProp( reader, ref properties );
                        }
                        else if( reader.Name.EndsWith( StatusXMLNode ) )
                        {
                            string protocol = null;
                            string description = null;

                            propStatus = ParseStatus(
                                reader, out protocol, out description );

                            properties.Add( DAVSharpReasonCodeKey, propStatus );
                            properties.Add( DAVSharpReasonStringKey, description );
                        }
                        else if( reader.Name.EndsWith( ResponseDescriptionXMLNode ) )
                        {
                            string description = ParseResponseDescription( reader );
                        }
                        else
                        {
							throw new XmlException(
								"Malformed propstat: unknown node type {0} at line " +
								reader.LineNumber + ":" + reader.LinePosition, null );
                        }
                        break;
                    }

                    case XmlNodeType.EndElement:
                    {
                        if( reader.Name.EndsWith( PropstatXMLNode ) )
                        {
                            foundPropstatEnd = true;
                        }
                        else
                        {
							throw new XmlException( "Malformed response at line " +
								reader.LineNumber + ":" + reader.LinePosition, null );
                        }

                        break;
                    }
                }
            }

            return( propStatus );
        }

        private static string ParseResponseDescription( XmlTextReader reader )
        {
            string description = null;
            bool foundResponseDescriptionEnd = false;

            while( !foundResponseDescriptionEnd )
            {
                reader.Read();

                switch( reader.NodeType )
                {
                    case XmlNodeType.Text:
                    case XmlNodeType.CDATA:
                    {
                        description = reader.Value;
                        break;
                    }

                    case XmlNodeType.EndElement:
                    {
                        if( reader.Name.EndsWith( ResponseDescriptionXMLNode ) )
                        {
                            foundResponseDescriptionEnd = true;
                        }
                        else
                        {
							throw new XmlException( "Malformed response at line " +
								reader.LineNumber + ":" + reader.LinePosition, null );
                        }

                        break;
                    }
                }
            }

            return( description );
        }

        private static WebDavStatusCode ParseStatus(
            XmlTextReader reader, out string protocol, out string reason )
        {
            bool foundStatusEnd = false;
            WebDavStatusCode code = WebDavStatusCode.NotImplemented;
            protocol = null;
            reason = null;

            while( !foundStatusEnd )
            {
                reader.Read();

                switch( reader.NodeType )
                {
                    case XmlNodeType.Text:
                    case XmlNodeType.CDATA:
                    {
                        DAVSharpUtil.ParseStatusLine(
                            reader.Value, out protocol, out code, out reason );
                        break;
                    }

                    case XmlNodeType.EndElement:
                    {
                        if( reader.Name.EndsWith( StatusXMLNode ) )
                        {
                            foundStatusEnd = true;
                        }
                        else
                        {
							throw new XmlException( "Malformed status at line " +
								reader.LineNumber + ":" + reader.LinePosition, null );
                        }

                        break;
                    }
                }
            }

            return( code );
        }

        private static void ParseProp( XmlTextReader reader, ref Hashtable properties )
        {
            bool foundPropEnd = false;

            while( !foundPropEnd )
            {
                reader.Read();

                switch( reader.NodeType )
                {
                    case XmlNodeType.Element:
                    {
                        if( reader.IsEmptyElement )
                        {
                            properties.Add( ExpandNamespace( reader ), null );
                        }
                        else
                        {
                            string propName = ExpandNamespace( reader );
                            object propValue = null;

                            ParseOneProperty( reader, reader.Name, out propValue );
                            
                            object existingVal = properties[ propName ];
                            if( existingVal != null )
                            {
                                if( existingVal.GetType() == typeof( ArrayList ) )
                                {
                                    ( (ArrayList) existingVal ).Add( propValue );
                                }
                                else
                                {
                                    ArrayList valList = new ArrayList( 2 );
                                    valList.Add( existingVal );
                                    valList.Add( propValue );

                                    properties[ propName ] = valList;
                                }
                            }
                            else
                            {
                                properties.Add( propName, propValue );
                            }
                        }
                        break;
                    }

                    case XmlNodeType.EndElement:
                    {
                        if( reader.Name.EndsWith( PropXMLNode ) )
                        {
                            foundPropEnd = true;
                        }
                        else
                        {
							throw new XmlException( "Malformed prop at line " +
								reader.LineNumber + ":" + reader.LinePosition, null );
                        }

                        break;
                    }
                }
            }
        }

        private static void ParseOneProperty(
            XmlTextReader reader, string propName, out object propValue )
        {
            bool foundPropEnd = false;
            
            propValue = null;

            while( !foundPropEnd )
            {
                reader.Read();

                switch( reader.NodeType )
                {
                    case XmlNodeType.Element:
                    {
                        if( propValue == null )
                        {
                            propValue = new Hashtable();
                        }
                        
                        if( reader.IsEmptyElement )
                        {
                            ( (Hashtable) propValue ).Add(
                                ExpandNamespace( reader ), null );
                        }
                        else
                        {
                            string subName = ExpandNamespace( reader );
                            object subValue = null;

                            ParseOneProperty( reader, reader.Name, out subValue );

                            object existingVal = ( (Hashtable) propValue )[ propName ];
                            if( existingVal != null )
                            {
                                if( existingVal.GetType() == typeof( ArrayList ) )
                                {
                                    ( (ArrayList) existingVal ).Add( subValue );
                                }
                                else
                                {
                                    ArrayList valList = new ArrayList( 2 );
                                    valList.Add( existingVal );
                                    valList.Add( subValue );

                                    ( (Hashtable) propValue )[ propName ] = valList;
                                }
                            }
                            else
                            {
                                ( (Hashtable) propValue ).Add( propName, subValue );
                            }
                        }
                        
                        break;
                    }

                    case XmlNodeType.Text:
                    case XmlNodeType.CDATA:
                    {
                        propValue = reader.Value;
                        break;
                    }

                    case XmlNodeType.EndElement:
                    {
                        if( reader.Name.EndsWith( propName ) )
                        {
                            foundPropEnd = true;
                        }
                        else
                        {
                            throw new XmlException( "Malformed prop: " + propName + " at line " +
								reader.LineNumber + ":" + reader.LinePosition, null );
                        }

                        break;
                    }
                }
            }
        }

        private static string ExpandNamespace( XmlTextReader reader )
        {
            string propName = reader.Name;
            string propNamespace = reader.LookupNamespace( reader.Prefix );
            
            int namespaceBreak = propName.IndexOf( XmlNamespaceSeperator );
            
            string expandedName;

            if( namespaceBreak < 0 )
            {
                expandedName = propName;
            }
            else
            {
                expandedName = propNamespace + propName.Substring( namespaceBreak + 1 );
            }

            return( expandedName );
        }

        private static bool ParseNamespace(
            string text, out string name, out string ns )
        {
            bool success = false;
            name = null;
            ns = null;

            int index = text.IndexOf( XmlNamespaceSeperator );

            if( index >= 0 )
            {
                name = text.Substring( index + 1 );
                ns = text.Substring( 0, index + 1 );
                success = true;
            }

            return( success );
        }

        private const string DavNamespace = "DAV";

        private const string MultiStatusXMLNode = ":multistatus";
        private const string ResponseXMLNode = ":response";
        private const string HrefXMLNode = ":href";
        private const string PropstatXMLNode = ":propstat";
        private const string PropXMLNode = ":prop";
        private const string StatusXMLNode = ":status";
        private const string ResponseDescriptionXMLNode = ":responsedescription";

        private const string XmlNamespaceSeperator = ":";

        private const string ResourceTypeXMLNode = ":resourcetype";
        private const string CollectionXMLNode = ":collection";

        public const string DavResourceTypeProp = DavNamespace + ResourceTypeXMLNode;
        public const string DavCollectionProp = DavNamespace + CollectionXMLNode;
        public const string DavHrefProp = DavNamespace + HrefXMLNode;

        public const string DavContentLengthProp = DavNamespace + ":getcontentlength";
        public const string DavLastModifiedProp = DavNamespace + ":getlastmodified";

        /// <summary>
        /// The standard preamble for XML content, specifying version (1.0) and
        /// encoding (utf-8).
        /// </summary>
        private const string XMLContentPreamble =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        
        public const string DAVSharpReasonCodeKey = "DAV Sharp Reason Code Key";
        public const string DAVSharpReasonStringKey = "DAV Sharp Reason String Key";
    }
}
 