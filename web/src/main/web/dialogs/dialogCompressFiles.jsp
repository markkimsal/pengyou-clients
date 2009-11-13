<form method="post" onsubmit="return compressFiles();">
<table id='dialogTable'>
    <tr>
     <th class="dialogHeader">Compress clipboard files</th>
    </tr>
    <tr>
        <td>
            You are about to compress <span id="compressFilesNb"></span> files in an archive. Please confirm.
        </td>
    </tr>
    <tr>
        <td align='right'><input type='submit' class='fileAction' value='Compress' /><input type='button' class='fileAction' value='Cancel' OnClick='return discardCurrentDialog();'/></td>
    </tr>
</table>
</form>
