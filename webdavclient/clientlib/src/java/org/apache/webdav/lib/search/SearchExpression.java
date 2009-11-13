// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search;



/**
 * @author Stefan Lützkendorf
 */
public abstract class SearchExpression
{
    public abstract void build(SearchBuilder builder) 
        throws SearchException;
}
