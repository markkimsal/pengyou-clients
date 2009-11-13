/* ====================================================================
 *   Copyright 2005 Jérémi Joslin.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */

package org.pengyou.client.lib.properties;

import org.pengyou.client.lib.BaseProperty;
import org.pengyou.client.lib.ResponseEntity;
import org.pengyou.client.lib.util.DOMUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.ArrayList;

public class ExportProperty  extends BaseProperty {
    public static final String TAG_NAME = "exportformat";

    /**
     * Default constructor for the property.
     */
    public ExportProperty(ResponseEntity response, Element element) {
        super(response, element);
    }

    public List getExportFormat(){
        NodeList children = element.getChildNodes();
        if (children == null || children.getLength() == 0)
            return null;

        List result = new ArrayList();
        for (int i = 0; i < children.getLength(); i++) {
            try {
                Element child = (Element) children.item(i);
                String namespace = DOMUtils.getElementNamespaceURI(child);
                if (namespace != null && namespace.equals("DAV:")) {
                    String localName = DOMUtils.getElementLocalName(child);
                    if ("format".equals(localName)) {
                        result.add(DOMUtils.getTextValue(child));
                    }
                }
            } catch (ClassCastException e) {
            }
        }
        return result;
    }

}
