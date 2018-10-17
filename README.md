# EvolutionaryRNN
Using genetic algorithms and recurrent neural networks to evolve a sensory based agent in the task of navigating an unknown environment.
---------------

Overview
---------------
The agent is set in an unknown environment and given a destination. To get to the destination the agent must learn to navigate the environment purely based on its senses.
These senses include:
- x, y scalar distance between the agent's current position and the destination
- 4 values set to 1 or 0 whether there is an adjacent block or not
- 2 inputs set to the last move taken in both x and y (optional)

These 6 (or 8) inputs are then used as an input to a **Recurrent Neural Network**. This network has 4 distinct outputs actions being the cardinal directions the block should move per turn. This model is then trained using a **genetic algorithm** to find the best possible solution.

Fitness function
---------------
The fitness function score is calculated by:
`SCORE = (MAX_TURNS - DISTANCE) * 0.95^MOVES_TAKEN`

Where:
- DISTANCE = Manhattan distance between the agent's position and destination at the end of an episode
- MAX_TURNS = Maximum amount of turns per episode
- MOVES_TAKEN = Moves taken to get to the goal (Used to promote more direct solutions)

Running the project
---------------
To run the project use:
```
$ javac Main.java
$ java Main
```

Keyboard inputs:
- X - End training early and display best agent
- N - (hold) Display neural network of best agent
- P - (hold) Display current best agent
- SPACE - Save best agent's weights to out.txt once training is finished

Some hyper parameters in main.java and their descriptions:
```java
int[] nShape      //Shape of neural network per-layer
int GENERATIONS   //Amount of generations to simulate
int TURNS         //Maximum turns per episode
int SAMPLES       //Amount of agents to simulate per generation
float MUTATION    //Rate of mutation of the agents
int MAP_SIZE      //Size of map
String INPUT      //Optional input for weight initialization, leave as "" for random initial values
String MAPNAME    //Name of .png map to be loaded
boolean TRAIN     //Train new generations or just display result
```


Java version: 1.8.0_121

Picture.java and StdDraw.java are used purely as a visual interface and input. 
Source: https://introcs.cs.princeton.edu/java/stdlib/
