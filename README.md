# Melissa project
A bee simulator using Java, JavaFX and JaCaMo, a multiagent system.

The hive, bee simulation and behaviours are developed with JaCaMo framework, that allow programming multiagent systems with Jason language. Each agent has goals, believes and actions. 

To see graphically these simulation, JavaFX was used to print each action and development of the simulation.

## How to run
To actually run, have JDK version 10 (that yet contains JAVAFX outdated version). 

Follow instalation instruction on http://jacamo.sourceforge.net/eclipseplugin/tutorial/ to install Eclipse plugin. Remember set JAVA HOME with jdk10, and check in project properties if are using the jdk10 downloaded.

Open perspective "JaCaMo" -> Import this project -> right button on melissa.jcm -> Run JaCaMo Application.

## Some prints:
Bee simulation running, with each agent report and opened threads and logs.
![Running bee simulation](/img/melissa-running.gif)

Print with more detail.
![Print with running simulations, with report for each agents actions](/img/running-stats.png)

Print with graphic detailed information.
![Detail of graphic information](/img/visual-simulation.png)
