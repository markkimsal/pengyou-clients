/* displays cache while loading elements */
function    displayCache()
{document.getElementById('Cache').style.display='block';}

/* hides cache after loading elements */
function    hideCache()
{document.getElementById('Cache').style.display='none';}

/******************************
* XMLHttpRequest initializers
*******************************/
function    createFolder(folderName)
{
    url = "session.jsp?target=FileList&action=createFolder&name="+folderName+"&path="+(document.getElementById('fullPath').value==''?'/':document.getElementById('fullPath').value);
    var ret = addRequestStack(url);
    XMLHTTPRequest(ret,function() {refreshFileList(ret);}, true);
    return false;
}

function    moveFiles()
{
    url="session.jsp?target=ClipBoard&action=move&path="+document.getElementById('fullPath').value;
   var ret = addRequestStack(url);
    XMLHTTPRequest(ret,function() {selectedItems = new Array();loadClipBoard();refreshFileList(ret);}, true);
    return false;
}

function    copyFiles()
{
    url="session.jsp?target=ClipBoard&action=copy&path="+document.getElementById('fullPath').value;
    var ret = addRequestStack(url);
    XMLHTTPRequest(ret,function() {selectedItems = new Array();loadClipBoard();refreshFileList(ret);hideCache();}, true);
    return false;
}

function    compressFiles()
{
    url="session.jsp?target=ClipBoard&action=compress";
    document.getElementById("ExecFrame").src=url;
    discardCurrentDialog();
    return false;
}

function    removeFiles()
{
    url="session.jsp?target=ClipBoard&action=remove&path="+document.getElementById('fullPath').value;
    var ret = addRequestStack(url);
    XMLHTTPRequest(ret,function() {selectedItems = new Array();loadClipBoard();refreshFileList(ret);}, true);
    return false;
}

/******************************
* END XMLHttpRequest initializers
*******************************/

function    reloadFiles()
{
    var path = document.getElementById("fullPath").value;
    applyDirectory(path);
    return false;
}

function    refreshFileList(ret)
{
    if (requestStack[ret][1].readyState == 4)
      {
          // If dialog retrieved successfuly
          if(requestStack[ret][1].status == 200)
          {
              discardCurrentDialog();
              reloadFiles();
              removeRequestStack(ret);
          }
          else
          {
              displayError(eval(ErrorList["RefreshFileList"]));
          }
      }
}
 /***************************
 * Dialog functions
 ***************************/
var dialogCache=new Array();
// Launch dialog code extraction from server
function    popDialog(name, callback)
{
        dialogName="dialog"+name;

        var url = "./dialogs/dialog"+name+".jsp"+urlQuery;
        displayCache();
        var ret=addRequestStack(url);
        urlQuery="";
        XMLHTTPRequest(ret,function() {insertDialogNode(ret,callback);}, true);
    return false;
}

/************************************************
* XMLHttpRequest callbacks for dialogs
* If any action wants to be taken on
* a freshly retrieved dialog, put the code here
**************************************************/
function    dialogLogin()
{
    document.getElementById('userInput').focus();
/*    document.getElementById('userInput').value='exoadmin';
    document.getElementById('pwdInput').value='exo@ecm';*/
}

function    dialogAddUser()
{
    
}
function    AddUser()
{
    if (document.getElementById('password').value != document.getElementById('password1').value){
        alert('passwords dont match');
        return false;
    }
}

function    dialogNewFolder()
{
}

function    dialogLogout()
{
}

function    dialogFileInfo()
{
}

function    dialogError(errorString)
{
  document.getElementById("errorContainer").innerHTML=errorString;
}

function    dialogNewFile()
{
    document.getElementById('fileLocation').innerHTML=document.getElementById('fullPath').value==""?"/":document.getElementById('fullPath').value;
    document.getElementById('inputLocation').value=document.getElementById('fullPath').value;
    document.getElementById('fileDesc').focus();
}


function    dialogDeleteFiles()
{
document.getElementById('deleteFilesNb').innerHTML = selectedItems.length;
}

function    dialogMoveFiles()
{
document.getElementById('moveFilesNb').innerHTML = selectedItems.length;
}

function    dialogCopyFiles()
{
document.getElementById('copyFilesNb').innerHTML = selectedItems.length;
}

function    dialogCompressFiles()
{
document.getElementById('compressFilesNb').innerHTML = selectedItems.length;
}
/******************************************
* END XMLHttpRequest callbacks for dialogs
*******************************************/


/*
* Dialog HTTPRequest callback:
* welcomes the XMLHttpRequest response for dialogs
* injects dialog in DOM hierarchy
* activates Cache
* invokes callback for more specific actions
*/
function insertDialogNode(ret,callback)
{
       if (requestStack[ret][1].readyState == 4)
        {
            // If dialog retrieved successfuly
            if(requestStack[ret][1].status == 200)
            {
                httpResponseText = requestStack[ret][1].responseText;
                dialogInnerHTML=document.getElementById('dialogSpace').innerHTML;
                document.getElementById('dialogSpace').innerHTML=httpResponseText;
                document.getElementById('dialogTr').childNodes[1].setAttribute('id',dialogName);
                if (callback!=null)callback();
                /*if (dialogCache[dialogName]==''){
                 alert('caching dialog');
                dialogCache[dialogName].HTML=httpResponseText;
                dialogCache[dialogName].callback=callback;
                    }
                else {
                    alert('dialog in cache'+dialogCache[dialogName].HTML+'      '+dialogCache[dialogName].callback);
                } */
                removeRequestStack(ret);
            }
            else
            {
                displayError(eval(ErrorList["dialogRetrieve"]));
            }
        }
}

/* Erase the content about dialog box in the DOM hierarchy and discard Cache */
function discardCurrentDialog()
{
    document.getElementById('dialogTr').childNodes[1].setAttribute('id','dialogSpace');
    document.getElementById('dialogSpace').innerHTML=dialogInnerHTML;
    document.getElementById('Cache').style.display='none';
    return true;
}

/***************************
 END Dialog functions   *
 ***************************/
