// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.webdav.lib.PropertyName;
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
public class SearchRequest
{
    private List selections = new ArrayList();
    private List scopes = new ArrayList();
    private SearchExpression whereClause = null;
    
    public void addSelection(PropertyName propertyName) {
        this.selections.add(propertyName);
    }
    public Iterator getSelection() {
        return this.selections.iterator();
    }
    public void addScope(SearchScope scope) {
        this.scopes.add(scope);
    }
    
    public void setWhereExpression(SearchExpression expression) {
        this.whereClause = expression;
    }
    public SearchExpression getWhereExpression() {
        return this.whereClause;
    }

    /**
     * Factory method for compare expressions.
     * @param op operator to use while comparision  
     * @param property property tp be compared
     * @param value value to be used for comparision.
     */
    public CompareExpression compare(CompareOperator op, PropertyName property, Object value) {
        return new CompareExpression(op, property, value);
    }
    /**
     * Factory method for <code>contains</code> expression.
     * @param text the text that should be contained.
     */
    public ContainsExpression contains(String text) {
        return new ContainsExpression(text);
    }
    /**
     * Factory method for <code>and</code> expression.
     * @param e1 first expression 
     * @param e2 second expression
     */
    public AndExpression and(SearchExpression e1, SearchExpression e2) {
        AndExpression and = new AndExpression();
        and.add(e1);
        and.add(e2);
        return and;
    }
    /**
     * Factory method for <code>and</code> expression.
     * @param e1 first expression 
     * @param e2 second expression
     * @param e3 third expression
     */
    public AndExpression and(SearchExpression e1, SearchExpression e2, SearchExpression e3) {
        AndExpression and = new AndExpression();
        and.add(e1);
        and.add(e2);
        and.add(e3);
        return and;
    }
    /**
     * Factory method for <code>or</code> expression.
     * @param e1 first expression 
     * @param e2 second expression
     */
    public OrExpression or(SearchExpression e1, SearchExpression e2) {
        OrExpression or = new OrExpression();
        or.add(e1);
        or.add(e2);
        return or;
    }
    /**
     * Factory method for <code>and</code> expression.
     * @param e1 first expression 
     * @param e2 second expression
     * @param e3 third expression
     */
    public OrExpression or(SearchExpression e1, SearchExpression e2, SearchExpression e3) {
        OrExpression or = new OrExpression();
        or.add(e1);
        or.add(e2);
        or.add(e3);
        return or;
    }
    /**
     * Factory method for <code>not</code> expressions.
     * @param ex expression to be negated
     */
    public NotExpression not(SearchExpression ex) {
        return new NotExpression(ex);
    }
    
    /**
     * Factory method for <code>is-defined</code> expressions.
     * @param property the name of the property to be checked
     */
    public IsDefinedExpression isDefined(PropertyName property) {
        return new IsDefinedExpression(property);
    }
    /**
     * Factory method for <code>is-????</code> expressions.
     * @param operator
     */
    public TestExpression test(TestOperator operator) {
        return new TestExpression(operator.getNamespaceURI(), operator.getLocalName());
    }
    
    /**
     * Factory method for variables. They are intended as placeholders for values
     * in other expressions.
     * @param name the variables name
     */
    public Object variable(String name) {
        return variable(name, null);
    }
    
    /**
     * Factory method for variables. They are intended as placeholders for values
     * in other expressions.
     * @param name the variables name
     * @param defaultValue the variables defaul value
     */
    public Object variable(String name, Object defaultValue) {
        return new Variable(name, defaultValue);
    }

    
    
    public String asString() throws SearchException {
        SearchBuilder builder = new BasicSearchBuilder();
        return builder.build(this, Collections.EMPTY_MAP, this.scopes);
    }

    public String asString(List scopes) throws SearchException {
        SearchBuilder builder = new BasicSearchBuilder();
        return builder.build(this, Collections.EMPTY_MAP, scopes);
    }
    public String asString(Map variables) throws SearchException {
        SearchBuilder builder = new BasicSearchBuilder();
        return builder.build(this, variables, this.scopes);
    }
    public String asString(Map variables, List scopes) throws SearchException {
        SearchBuilder builder = new BasicSearchBuilder();
        return builder.build(this, variables, scopes);
    }
    
    
    public static class Variable {
        private String name;
        private Object defaultValue;
        Variable(String name, Object value) {
            if (value instanceof Variable) 
                throw new IllegalArgumentException("Variables values must not be variables.");
            this.name = name;
            this.defaultValue = value;
        }
        public Object getDefaultValue()
        {
            return defaultValue;
        }
        public String getName()
        {
            return name;
        }
    }
}
