<form action="addUser.jsp" method="post" onsubmit="return AddUser();discardCurrentDialog();displayCache();">
<table id='dialogTable'>
    <tr>
     <th class="dialogHeader" colspan="2">Add new user</th>
    </tr>
    <tr>
        <td>Firstname</td>
        <td><input type="text" id="firstname" name="firstname" value=""/></td>
    </tr>
    <tr>
        <td>Lastname</td>
        <td><input type="text" id="lastname" name="lastname" value=""/></td>
    </tr>
    <tr>
        <td>User name</td>
        <td><input type="text" id="username" name="username" value=""/></td>
    </tr>
    <tr>
        <td>Password</td>
        <td><input type="password" id="password" name="password" value=""/></td>
    </tr>
    <tr>
        <td>Password again</td>
        <td><input type="password" id="password1" name="password1" value=""/></td>
    </tr>
    <tr>
        <td>Email</td>
        <td><input type="text" id="email" name="email" values=""/></td>
    </tr>
    <tr>
        <td align='right' colspan="2"><input type='submit' class='fileAction' value='Add user' /><input type='button' class='fileAction' value='Cancel' OnClick='return discardCurrentDialog();'/></td>
    </tr>
</table>
</form>
