/* Item selection colors. Is applied to list items in any kind of
resource lists (files / users) and on any kind of list (detailed / simple) */
var selectionColor = '#02007F';
var idleSelectionColor = '#7F7F7F';
/* Stores the name of the currently displayed dialog */
var dialogName="";
/* Temporarily stores the inner content of the dialog container (most of the time,
 this content is the wait rotating loading please wait image */
var dialogInnerHTML='';

/* List of the values of the 'id' property of the side boxes. Specified for an easier access */
var sideBoxes = new Array('search', 'quickFiles', 'recentFiles', 'clipBoard');

/* The request stack object, stores XMLHttpRequest objects indexed to the urls */
var requestStack=new Array();

/* to use if you want to call a dialog with GET parameters
value example :   ?variableValue=value&discardMode=keepcache */
var urlQuery="";

/* Stores the value of the current path */
var dirPath;


/* When activated, a call to debug function will print
its parameters in an iframe. useful to check values and progressions */
var debugMode=1;


function    page(getParam)
{

    document.location="index.jsp?p="+getParam;
    return false;
}

/* switches the resources listing method from simple to detailed and from detailed to simple
id parameter: DOM id attribute of the object
class parameter: new class attribute value for the object identified by id */
function switchClass(id, className) {
    document.getElementById(id).className = className;
    if (className=="listTab")
    {
        document.getElementById("headerList").style.display="block";
    }
    else
        document.getElementById("headerList").style.display="none";
    return false;
}

/* Selects all items of current location of listed resource */
function    selectAll()
{
    var list=document.getElementById('ulList').childNodes;
    for(var i=0; i < list.length; i++){
        if(list[i].className=="liFile" )
              if (list[i].onmousedown && isInClipBoard(getFileProperty(list[i], 'fullUrl')) == false)
                 toClipBoard(list[i]);
        }

    return false;
}
/* Inverts selection of items of current location of listed resource */
function invertSelection(){
var list=document.getElementById('ulList').childNodes;
for(var i=0; i < list.length; i++){
    if(list[i].className=="liFile" ){
      {
          if (list[i].onmousedown)
            toClipBoard(list[i]);
      }
    }
}
return false;
}

/* Effectively executes XMLHttp requests of a request object of the stack
 ret: int
 callback: function to call on successful request
 synchrone: mode synchrone (true/false) */
function XMLHTTPRequest(ret, callback, synchrone)
{
    requestStack[ret][1].open("GET", requestStack[ret][0], synchrone);
    requestStack[ret][1].onreadystatechange = callback;
    try{
    requestStack[ret][1].send(null);
    }catch (Exception)
    {
       alert("Exception caught:"+Exception);
    }
 }
/* Cleans the request stack of a terminated request */
function    removeRequestStack(ret)
{requestStack[ret]='';}

/* Adds an url to the request stack by injecting a 'personal' XMLHtpRequest object.
Return the index in stack of the request object created for url
url parameter type is a string of any kind of url*/
function    addRequestStack(url){
    /* First look for an idle (reusable) entry in the stack */
    var ret = requestStack.length;
    debug('[request stack] length :'+ ret + ' url :'+ url);
    for (var i = 0; i <= ret; i++)
        if (requestStack[i]=='')
            break;
    if (i < ret)
    ret=i;
    /* Created space for the new request and inject the URL and the XMLHttpRequest object */
    requestStack[ret]=new Array(2);
    requestStack[ret][0]=url;
    if (window.ActiveXObject)
    {
        requestStack[ret][1] = new ActiveXObject("Microsoft.XMLHTTP");
    }
    else if (window.XMLHttpRequest)
    {
        requestStack[ret][1] = new XMLHttpRequest();
    }
    return ret;
}

/* Collapses clicked sideBox and expand others
id parameter: DOM id attribute of the HTML entity
*/
function collapseExpand(id)
{
    debug("collapseExpand "+ id);
    if (expandedBox != ''){
        document.getElementById(expandedBox).style.display = 'none';
        document.getElementById(expandedBox + 'Img').src = 'img/expand.png';
        document.getElementById(expandedBox + 'H2').style.height = '35px';
        var url = "session.jsp?target=SideBox&action=collapse&box="+expandedBox;
        expandedBox = '';
    }
    if (expandedBox != id) {
        document.getElementById(id).style.display = 'block';
        document.getElementById(id + 'Img').src = 'img/collapse.png';
        document.getElementById(id + 'H2').style.height = '20px';
        var url = "session.jsp?target=SideBox&action=expand&box="+id;
        expandedBox=id;
    }
    /* Store the box id in the session.
    This way, on page reload, the boxes will keep their state*/
    var ret=addRequestStack(url);
    XMLHTTPRequest(ret, function (){defaultErrorManager(ret, "collapseExpand");}, false);
    return false;
}

