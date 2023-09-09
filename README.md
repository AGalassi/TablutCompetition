# TablutCompetition
Software for the Tablut Students Competition

## Installation on Ubuntu/Debian 

From console, run these commands to install JDK 8 e ANT:

```
sudo apt update
sudo apt install openjdk-8-jdk -y
sudo apt install ant -y
```

Now, clone the project repository:

```
git clone https://github.com/AGalassi/TablutCompetition.git
```

## Run the Server without Eclipse

The easiest way is to utilize the ANT configuration script from console.
Go into the project folder (the folder with the `build.xml` file):
```
cd TablutCompetition/Tablut
```

Compile the project:

```
ant clean
ant compile
```

The compiled project is in  the `build` folder.
Run the server with:

```
ant server
```

Check the behaviour using the random players in two different console windows:

```
ant randomwhite

ant randomblack
```

At this point, a window with the game state should appear.

To be able to run other classes, change the `build.xml` file and re-compile everything


## Replay function

Replay a game using the logfile

Example:

```
java -jar .\server.jar -g -R .\logs\PLAYER1_vs_PLAYER2_1652711382324_gameLog.txt
```

## Tablut Client with GUI

Follow the instruction in [TablutClientGUI.md](./TablutClientGUI.md).

<div align="center">
<img width="50%" src="https://user-images.githubusercontent.com/56556806/210098077-4e564401-9518-4437-852c-70db378e1990.gif">
<br/>
Demo
</div>