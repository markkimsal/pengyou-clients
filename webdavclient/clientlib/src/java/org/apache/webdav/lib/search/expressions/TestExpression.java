// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search.expressions;

import org.apache.webdav.lib.search.SearchBuilder;
import org.apache.webdav.lib.search.SearchException;
import org.apache.webdav.lib.search.SearchExpression;


/**
 * @author Stefan Lützkendorf
 */
public class TestExpression extends SearchExpression
{
    private String localName;
    private String namespace;
    
    public TestExpression(String namespace, String localname) {
        this.namespace = namespace;
        this.localName = localname;
    }
    
    public void build(SearchBuilder builder) throws SearchException
    {
        builder.buildTest(this);
    }

    public String getLocalName()
    {
        return localName;
    }
    public String getNamespace()
    {
        return namespace;
    }
}
