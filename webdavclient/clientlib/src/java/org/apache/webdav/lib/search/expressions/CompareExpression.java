// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search.expressions;

import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.search.CompareOperator;
import org.apache.webdav.lib.search.SearchBuilder;
import org.apache.webdav.lib.search.SearchException;
import org.apache.webdav.lib.search.SearchExpression;


/**
 * @author Stefan Lützkendorf
 */
public class CompareExpression extends SearchExpression
{
    private PropertyName propertyName;
    private CompareOperator operator;
    private Object value;
    
    public CompareExpression(CompareOperator operator, PropertyName name, Object value) {
        this.operator = operator;
        this.propertyName = name;
        this.value = value;
    }
    
    public PropertyName getPropertyName()
    {
        return propertyName;
    }
    public Object getValue()
    {
        return value;
    }
    public CompareOperator getOperator() 
    {
        return this.operator;
    }
    
    public void build(SearchBuilder builder) throws SearchException
    {
        builder.buildCompare(this);
    }
}
