path="C:\Program Files\OpenOffice.org 2.0\program";%path%
taskkill /F /IM soffice.bin
cd "PengYou.uno\"
del ..\PengYou.uno.zip
zip -r ..\PengYou.uno.zip Addons.xcu ProtocolHandler.xcu META-INF images LICENSE.txt ProtocolHandlerAddon_java.uno.jar *.jar
cd ..
unopkg.exe remove PengYou.uno.zip
unopkg.exe add "PengYou.uno.zip"
"C:\Program Files\OpenOffice.org 2.0\program\swriter.exe"
pause
