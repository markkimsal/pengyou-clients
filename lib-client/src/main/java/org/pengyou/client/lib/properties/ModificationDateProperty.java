// vi: set ts=3 sw=3:
package org.pengyou.client.lib.properties;

import org.w3c.dom.Element;
import org.pengyou.client.lib.ResponseEntity;


/**
 * <code>DAV:modificationdate</code>
 */
public class ModificationDateProperty extends DateProperty
{

    public static final String TAG_NAME = "modificationdate";

    public ModificationDateProperty(ResponseEntity response, Element element)
    {
        super(response, element);
    }

}
