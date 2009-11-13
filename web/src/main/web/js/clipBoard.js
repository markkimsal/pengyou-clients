/* 1 if clipBoardDetails is activated 0 otherwise */
var clipBoardDetails=0;
/* Maximum number of references to be displayed in the ClipBoard */
var clipBoardMaxSize = 6;


/*
* Looks for filename in clipBoard list
* returns true if found
* false otherwise
*/
function    isInClipBoard(filename)
{
    var print = 'looking for ' + filename + " in ";
    for (var j = 0; j<selectedItems.length;j++)
    print+="["+selectedItems[j]+']';
   debug(print);
    for (var i = 0; i < selectedItems.length;i++)
     if (selectedItems[i][0]==filename)
     return true;
    return false;
}

/*
* Creates a node to add an item to the sidebox specified by forSideBox parameter
* the returned element includes an filetype icon, the filename, and the fullpath
* the object is encapsulated in a <p> element
*/
function createClipBoxNode(fullPath, icon, fileName, forSideBox) {
    debug('create clipboard node: '+ fullPath + '____'+fileName);
    /* initialize variables HTML object composing final sidebox element */
    var pElem = document.createElement('p');
    var iElem = document.createElement('input');
    var aElem = document.createElement('a');
    var aDelElem = document.createElement('a');
    var imgElem = document.createElement('img');
    var delElem = document.createElement('img');
    /* set several attributes for created elements */
    iElem.setAttribute('type', 'hidden');
    iElem.setAttribute('value', fullPath);
    aElem.setAttribute('href', '#');
    aElem.innerHTML = fileName.length>= 45 ? fileName.substr(0, 40)+'...':fileName;
    imgElem.setAttribute('src', icon);
    imgElem.setAttribute('alt', '-');
    imgElem.setAttribute('title', fullPath);
    delElem.setAttribute('src', 'img/delete.gif');
    delElem.setAttribute('alt', 'Remove from list');
    delElem.setAttribute('title', 'Remove from list');
    delElem.setAttribute("class", "delIcon");
    aDelElem.setAttribute("href","RemoveFromList")
    aDelElem.appendChild(delElem);
    pElem.appendChild(iElem);
    pElem.appendChild(imgElem);
    pElem.appendChild(aElem);
    pElem.appendChild(aDelElem);
    pElem.style.display = 'block';
    pElem.setAttribute("class","sideBoxP");
    aDelElem.setAttribute("onclick","return clickRemoveFrom"+forSideBox+"(new Array('"+fullPath+"',this.parentNode));");
    return pElem;
}

/*
* retrieve fileName attributes from DOM in directory listing
*/
function getFileProperty(object, type) {
    var childs = object.childNodes;
    for (var i = 0; i < childs.length; i++)
        if (childs[i].className == type)
            return(childs[i].getAttribute("value"));
    return "";
}

/*
* Finds the specified file in the file listing
*/
function    findFileProperty(fullPath, property)
{
    var list=document.getElementById('ulList').childNodes;
    for(var i=0; i < list.length; i++)
        if(list[i].className=="liFile" && list[i].onmousedown && getFileProperty(list[i], "fullUrl") == fullPath)
           {
               return getFileProperty(list[i], property);
           }
    return false;
}

function    hideClipBoardFiles()
{
    clipBoardDetails=0;
    if (document.getElementById('clipBoardDetails'))
        document.getElementById('main').removeChild(document.getElementById('clipBoardDetails'));
    return false;
}

function    showClipBoardFiles()
{
    hideClipBoardFiles();
    clipBoardDetails=1;
    divElem=document.createElement('div');
    divElem.setAttribute('id',"clipBoardDetails");
    aElem=document.createElement("a");
    aElem.setAttribute("href",'Close');
    aElem.setAttribute("onclick","return hideClipBoardFiles();");
    aElem.innerHTML='Close';
    divElem.appendChild(aElem);
    if (selectedItems.length)
        for (var i = 0; i < selectedItems.length; i++)
            divElem.appendChild(createClipBoxNode(selectedItems[i][0],selectedItems[i][1],
                                selectedItems[i][0].substr(selectedItems[i][0].lastIndexOf('/') + 1,
                                selectedItems[i][0].length),"ClipBoard"));
    else
    {
        pElem=document.createElement('p');
        pElem.innerHTML="Clipboard is empty";
        pElem.setAttribute("class","boxEmpty");
        divElem.appendChild(pElem);
    }
    document.getElementById('main').appendChild(divElem);
    return false;
}

// On page load: Dump selected items to the clipboard box using the JS array
function loadClipBoard() {
    debug('loading clipboard');
    document.getElementById('clipBoard').innerHTML="";
    var clipBox = document.getElementById('clipBoard').childNodes;
    var dotElem = document.createElement('p');
    dotElem.setAttribute('id', 'clipBoardShorten')
    dotElem.innerHTML = '<a href="ViewClipBoard" title="View ClipBoard" onclick="return showClipBoardFiles();"><span>...</span></a>';
                     
    if (selectedItems.length)
    {
        for (i = 0; i < selectedItems.length; i++) {
            fullPath = selectedItems[i][0];
            icon = selectedItems[i][1];
            pElem = createClipBoxNode(fullPath, icon, fullPath.substr(fullPath.lastIndexOf('/') + 1, fullPath.length),"ClipBoard");
            if (clipBox.length >= clipBoardMaxSize)
                pElem.style.display = 'none';
            document.getElementById('clipBoard').appendChild(pElem);
        }
        if (selectedItems.length >= clipBoardMaxSize)
            dotElem.setAttribute('style', 'display:block');
        else
            dotElem.setAttribute('style', 'display:none');
        document.getElementById('clipBoard').appendChild(dotElem);
    }
    else
    {
        var pElem = document.createElement('p');
        pElem.setAttribute("class","boxEmpty");
        pElem.innerHTML = "Clipboard is empty";
        document.getElementById('clipBoard').appendChild(pElem);
    }
    if (clipBoardDetails && selectedItems.length){
        showClipBoardFiles();
    }
    document.getElementById("clipBoardFileNb").innerHTML = selectedItems.length?'(' + selectedItems.length + ')':'';
    debug('clipBoard loaded');
}


