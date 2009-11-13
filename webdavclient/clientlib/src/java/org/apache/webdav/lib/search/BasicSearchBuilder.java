// vi: set ts=3 sw=3:
package org.apache.webdav.lib.search;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.xmlio.out.XMLEncode;

import org.apache.webdav.lib.Constants;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.search.SearchRequest.Variable;
import org.apache.webdav.lib.search.expressions.AndExpression;
import org.apache.webdav.lib.search.expressions.CompareExpression;
import org.apache.webdav.lib.search.expressions.ContainsExpression;
import org.apache.webdav.lib.search.expressions.IsDefinedExpression;
import org.apache.webdav.lib.search.expressions.NotExpression;
import org.apache.webdav.lib.search.expressions.OrExpression;
import org.apache.webdav.lib.search.expressions.TestExpression;
import org.apache.webdav.lib.util.QName;


/**
 * @author Stefan Lützkendorf
 */
public class BasicSearchBuilder implements SearchBuilder
{
    private static DateFormat dateFormat = new SimpleDateFormat(
            "EEE, d MMM yyyy kk:mm:ss z", Locale.US);

    private Map variables;
    private StringWriter writer;
    
    public String build(SearchRequest search, Map variables, List scopes) 
        throws SearchException
    {
        this.variables = variables;
        
        this.writer = new StringWriter(); 
        
        writer.write("<D:searchrequest xmlns:D='DAV:'>\n");
        writer.write("<D:basicsearch>\n");
        writeSelection(search, writer);
        writeFrom(scopes, writer);
        
        if (search.getWhereExpression() != null) {
            writer.write("<D:where>\n");
            search.getWhereExpression().build(this);
            writer.write("</D:where>\n");
        }
        
        writer.write("</D:basicsearch>\n");
        writer.write("</D:searchrequest>\n");
        
        return writer.toString();
    }
    
    private static void writeSelection(SearchRequest search, StringWriter writer)
    {
        Iterator i = search.getSelection();
        if (i.hasNext()) {
            writer.write("<D:select><D:prop>\n");
            for(;i.hasNext();) {
                writer.write("<");
                writeQNameStart((PropertyName)i.next(), writer);
                writer.write("/>\n");
            }
            writer.write("</D:prop></D:select>\n");
        }
    }
    
   

    private static void writeFrom(List scopes, StringWriter writer) {
        writer.write("<D:from>\n");
        for(int i = 0, l = scopes.size(); i < l; i++) {
            SearchScope scope = (SearchScope)scopes.get(i);
            writer.write("<D:scope><D:href>");
            writer.write(XMLEncode.xmlEncodeText(scope.getHref()));
            writer.write("</D:href><D:depth>");
            writer.write(scope.getDepthString());
            writer.write("</D:depth></D:scope>\n");
        }
        writer.write("</D:from>\n");
    }
    
    private static void writeQNameStart(QName name, StringWriter writer) 
    {
        if (name.getNamespaceURI().equals(Constants.DAV)) {
            writer.write("D:");
            writer.write(name.getLocalName());
        } else {
            writer.write(name.getLocalName());
            writer.write(" xmlns='");
            writer.write(name.getNamespaceURI());
            writer.write("'");
        }
    }
    private static void writeQNameEnd(QName name, StringWriter writer) 
    {
        if (name.getNamespaceURI().equals(Constants.DAV)) {
            writer.write("D:");
            writer.write(name.getLocalName());
        } else {
            writer.write(name.getLocalName());
        }
    }
    
    
    public void buildAnd(AndExpression and) throws SearchException
    {
        writer.write("<D:and>\n");
        for(Iterator i = and.getExpressions(); i.hasNext();) {
            SearchExpression expression = (SearchExpression)i.next();
            expression.build(this);
        }
        writer.write("</D:and>\n");
    }
    
    public void buildOr(OrExpression or) throws SearchException
    {
        writer.write("<D:or>\n");
        for(Iterator i = or.getExpressions(); i.hasNext();) {
            SearchExpression expression = (SearchExpression)i.next();
            expression.build(this);
        }
        writer.write("</D:or>\n");
    }
    
    public void buildNot(NotExpression expression) throws SearchException
    {
        writer.write("<D:not>\n");
        expression.getExpression().build(this);
        writer.write("</D:not>\n");
    }
    
    public void buildCompare(CompareExpression compare) throws SearchException
    {
        writer.write("<");
        writeQNameStart(compare.getOperator(), writer);
        writer.write(">");
        
        writer.write("<D:prop><");
        writeQNameStart(compare.getPropertyName(), writer);
        writer.write("/></D:prop>");
        
        writer.write("<D:literal>");
        writer.write(XMLEncode.xmlEncodeText(valueToString(compare.getValue())));
        writer.write("</D:literal>");
        
        writer.write("</");
        writeQNameEnd(compare.getOperator(), writer);
        writer.write(">\n");
     
    }
    
    public void buildContains(ContainsExpression contains) 
        throws SearchException
    {
        writer.write("<D:contains>");
        writer.write(XMLEncode.xmlEncodeText(contains.getText()));
        writer.write("</D:contains>\n");
    }
    
    public void buildIsdefined(IsDefinedExpression expression)
        throws SearchException {
        writer.write("<D:is-defined><D:prop><");
        writeQNameStart(expression.getPropertyName(), writer);
        writer.write("/></D:prop></D:is-defined>\n");
    }
    public void buildTest(TestExpression expression)
        throws SearchException 
    {
        writer.write("<");
        if (expression.getNamespace().equals(Constants.DAV)) {
            writer.write("D:");
            writer.write(expression.getLocalName());
        } else {
            writer.write(expression.getLocalName());
            writer.write(" xmlns='");
            writer.write(expression.getNamespace());
            writer.write("'");
        }
        writer.write("/>");
    }
    
    private String valueToString(Object value) throws SearchException {
        if (value instanceof Date) {
            synchronized(dateFormat) {
                return dateFormat.format((Date)value);
            }
        }
        if (value instanceof Variable) {
            Variable var = (Variable)value;
            Object val = this.variables.get(var.getName());
            if (val == null) val = var.getDefaultValue();
            if (val == null) {
                throw new SearchException("Unset variable: " + var.getName());
            }
            return valueToString(val);
        }
        // otherwise
        return value.toString();
    }
}
