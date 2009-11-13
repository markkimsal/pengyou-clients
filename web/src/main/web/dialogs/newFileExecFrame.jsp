<html>
    <head>
       <title></title>
  <script type='text/javascript'>
      
      function inject(val) {
        document.body.innerHTML = val;
    }
      function  getData()
      {
          document.getElementById("fileInput").value=window.parent.getFormValue("fileInput","value");       
          document.getElementById("fileDesc").value=window.parent.getFormValue("fileDesc","text");
      }
  </script>
</head>

<body onload="getData();">
<form id="upload" action="upload.jsp" enctype="multipart/form-data" method="post">
<table id='dialogTable'>
    <tr>
     <th colspan='2' class="dialogHeader">New Document</th>
    </tr>
    <tr>
        <td>Select document</td>
        <td>
            <input type='file' id='fileInput' name='fileInput' size='33' value='' />
        </td>
    </tr>
    <tr>
        <td valign='top'>Description</td>
        <td valign='top'><textarea id='fileDesc' name='fileDesc'></textarea></td>
    </tr>
    <tr>
        <td>To remote location</td>
        <td><span id='fileLocation'>/.svn</span><input type='hidden' id="inputLocation" name='location' value='/.svn' /></td>
    </tr>
    <tr>
        <td colspan='2' align='right'><input type='submit' class='fileAction' value='Add document' /><input type='button' class='fileAction' value='Cancel' OnClick='discardCurrentDialog(); return false;'/></td>
    </tr>
</table>
</form>

</body>

</html>