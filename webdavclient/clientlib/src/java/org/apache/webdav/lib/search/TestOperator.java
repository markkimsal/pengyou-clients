package org.apache.webdav.lib.search;

import org.apache.webdav.lib.Constants;
import org.apache.webdav.lib.util.QName;

/**
 * @author stefan
 */
public final class TestOperator extends QName {
    
    public static final TestOperator IS_COLLECTION = new TestOperator(Constants.DAV, "is-collection");
    public static final TestOperator IS_PRINCPAL = new TestOperator(Constants.DAV, "is-principal");

    private TestOperator(String namespaceURI, String localName) {
        super(namespaceURI, localName);
    }
}
