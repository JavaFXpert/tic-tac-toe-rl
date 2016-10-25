# tic-tac-toe-rl
Uses Java and BURLAP reinforcement learning library to play Tic-Tac-Toe.

With guidance from Dr. James MacGlashan, I've implemented a Tac-Tac-Toe Q-Learning example using BURLAP 3 libraries.  It loosely implements what is described by Sutton and Barto [1] in the "An Extended Example: Tic-Tac-Toe" section.  The application is Apache 2 licensed, and I welcome collaboration on improvements.  Currently, the LearningAgent plays "X" and always moves first.  The "O" player is implemented in the Environment, and has a very simple (and easily beatable) strategy, which is to always select the first empty cell.  The "O" strategy is stationary enough for "X" to learn to beat it rather consistently after 100 or so episodes.

The code is available on GitHub at:
https://github.com/JavaFXpert/tic-tac-toe-rl

To use the Q-Learning algorithm, run the TicTacToeQLearning.java program.  To interact with it in VisualExplorer, run TicTacToeWorld.java

Improvements I plan to make include:

- Try other strategies for "O" including:
    - [accomplished] always random placement
    - [accomplished] random placement except when there are opportunities to block an "X" three-in-a row
    - [accomplished] random placement except when there are opportunities to play a third "O" in a row, or block an "X" three-in-a row
    - [accomplished] prefer random corner or center placement, except when there are opportunities to play a third "O" in a row, or block an "X" three-in-a row
    - mimic an "O" player that never loses (e.g. plays perfect minimax game) to an "X" player that never loses

- Implement the VisualActionObserver so that the board and its moves are visible as Q-Learning is occurring

- Provide visualizations of the Q-Learning table and associated policies

- Improve the appearance of the rendered tic-tac-toe board, "X", and "O" marks.

- Currently, MoveAction preconditions (for only allowing "X" to move in empty cells) are implemented while Q-Learning, but I'd like to have them respected when typing the 1 - 9 keys in VisualExplorer.

Again, suggestion and collaboration are most welcome.

Regards,
James L. Weaver (Twitter: @JavaFXpert)

[1] Reinforcement Learning: An Introduction, 2016 https://webdocs.cs.ualberta.ca/~sutton/book/bookdraft2016sep.pdf
