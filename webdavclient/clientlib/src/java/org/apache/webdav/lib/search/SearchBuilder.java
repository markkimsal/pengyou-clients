// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search;

import java.util.List;
import java.util.Map;

import org.apache.webdav.lib.search.expressions.AndExpression;
import org.apache.webdav.lib.search.expressions.CompareExpression;
import org.apache.webdav.lib.search.expressions.ContainsExpression;
import org.apache.webdav.lib.search.expressions.IsDefinedExpression;
import org.apache.webdav.lib.search.expressions.NotExpression;
import org.apache.webdav.lib.search.expressions.OrExpression;
import org.apache.webdav.lib.search.expressions.TestExpression;


/**
 * @author Stefan Lützkendorf
 */
public interface SearchBuilder
{
    public String build(SearchRequest search, Map variables, List scopes) 
            throws SearchException;
    
    public void buildAnd(AndExpression and) 
            throws SearchException;
    public void buildOr(OrExpression or)
            throws SearchException;
    public void buildNot(NotExpression expression) 
            throws SearchException;
    public void buildCompare(CompareExpression compare) 
            throws SearchException;
    public void buildContains(ContainsExpression contains) 
            throws SearchException;
    public void buildIsdefined(IsDefinedExpression expression)
            throws SearchException;
    public void buildTest(TestExpression expression)
            throws SearchException;
}