/* Directory browsing: called on folder double click - build url and change location
path parameter example: /directory/subdir
*/
function applyDirectory(path)
{
    document.getElementById('Cache').style.display='block';
    url="./fileList.jsp?path="+path;
    dirPath=path;
    var ret=addRequestStack(url);
    XMLHTTPRequest(ret, function(){dumpDirectoryContent(ret);}, true);
    return false;
}

/* XMLHttpRequest response manager for directory content injection
ret parameter type: int*/
function dumpDirectoryContent(ret){
    if (requestStack[ret][1].readyState == 4)
    {
        // If dialog retrieved successfuly
        try{
            if(requestStack[ret][1].status == 200 || requestStack[ret][1].status == 0)
            {
                httpResponseText = requestStack[ret][1].responseText;
                document.getElementById('ulList').innerHTML=listHeaders+httpResponseText;
                document.getElementById('Cache').style.display='none';
                updatePathRail(dirPath);
                debug("dumpDirectory request got response stackPos="+ret);
                removeRequestStack(ret);
            }
            else
            {
                displayError(eval(ErrorList["dumpDirectoryContent"]));
            }
        }catch (error){
            displayError(eval(ErrorList["dumpDirectoryContent"]));            
        }
    }
}

/* Update path rail to current location
path paramater example: /directory/subdir
*/
function    updatePathRail(path)
{
    /* Retrieve the value from the hidden input */
    document.getElementById("fullPath").value=path;
    var railUl=document.getElementById("pathRail");
    /* First locate the next element to root (which can't be deleted)
    and store its position in the DOM hierarchy*/
    var canDelete=false;
    var indexRailRoot=0;
    for (j = 0; j < railUl.childNodes.length; j++){
      if (canDelete==false && railUl.childNodes[j].id=="railRoot"){
        canDelete=true;
        indexRailRoot=j;
      }
    }
    /* remove all the path rail elements except 'root' */
    if (canDelete==true)
        for (j=railUl.childNodes.length-1; j > indexRailRoot;j--)
            railUl.removeChild(railUl.childNodes[j]);

    /* build the new pathrail using the path parameter
    and inject the DOM object in the page hierarchy */
    var pathList = path.split("/");
    var railBuild = "";

    for (var i = 1; i < pathList.length; i++) {
      railBuild += "/" + pathList[i];
      var liSeparator=document.createElement('li');
      liSeparator.innerHTML = '&gt;';
      var liElem=document.createElement('li');
      var aElem=document.createElement('a');
      aElem.setAttribute('href', 'index.jsp?p=files&path='+railBuild);
      aElem.setAttribute('onclick','return applyDirectory("'+railBuild+'");');
      aElem.innerHTML=pathList[i];
      liElem.appendChild(aElem);
        if (pathList[i]!="")
      railUl.appendChild(liSeparator);
      railUl.appendChild(liElem);
    }
}


/*
* Launch the file info dialog to display a the document ID for the file specified by path
*/
function applyFile(path)
{
    urlQuery = '?target=' + path;
    popDialog("FileInfo",dialogFileInfo());
}

/*
* Launch file download in the execution iframe for file specified by paths
*/
function downloadFile(path)
{
    url = "download.jsp?fileName="+path;
    document.getElementById("ExecFrame").src=url;
}

/*
*  explicit enough
*/
function    initFileExplorer()
{
    debug('loading');
    loadClipBoard();
    if (expandedBox !='')
        collapseExpand(expandedBox);
}


/*function    changeBackground(clr)
{
    document.getElementById("main").style.background=clr;
    document.getElementById("buttons").style.background=clr;
} */

/*HowTo section function*/
function	dropDown(elem){
    		elem.nextSibling.style.display='block';

    		var next=elem.nextSibling.nextSibling;
    		while(next){
    		 if(next.className=="howToA")
    		 	next.style.display='none';
    		 next = next.nextSibling;
}
				var prev=elem.previousSibling.previousSibling;
    		while(prev){
    		 if(prev.className=="howToA")
    		 	prev.style.display='none';
    		 prev = prev.previousSibling;
    		}
    	}


function    debug(toPrint)
{
                return false;
    if (!debugMode){
    document.getElementById('dev').style.display='none';
    return false;
        }
    objDiv=document.getElementById("debugDiv");
    objDiv.innerHTML+='<br />'+toPrint;
    objDiv.scrollTop = objDiv.scrollHeight;
}

var error="";
function    popError(err)
{
    error=err;
}