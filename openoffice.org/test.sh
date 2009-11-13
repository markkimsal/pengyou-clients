#!/bin/bash
PATH="/opt/openoffice.org2.0/program":$PATH
killall soffice.bin
cd "PengYou.uno"
rm ../PengYou.uno.zip
zip -r ../PengYou.uno.zip Addons.xcu ProtocolHandler.xcu META-INF images LICENSE.txt ProtocolHandlerAddon_java.uno.jar *.jar
cd ..
unopkg remove PengYou.uno.zip
unopkg add "PengYou.uno.zip"
swriter
