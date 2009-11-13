package org.pengyou.client.web;

import java.util.Comparator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Jul 7, 2006
 * Time: 3:18:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileSort implements Comparator {
    public int compare(Object o1, Object o2) {
        Vector elem1 = ((Vector) o1);
        Vector elem2 = ((Vector) o2);
        String str1 = (String)elem1.get(0);
        String str2 = (String)elem2.get(0);
        return (str1.compareTo(str2));
    }
}