function clickRemoveFromClipBoard(argList){
    var fullPath=argList[0];
    var filePath=fullPath.substring(0,fullPath.lastIndexOf('/'));
    var listPath=document.getElementById('fullPath').value;
    var i,j;
    filePath=filePath==""?"/":filePath;
    listPath=listPath==""?"/":listPath;

    if (filePath == listPath){
        var o=document.getElementById("ulList").childNodes;
        var object="";
        for (i = 0; i < o.length; i++){
            f=o[i].childNodes;
            object=o[i];
           for (j = 0; j < f.length;j++)
            if(f[j].className=='fullUrl' && f[j].value && f[j].value==fullPath){
                toClipBoard(object);
                break;
           }
        }
    }
    else{
        var url = 'session.jsp?target=ClipBoard&action=unselect&file=' + fullPath;
        for (i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i][0] == fullPath) {
                selectedItems.splice(i, 1);
                break;
            }
        }
        loadClipBoard();
        var ret = addRequestStack(url);
        XMLHTTPRequest(ret,function(){}, false);
        document.getElementById("clipBoardFileNb").innerHTML = selectedItems.length?'(' + selectedItems.length + ')':'';
    }
    if (clipBoardDetails)showClipBoardFiles();
    return false;
}

function    removeNodeFromClipBoard(fullPath)
{
    var clipBox = document.getElementById('clipBoard').childNodes;
    // If clipBoard list is big enough for every selected file, display all and hide '...' item

    if (selectedItems.length < clipBoardMaxSize) {
        for (i = 0; i < clipBox.length; i++)
            clipBox[i].style.display = 'block';
        document.getElementById("clipBoardShorten").style.display = 'none';
    }

    for (i = 0; i < clipBox.length; i++) {
        found = 0;
        // Explore each child node to access full path of the file
        for (j = 0; j < clipBox[i].childNodes.length; j++)
            if (fullPath == clipBox[i].childNodes[j].getAttribute("value")) {
                found = 1;
                // Node found and was displayed: hide it and look for a hidden node to display
                if (clipBox[i].style.display != 'none')
                    for (t = 0; t < clipBox.length - 2; t++) {
                        if (clipBox[t].style.display == 'block' && t > 1 && clipBox[t - 1].style.display == 'none') {
                            clipBox[t - 1].style.display = 'block';
                            break;
                        }
                    }
                break;
            }
            // Unclicked file found in the clipBoard nodes: remove the node
        if (found) document.getElementById('clipBoard').removeChild(clipBox[i]);
    }
}


// Add/Remove a ClipBoard file

function toClipBoard(object)
{
    var i;
    var color;
    //shortcuts
    var childs = object.childNodes;
    var fullPath = getFileProperty(object, 'fullUrl');
    var icon = getFileProperty(object, 'smallIcon');
    var clipBox = document.getElementById('clipBoard').childNodes;
    debug('adding/removing toclipboard:' + fullPath);
    //file is already selected?
    color = selectionColor;
    for (i = 0; i < selectedItems.length; i++) {
        if (selectedItems[i][0] == fullPath) {
            color = idleSelectionColor;
            selectedItems.splice(i, 1);
            var url = 'session.jsp?target=ClipBoard&action=unselect&file=' + fullPath;
            break;
        }
    }
    if (color == selectionColor){
        selectedItems[selectedItems.length] = new Array(fullPath,icon);
        var url = 'session.jsp?target=ClipBoard&action=select&file=' + fullPath+ "&icon="+icon;
    }               
    loadClipBoard();

    // Store select/unselect in the session
    var ret = addRequestStack(url);
    XMLHTTPRequest(ret,function(){}, false);

    // Visual select: change background color of HTML objects using DOM hierarchy
    object.style.background = color;
    for (i = 0; i < childs.length; i++)

        if (childs[i].nodeType == 1 &&
            (childs[i].className == "thumbFile" || childs[i].className == "fileDetails")) {
            childs[i].style.background = color;
            if (childs[i].className=="fileDetails"){
                var detailList = childs[i].childNodes;
                for(var inc = 0; inc < detailList.length;inc++)
                    if (detailList[inc].className=="detailsUl")
                        for(var ul = 0; ul < detailList[inc].childNodes.length;ul++)
                        {
                            if (detailList[inc].childNodes[ul].tagName=="LI")
                                detailList[inc].childNodes[ul].style.background=color;
                }
            }
        }
        // Update the header value of the clipboard box
    document.getElementById("clipBoardFileNb").innerHTML = selectedItems.length?'(' + selectedItems.length + ')':'';
}
  
/***************************
 END ClipBoard functions   *
 ***************************/

