<form action="login.jsp?login=1" method="post">
<table id='dialogTable'>
    <tr>
     <th colspan='2' class="dialogHeader">Authenticate</th>
    </tr>
    <tr>
        <td> User name</td>
        <td>

            <input type='text' id='userInput' class='authInput' name='user' size='33' value='you may use any username you want' />
        </td>
    </tr>
    <tr>
        <td valign='top'> Password</td>
        <td valign='top'><input type='password' class='authInput' id='pwdInput' name='pwd' size='33' /></td>
    </tr>
    <tr>

        <td colspan='2' align='right'><input type='submit' class='fileAction' value='Log in' /><input type='button' class='fileAction' value='Cancel' OnClick='discardCurrentDialog(); return false;'/></td>
    </tr>
</table>
</form>
<!--

<html><head><title></title>
               <link rel="stylesheet" type="text/css" href="css/dialog.css"/>               
           </head>
           <body>
           <form action="login.jsp?login=1" method="post">
           <table>
               <tr>
                   <td> User name</td>
                   <td>
                       <input type='text' id='userInput' class='authInput' name='user' size=33 />
                   </td>
               </tr>
               <tr>
                   <td valign='top'> Password</td>
                   <td valign='top'><input type='password' class='authInput' id='pwdInput' name='pwd' size=33 /></td>
               </tr>
               <tr>
                   <td colspan='2' align='right'><input type='submit' class='fileAction' value='Log in' /><input type='button' class='fileAction' value='Cancel' OnClick='discardCurrentDialog(); return false;'/></td>

               </tr>
           </table>
           </form>
           </body>
           </html>

        <td> User name</td>
        <td>
            <input type='text' id='userInput' class='authInput' name='user' size=33 />
        </td>
    </tr>
    <tr>
        <td valign='top'> Password</td>
        <td valign='top'><input type='password' class='authInput' id='pwdInput' name='pwd' size=33 /></td>
    </tr>
    <tr>
        <td colspan='2' align='right'><input type='submit' class='fileAction' value='Log in' /><input type='button' class='fileAction' value='Cancel' OnClick='discardCurrentDialog(); return false;'/></td>
    </tr>
</table>
</form>
    -->