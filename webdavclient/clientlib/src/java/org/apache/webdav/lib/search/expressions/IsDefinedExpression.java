// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search.expressions;

import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.search.SearchBuilder;
import org.apache.webdav.lib.search.SearchException;
import org.apache.webdav.lib.search.SearchExpression;


/**
 * @author Stefan Lützkendorf
 */
public class IsDefinedExpression extends SearchExpression
{
    private PropertyName propertyName;
    
    public IsDefinedExpression(PropertyName name) {
        this.propertyName = name;
    }
    
    public void build(SearchBuilder builder) throws SearchException
    {
        builder.buildIsdefined(this);
    }

    public PropertyName getPropertyName()
    {
        return propertyName;
    }
}
