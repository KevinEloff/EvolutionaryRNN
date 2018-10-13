# EvolutionaryRNN
Using genetic algorithms and recurrent neural networks to evolve a sensory based agent in the task of navigating an unknown environment.

The agent is set in an unknown environment and given a destination. To get to the destination the agent must learn to navigate the environment purely based on its senses.
These senses include:
- x, y scalar distance between the agent's current position and the destination
- 4 values set to 1 or 0 whether there is an adjacent block or not
- 2 inputs set to the last move taken in both x and y (optional)

These 6 (or 8) inputs are then used as an input to a Recurrent Neural Network. This network has 4 distinct outputs actions being the cardinal directions the block should move per turn. This model is then trained using a genetic algorithm to select the best possible solution.

The fitness function score is calculated by:
- SCORE = (MAX_TURNS - DISTANCE) * 0.95^MOVES_TAKEN
Where:
- DISTANCE = Manhattan distance between the agent's position and destination at the end of an episode
- MAX_TURNS = Maximum amount of turns per episode
- MOVES_TAKEN = Moves taken to get to the goal (Used to promote more direct solutions)

Picture.java and StdDraw.java are used purely as a visual interface and input. Source: https://introcs.cs.princeton.edu/java/stdlib/