<form action="copyFiles.jsp" method="post" onsubmit="discardCurrentDialog();displayCache();return copyFiles();">
<table id='dialogTable'>
    <tr>
     <th class="dialogHeader">Copy clipboard files</th>
    </tr>
    <tr>
        <td>You are about to copy <span id="copyFilesNb"></span> files to current directory. Please confirm.</td>
    </tr>
    <tr>
        <td align='right'><input type='submit' class='fileAction' value='Copy' /><input type='button' class='fileAction' value='Cancel' OnClick='return discardCurrentDialog();'/></td>
    </tr>
</table>
</form>
