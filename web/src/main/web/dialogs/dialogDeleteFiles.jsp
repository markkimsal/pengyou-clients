<form action="deleteFiles.jsp" method="post" onsubmit="return removeFiles();">
<table id='dialogTable'>
    <tr>
     <th class="dialogHeader">Move files to trash</th>
    </tr>
    <tr>
        <td>You are about to move <span id="deleteFilesNb"></span> files to trash. Please confirm.</td>
    </tr>
    <tr>
        <td align='right'><input type='submit' class='fileAction' value='Delete' /><input type='button' class='fileAction' value='Cancel' OnClick='discardCurrentDialog(); return false;'/></td>
    </tr>
</table>
</form>
