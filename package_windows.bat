set INPUT=jar
set OUTPUT=_exe
set JAR=memristor-discovery-2.0.1.jar
set VERSION=2.0.1
set APP_ICON=_exe/icons.ico

call "%JAVA_HOME%\bin\java.exe" ^
    -Xmx512M ^
    --add-modules "java.base,java.datatransfer,java.desktop,java.logging,java.prefs,java.xml,jdk.xml.dom, javafx.controls,javafx.fxml" ^
    --input "%INPUT%" ^
    --output "%OUTPUT%" ^
    --name "Memristor-Discovery" ^
    --main-jar "%JAR%" ^
    --version "%VERSION%" ^
    --jvm-args "--add-opens javafx.base/com.sun.javafx.reflect=ALL-UNNAMED" ^
    --icon "%APP_ICON%" ^
    --copyright "Knowm Inc."
