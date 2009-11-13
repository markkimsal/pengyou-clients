// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search.expressions;

import org.apache.webdav.lib.search.SearchBuilder;
import org.apache.webdav.lib.search.SearchException;
import org.apache.webdav.lib.search.SearchExpression;


/**
 * @author Stefan Lützkendorf
 */
public class NotExpression extends SearchExpression
{

    private SearchExpression expression;
    
    public NotExpression(SearchExpression expression) {
        this.expression = expression;
    }
    
    public void build(SearchBuilder builder) throws SearchException
    {
        builder.buildNot(this);
    }
    public SearchExpression getExpression()
    {
        return expression;
    }
}
