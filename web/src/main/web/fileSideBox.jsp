<div class="sideBox">
  <!--  <div class="Article">
        <h2 id="searchH2" onclick="return collapseExpand('search');" style="height:35px;"><img src="img/expand.png"
                                                                                      alt='expand' id='searchImg'/>
            Search documents</h2>

        <div class="ArticleBody" id="search" style="display:none;">
            <input type="text" class="searchTxt" name="searchTxt"/>
            <input type="image" class="searchImg" name="searchImg" src="./img/actions/find.png"/><br/>
            <br/>
            <input type="radio" id="searchRadio0" class="searchRadio" name="searchRadio" checked="checked"/> <label
                for="searchRadio0">In every file</label>
            <br/>
            <input type="radio" id="searchRadio1" class="searchRadio" name="searchRadio"/> <label for="searchRadio1">
            Down from current directory</label>
            <br/>
        </div>

        <div class="ArticleFooter"></div>
    </div>

    <div class="Article">
        <h2 id="quickFilesH2"  onclick="return collapseExpand('quickFiles');" style="height:35px;"><img
                src="img/expand.png" alt='expand' id='quickFilesImg'/> My quick files</h2>

        <div class="ArticleBody" id="quickFiles" style="display:none;">
            <p><img alt="document" title="Favorite document" src="img/pdf.gif"/><a href="#">Management plan 2006.pdf</a>
            </p>

            <p><img alt="document" title="Favorite document" src="img/word.jpg"/><a href="#">Loreal.doc</a></p>

            <p><img alt="document" title="Favorite document" src="img/folder.png"/><a href="#">FT prospects</a></p>

            <p><img alt="document" title="Favorite document" src="img/pdf.gif"/><a href="#">Market study.pdf</a></p>
        </div>

        <div class="ArticleFooter"></div>
    </div>

    <div class="Article">
        <h2 id="recentFilesH2" onclick="return collapseExpand('recentFiles');" style="height:35px;"><img
                src="img/expand.png" alt='expand' id='recentFilesImg'/> Recently used</h2>

        <div class="ArticleBody" id="recentFiles" style="display:none;">
            <p><img alt="document" title="Favorite document" src="img/pdf.gif"/><a href="#">Management plan 2006.pdf</a>
            </p>

            <p><img alt="document" title="Favorite document" src="img/word.jpg"/><a href="#">Loreal.doc</a></p>

            <p><img alt="document" title="Favorite document" src="img/folder.png"/><a href="#">FT prospects</a></p>

            <p><img alt="document" title="Favorite document" src="img/pdf.gif"/><a href="#">Market study.pdf</a></p>
        </div>

        <div class="ArticleFooter"></div>
    </div>              -->

        <div class="Article">
        <h2 id="clipBoardH2" style="height:20px;" onclick="return collapseExpand('clipBoard');"><img
                src="img/collapse.png" alt='expand' id='clipBoardImg'/> Clipboard <span id="clipBoardFileNb"></span></h2>

        <div class="ArticleBody" id="clipBoard" style="display:block;"></div>

        <div class="ArticleFooter">
            <p style='text-align:right'>
                <img alt='Copy clipboard documents to current folder' title='Copy clipboard documents to current folder' src="img/actions/copy.png" style='width:16px;'  onclick="popDialog('CopyFiles',function(){dialogCopyFiles();});" />
                <img alt='Move clipboard documents to current folder' title='Move clipboard documents to current folder' src="img/actions/cut.png" style='width:16px;'  onclick="popDialog('MoveFiles',function(){dialogMoveFiles();});" />
                <img alt='Move to trash' title='Move to trash' src="img/actions/trash.png" style='width:16px;' onclick="popDialog('DeleteFiles',function(){dialogDeleteFiles();});" />
                <img alt='Compress and download' title='Compress and download' src="img/actions/save.png" style='width:16px;' onclick="popDialog('CompressFiles',function(){dialogCompressFiles();});" />
            </p>
        </div>
    </div>
</div>





