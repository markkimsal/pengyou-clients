<form action="moveFiles.jsp" method="post" onsubmit="return createFolder(document.getElementById('newFolderName').value);">
<table id='dialogTable'>
    <tr>
     <th class="dialogHeader">Create new folder</th>
    </tr>
    <tr>
        <td><input type="text" name="folderName" id="newFolderName" value="Please type a name for your new folder" onclick="if(this.value=='Please type a name for your new folder')this.value='';"/></td>
    </tr>
    <tr>
        <td align='right'><input type='submit' class='fileAction' value='Create' /><input type='button' class='fileAction' value='Cancel' OnClick='return discardCurrentDialog();'/></td>
    </tr>
</table>
</form>
