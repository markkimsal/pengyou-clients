package org.pengyou.client.web;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;

import javax.servlet.ServletContext;
import javax.servlet.Servlet;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Oct 14, 2006
 * Time: 11:14:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationSingleton {
    private static ConfigurationSingleton conf;
    private static Document doc = null;
    private static String defaultConfigurationEntity = "";
    private static String username="";
    private static String password="";

    private Document parse(InputStream stream) throws DocumentException {
        try {
            SAXReader reader = new SAXReader();
            return reader.read(stream);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public String getConfigurationValue(String nodeName, String Key) {

        Node node = doc.selectSingleNode("//PengYouWeb/"+nodeName+"[@attribute='" + Key + "']");
        String value = node.valueOf("value");
        node.hasContent();
        return value;
    }

    public  void    setDefaultConfigurationEntity(String Entity)
    {
            defaultConfigurationEntity = Entity;
    }

    // Private constructor suppresses generation of a (public) default constructor
    private ConfigurationSingleton(InputStream stream) throws Exception {

        try {
            doc = parse(stream);
        } catch (DocumentException e) {
            throw new Exception("Can't parse xml resources", e);
        }
    }

    public static ConfigurationSingleton getInstance(InputStream stream) {
            if (conf == null)
                try{
                    conf = new ConfigurationSingleton(stream);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            return conf;
        }

    public static ConfigurationSingleton getInstance() {
             return conf;
        }

    public static void SetUsername(String usr)
    {
        username = usr;
    }
    public static void SetPassword(String pass)
    {
        password = pass;
    }

    public static String GetUsername()
    {
        return username;
    }

    public static String GetPassword()
    {
        return password;
    }

}
