#!/bin/bash
cd /opt/auretim
JAVA_HOME=/opt/auretim/jre \
PATH=$JAVA_HOME/bin:$PATH \
java -Djavafx.platform=monocle \
-Dmonocle.screen.fb=/dev/fb1 \
-Dmonocle.platform=Linux \
-Dmonocle.input.0/0/0/0.flipXY=false \
-Dmonocle.input.0/0/0/0.maxX=3900  \
-Dmonocle.input.0/0/0/0.minX=210 \
-Dmonocle.input.0/0/0/0.minY=230 \
-Dmonocle.input.0/0/0/0.maxY=3950 \
-Dcom.sun.javafx.isEmbedded=true \
-Dcom.sun.javafx.touch=true \
-Dcom.sun.javafx.virtualKeyboard=javafx \
-jar AuReTim-1.3-SNAPSHOT.jar
if [ $? = 1 ]
then
# echo "poweroff"
 poweroff
fi