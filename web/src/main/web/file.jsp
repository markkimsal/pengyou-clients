 <li class="liFile"
    ondblclick="apply<%= fileWrapper.isCollection()?"Directory":"File"%>('<%=path+"/"+fileWrapper.getDisplayName()%>')"
    onmousedown="toClipBoard(this);" <%if (ClipBoard.containsKey(path+"/"+fileWrapper.getDisplayName())){%>style='background:#02007F;'<%};%>>
    <input type="hidden" value='<%=path+"/"+fileWrapper.getDisplayName()%>' class="fullUrl" />
    <input type="hidden" value='<%=fileWrapper.getDisplayNameList()%>' class="displayNameList" />
     <input type="hidden" value='<%=fileWrapper.isCollection()%>' class="directory" />
     <input type="hidden" value='<%=fileWrapper.getIconSmall()%>' class="smallIcon" />     
    <div class="thumbFile">
        <img src="<%=fileWrapper.getIcon()%>" alt="<%=fileWrapper.getDisplayName()%>" title="<%=fileWrapper.getDisplayName()%>"/><br/>
        <%=fileWrapper.getDisplayNameThumb()%>
    </div>

    <div class="fileDetails">
        <ul class="detailsUl" ondblclick="apply<%= fileWrapper.isCollection()?"Directory":"File"%>('<%=path+"/"+fileWrapper.getDisplayName()%>')">
            <li class="fileName"><img src="<%=fileWrapper.getIcon()%>" alt="<%=fileWrapper.getDisplayName()%>" title="<%=fileWrapper.getDisplayName()%>" style="height:16px;"/>&nbsp;<%=fileWrapper.getDisplayNameList()%></li>
            <li class="fileSize"><%=fileWrapper.getContentLength()%></li>
            <li class="lastModif"><%=fileWrapper.getLastModifiedDate()%></li>
            <li class="creatDate"><%=fileWrapper.getCreationDate()%></li>
        </ul>
    </div>
 </li>

