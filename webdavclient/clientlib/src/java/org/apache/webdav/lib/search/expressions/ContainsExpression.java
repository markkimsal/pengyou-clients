// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search.expressions;

import org.apache.webdav.lib.search.SearchBuilder;
import org.apache.webdav.lib.search.SearchException;
import org.apache.webdav.lib.search.SearchExpression;



/**
 * @author Stefan Lützkendorf
 */
public class ContainsExpression extends SearchExpression
{
    private String text;
    
    public ContainsExpression(String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void  build(SearchBuilder builder) throws SearchException
    {
        builder.buildContains(this);
    }
}
