package org.pengyou.client.web;


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: May 24, 2006
 * Time: 1:57:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class File {
    String[] fileInfo;

    public String[] getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(String[] fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String getDisplayNameThumb() {
        if (fileInfo[0].length() > 15)
            return fileInfo[0].substring(0, 8)+".."+fileInfo[0].substring(fileInfo[0].length()-4, fileInfo[0].length());
        return fileInfo[0];
    }

    public String getDisplayNameList() {
        if (fileInfo[0].length() > 40)
            return fileInfo[0].substring(0, 20)+"..."+fileInfo[0].substring(fileInfo[0].length()-15, fileInfo[0].length());
        return fileInfo[0];
    }

    public String getDisplayName() {
        return fileInfo[0];
    }

    public String getLastModifiedDate() {
        return fileInfo[3];
    }

    public String getCreationDate() {
        return fileInfo[5];
    }

    public String getContentType() {
        return fileInfo[2];
    }

    public String getContentLength() {
       return fileInfo[1];
    }

    public boolean isCollection() {
        return fileInfo[2].equals("COLLECTION");
    }
    public String   getIcon()
    {
        return MimeTypeIcons.getImgUrl(fileInfo[0], fileInfo[2]);/*        MimeTypeIcons.getImgUrl(this.getDisplayName(), this.getContentType(), );*/
    }

    public String   getIconSmall()
    {
        return MimeTypeIcons.getImgUrlSmall(fileInfo[0], fileInfo[2]);/*        MimeTypeIcons.getImgUrl(this.getDisplayName(), this.getContentType(), );*/
    }
}
