# Tablut Client GUI

### Demo

https://user-images.githubusercontent.com/56556806/210091347-19bc307f-f2e9-4d9a-8fd7-7b4a1dc597c0.mp4

### Prerequisites
- JDK/JRE => 8 with JavaFX integrated

###### Download JRE
NB: openjdk doesn't include JavaFX, therefore the application
won't start if launched with `java` command from that jdk.
Some valid alternatives are:
- ZuluFX (download from [here](https://www.azul.com/downloads/?version=java-8-lts&architecture=x86-64-bit&package=jre-fx));
- LibericaFX (download from [here](https://bell-sw.com/pages/downloads/#/java-8-lts));
- Oracle (download from [here](https://www.oracle.com/it/java/technologies/javase/javase8-archive-downloads.html)).

###### Download Standalone Application
Alternatively, you can download the standalone application from [here](https://github.com/mikyll/TablutCompetition/releases/tag/v1.1):
- [Windows x64](https://github.com/mikyll/TablutCompetition/releases/download/v1.1/TablutClientGUI-1.1-Windows_x64.zip)
- [Linux x64](https://github.com/mikyll/TablutCompetition/releases/download/v1.1/TablutClientGUI-1.1-Linux_x64.tar.gz)

### Run
1. Launch the Server;
2. Launch TablutClientGUI;
    ```
    /path/to/jre/bin/java -jar ./TablutClientGUI.jar 
    ```
3. Launch a second player (could be a second TablutClientGUI);
4. Play Tablut!
