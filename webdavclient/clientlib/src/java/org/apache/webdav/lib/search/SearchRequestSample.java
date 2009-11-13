package org.apache.webdav.lib.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.webdav.lib.PropertyName;

/**
 * @author stefan
 */
public class SearchRequestSample {

    public static void main(String[] args) {
        try {
            test1();
            testWithScopes();
            testWithVariables();
        }
        catch (SearchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static final PropertyName DISPLAYNAME = new PropertyName("DAV:", "displayname");
    static final PropertyName LASTMODIFIED = new PropertyName("DAV:", "getlastmodified");
    static final PropertyName ABSTRACT = new PropertyName("http://any.domain/test/", "abstract");
    static final PropertyName KEYWORDS = new PropertyName("http://any.domain/test/", "keywords");

    
    public static void test1() throws SearchException {
        SearchRequest s = new SearchRequest();
        
        s.addSelection(DISPLAYNAME);
        s.addSelection(LASTMODIFIED);
        
        s.addScope(new SearchScope("files"));
        
        SearchExpression expression= 
            s.and(s.compare(CompareOperator.LIKE, DISPLAYNAME, "%.xml"),
                  s.and(s.compare(CompareOperator.GT, LASTMODIFIED, new Date(1100000000000L)),
                	    s.compare(CompareOperator.LT, LASTMODIFIED, new Date(1121212121212L))));
        
        s.setWhereExpression(expression);
        
        System.out.println(s.asString());
    }
    
    public static void testWithVariables() throws SearchException {
        SearchRequest s = new SearchRequest();
        
        s.addSelection(DISPLAYNAME);
        s.addSelection(LASTMODIFIED);
        
        s.addScope(new SearchScope("files/docs1"));
        s.addScope(new SearchScope("files/docs2"));
        
        SearchExpression expression= s.compare(CompareOperator.PROPERTY_CONTAINS, 
                ABSTRACT, s.variable("TEXT"));
        
        s.setWhereExpression(expression);
        
        Map variables = new HashMap();
        variables.put("TEXT", "aber hallo");
        System.out.println(s.asString(variables));
        
        variables.put("TEXT", "holla blokes");
        System.out.println(s.asString(variables));
    }
    
    public static void testWithScopes() throws SearchException {
        SearchRequest s = new SearchRequest();
        
        s.addSelection(DISPLAYNAME);
        s.addSelection(LASTMODIFIED);
        
        SearchExpression expression = s.or( 
                s.isDefined(ABSTRACT),
                s.isDefined(KEYWORDS));
        
        s.setWhereExpression(expression);
        
        System.out.println(s.asString());

        List scopes = new ArrayList();
        scopes.add(new SearchScope("files/docs1"));
        scopes.add(new SearchScope("files/docs2"));
        
        System.out.println(s.asString(scopes));
    }
}
