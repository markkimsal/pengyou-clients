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

package org.pengyou.client.lib.test;

import junit.framework.TestCase;
import org.pengyou.client.lib.DavResource;
import org.pengyou.client.lib.DavContext;

import java.io.*;
import java.util.Vector;

public class DavRessourceTest extends TestCase {
   DavContext context;

    protected void setUp() throws Exception {
        super.setUp();
        //context = new DavContext("http://192.168.3.31", 8181, "/PengYouWebapp/repository/default/");
        context = new DavContext("http://127.0.0.1", 8080, "/repository/default/");
        context.setUsername("jeremi");
        context.setPassword("pwd");
    }

    public void testGetContentAsStream()
    {
        DavResource dav = new DavResource("test/testEmptyDoc", context);
        try {
            assertNotNull(dav.getContentAsStream());
            assertFalse(dav.isCollection());
        } catch (IOException e) {
            assertTrue(false);
        }
    }


    public void testGetError404() throws IOException {
        DavResource dav = new DavResource("test/QSDFQSDFEsg", context);
        assertFalse(dav.isExist());
        assertNull(dav.getContentAsStream());
    }

    public void testGetDirectory()
    {
        DavResource dav = new DavResource("test/testListDirectory", context);
        try {
            assertNotNull(dav.getContentAsStream());
            assertTrue(dav.isCollection());
            Vector childrens = dav.listBasic();
            assertEquals(3, childrens.size());
        } catch (IOException e) {
            assertTrue(false);
        }
    }

    public void testSave() throws IOException {
        DavResource dav = new DavResource("qsdfcq.txt", context);
        dav.setNew(true);

        File tmpFile = File.createTempFile("__importcontext", "tmp");
        FileOutputStream out = new FileOutputStream(tmpFile);

        PrintStream p = new PrintStream( out );
        p.println ("This is written to a file");
        p.close();


        dav.setContent(new FileInputStream(tmpFile));
        dav.setContentType("text/plain");
        assertTrue(dav.save());

    }

    public void testExport() throws IOException {
        DavResource dav = new DavResource("CDC.doc", context);
        assertNotNull(dav.getExportFormat());
        File tmpFile = File.createTempFile("__importcontext", "tmp");
        FileOutputStream out = new FileOutputStream(tmpFile);

        assertTrue(tmpFile.length() == 0);
        dav.getExportContentAsStream("odt", out);
        assertTrue(tmpFile.length() > 0);
        assertTrue(dav.getStatusCode() >= 200 && dav.getStatusCode() < 300);
    }
}
