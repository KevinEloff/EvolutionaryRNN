# EvolutionaryRNN
Using genetic algorithms and recurrent neural networks to evolve a sensory based agent in the task of navigating an unknown environment.

The task is to create an agent that is able to navigate an unknown environment purely based on its senses.
These senses include:
- 2 scalar distance between the agent and the goal
- 4 directional senses set to 1 or 0 whether there is an adjacent block

These 6 (or 8) inputs are then put into a Recurrent Neural Network. This network has 4 distinct outputs being the cardinal directions that the block can move each turn.

The fitness function score is calculated by:
SCORE = (EPISODETURNS - Manhatten_Distance) * 0.95^MOVES_TAKEN

Picture.java and StdDraw.java are used purely as a visual interface and input.