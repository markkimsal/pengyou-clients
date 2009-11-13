<form action="moveFiles.jsp" method="post" onsubmit="return moveFiles();">
<table id='dialogTable'>
    <tr>
     <th class="dialogHeader">Move clipboard files</th>
    </tr>
    <tr>
        <td>You are about to move <span id="moveFilesNb"></span> files to current directory. Please confirm.</td>
    </tr>
    <tr>
        <td align='right'><input type='submit' class='fileAction' value='Move' /><input type='button' class='fileAction' value='Cancel' OnClick='return discardCurrentDialog();'/></td>
    </tr>
</table>
</form>
