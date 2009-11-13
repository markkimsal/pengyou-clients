// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search;


/**
 * 
 */
public class SearchScope
{
    public static int DEPTH_0 = 0;
    public static int DEPTH_1 = 1;
    public static int DEPTH_INFINITY = Integer.MAX_VALUE;
    
    private String href;
    private int depth;
    
    public SearchScope(String uri) {
        this.href = uri;
        this.depth = DEPTH_INFINITY;
    }
    
    public SearchScope(String uri, int depth) {
        this.href = uri;
        this.depth = depth;
    }
    
    public int getDepth()
    {
        return depth;
    }
    public String getDepthString()
    {
        if(this.depth == DEPTH_INFINITY) {
            return "infinity";
        } else {
            return Integer.toString(this.depth);
        }
    }
    public String getHref()
    {
        return href;
    }
}
