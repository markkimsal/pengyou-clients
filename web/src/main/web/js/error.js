/*
* Hash table defining errors
*/
var ErrorList = new Array();
ErrorList["dumpDirectoryContent"]="'Error retrieving directory content<br />The server returned: '+ requestStack[ret][1].status + ': '+ requestStack[ret][1].statusText+ '<br />Connection string may be invalid.<br />Please verify the Server parameters in config.xml'";
ErrorList["RefreshFileList"]="'Error refreshing current directory<br />The server returned: '+ requestStack[ret][1].status + ': '+ requestStack[ret][1].statusText";
ErrorList["dialogRetrieve"]="'An error occured while retrieving the dialog identified by '+dialogName+'<br />The server returned: '+ requestStack[ret][1].status + ': '+ requestStack[ret][1].statusText";
ErrorList["collapseExpand"]="'The server could not be reached for selected box storage<br />The server returned: '+ requestStack[ret][1].status + ': '+ requestStack[ret][1].statusText";

/*
* Default error manager for XMLHttpRequest with no callbacks
*/
function    defaultErrorManager(ret, errorString)
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
              displayError(eval(ErrorList[errorString]));
          }
      }
}

/* Empties the cache system from any displayed dialog and
injects and error dialog with a custom Error message */
function displayError(errorString){
    discardCurrentDialog();
    popDialog("Error",function(){dialogError(errorString);});
    return false;
}
