using System;
using System.Collections;

namespace DAVSharp
{
    /// <summary>
    /// This class provides various utility functions to the DAVSharp classes.
    /// </summary>
    class DAVSharpUtil
    {
        public static int IndexOfArrayInArray( byte[] source, int sourceStart,
            byte[] search, int searchStart, int searchLength )
        {
            int index = -1;

            for( int i = 0; ( index < 0 ) && ( i < source.Length - searchLength ); i++ )
            {
                bool found = true;

                for( int j = 0; ( j < searchLength ) && ( found == true ); j++ )
                {
                    if( source[ i + j ] != search[ j ] )
                    {
                        found = false;
                    }
                }

                if( found )
                {
                    index = i;
                }
            }

            return( index );
        }

        public static void ParseStatusLine( string statusLine,
            out string protocol, out WebDavStatusCode code, out string reason )
        {
            int space1Index = statusLine.IndexOf( " " );
            int space2Index = statusLine.IndexOf( " ", space1Index + 1 );

            if( space1Index < 0 )
            {
                throw new ArgumentException( "Invalid status line" );
            }

            protocol = statusLine.Substring( 0, space1Index );
            code = (WebDavStatusCode) int.Parse( statusLine.Substring(
                space1Index + 1, space2Index - space1Index - 1 ) );
            reason = statusLine.Substring( space2Index + 1 );
        }

        public static void PrintProperties( Hashtable props, int indent )
        {
            if( props.Count == 0 )
            {
                for( int i = 0; i < indent; i++ )
                {
                    Console.Write( "\t" );
                }
                Console.WriteLine( "Empty" );
            }

            foreach( object key in props.Keys )
            {
                object val = props[ key ];

                for( int i = 0; i < indent; i++ )
                {
                    Console.Write( "\t" );
                }
                
                if( val != null )
                {
                    if( val.GetType() == typeof( Hashtable ) )
                    {
                        Console.WriteLine( "{0} -> Hashtable", key );
                        PrintProperties( (Hashtable) val, indent + 1 );
                    }
                    else if( val.GetType() == typeof( ArrayList ) )
                    {
                        indent++;

                        Console.WriteLine( "{0} -> Array", key );

                        
                        ArrayList list = (ArrayList) val;
                        for( int j = 0; j < list.Count; j++ )
                        {
                            for( int i = 0; i < indent; i++ )
                            {
                                Console.Write( "\t" );
                            }

                            if( list[ j ].GetType() == typeof( Hashtable ) )
                            {
                                Console.WriteLine( "{0} -> Hashtable ({1} entries)",
                                    j, ( (Hashtable) list[ j ] ).Count );
                                PrintProperties( (Hashtable) list[ j ], indent + 1 );
                            }
                            else
                            {
                                Console.WriteLine( "{0} -> {1}", key, val );
                            }
                        }
                        

                        indent--;
                    }
                    else
                    {
                        Console.WriteLine( "{0} -> {1}", key, val );
                    }
                }
                else
                {
                    Console.WriteLine( "{0}", key );
                }
            }
        }
    }
}
