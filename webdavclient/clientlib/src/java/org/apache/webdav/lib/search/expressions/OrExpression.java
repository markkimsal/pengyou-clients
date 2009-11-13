// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search.expressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.webdav.lib.search.SearchBuilder;
import org.apache.webdav.lib.search.SearchException;
import org.apache.webdav.lib.search.SearchExpression;


/**
 * @author Stefan Lützkendorf
 */
public class OrExpression  extends SearchExpression
{
 
    private List expressions = new ArrayList();
    
    public OrExpression() {
        
    }
    
    public void add(SearchExpression expression) {
        this.expressions.add(expression);
    }
    
    public Iterator getExpressions() {
        return this.expressions.iterator();
    }

    public void build(SearchBuilder builder) throws SearchException {
        builder.buildOr(this);
    }
}
