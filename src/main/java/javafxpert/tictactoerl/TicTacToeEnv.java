package javafxpert.tictactoerl;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamesweaver on 10/21/16.
 */
public class TicTacToeEnv implements Environment {
  private static int WIN_REWARD = 8;
  private static int LOSE_REWARD = -8;
  private static int MOVE_REWARD = -1;

  private static char X_MARK = 'X';
  private static char O_MARK = 'O';
  private static char EMPTY = 'I';

  /**
   * String representation of cells on the game board.
   * For example: "XOIIXOXIO"
   */
  private StringBuffer gb;

  /**
   * Game status, specifically, whether the game is in-progress, or if X won,
   * or if O won, or if it is cat's game (nobody won).
   */
  private String gameStatus;

  /**
   * Indicates whether the the game is in the terminal state
   */
  private boolean terminated = false;

  /**
   * Reward given for the current action
   */
  private int reward = 0;

  public TicTacToeEnv() {
    resetEnvironment();
  }

  @Override
  public State currentObservation() {
    return null;
  }

  @Override
  public EnvironmentOutcome executeAction(Action action) {
    MoveAction moveAction = (MoveAction)action;

    TicTacToeState priorState = new TicTacToeState();
    priorState.set(TicTacToeState.VAR_GAME_BOARD, gb.toString());
    priorState.set(TicTacToeState.VAR_GAME_STATUS, gameStatus);

    TicTacToeState newState = new TicTacToeState();

    // actionId is the same as the cell number (1 - 9) of the move
    int cellNum = moveAction.getActionId();

    if (cellNum < 1 || cellNum > TicTacToeState.NUM_CELLS ||
        (gb.charAt(cellNum - 1) != EMPTY)) {
      // Illegal move attempted so don't change
      System.out.println("Illegal move attempted to cell " + cellNum);
    }
    else {
      gb.setCharAt(cellNum - 1, X_MARK);
    }

    gameStatus = evalGameStatus();
    if (gameStatus.equals(TicTacToeState.GAME_STATUS_X_WON)) {
      reward = WIN_REWARD;
      terminated = true;
    }
    else if (gameStatus.equals(TicTacToeState.GAME_STATUS_X_WON)) {
      reward = LOSE_REWARD;
      terminated = true;
    }
    else if (gameStatus.equals(TicTacToeState.GAME_STATUS_CATS_GAME)) {
      reward = MOVE_REWARD;
      terminated = true;
    }
    else {
      reward = MOVE_REWARD;
      terminated = false;
      // For now, the environment will employ a simple strategy, filling in the
      // first empty cell with an "O"
      gb.setCharAt(gb.indexOf(Character.toString(EMPTY)), O_MARK);
    }

    newState.set(TicTacToeState.VAR_GAME_BOARD, gb.toString());
    newState.set(TicTacToeState.VAR_GAME_STATUS, gameStatus);

    EnvironmentOutcome environmentOutcome =
        new EnvironmentOutcome(priorState, action, newState, reward, terminated);

    return environmentOutcome;
  }

  @Override
  public double lastReward() {
    return reward;
  }

  @Override
  public boolean isInTerminalState() {
    return terminated;
  }

  @Override
  public void resetEnvironment() {
    gb = new StringBuffer("IIIIIIII");
    gameStatus = TicTacToeState.GAME_STATUS_IN_PROGRESS;
  }

  /**
   * Evaluate the status of the game (in-progress, or who won)
   *
   * @return Indicator of in-progress, or who won
   */
  private String evalGameStatus() {
    // Start with the assumption that all cells are occupied but nobody won
    String gameStatus = TicTacToeState.GAME_STATUS_CATS_GAME;

    // Check if this game is still in progress
    for (int idx = 0; idx < TicTacToeState.NUM_CELLS; idx++) {
      if (gb.charAt(idx) ==  EMPTY) {
        gameStatus = TicTacToeState.GAME_STATUS_IN_PROGRESS;
        break;
      }
    }

    // Check if X won
    if ((gb.charAt(0) == X_MARK && gb.charAt(1) == X_MARK && gb.charAt(2) == X_MARK) ||
        (gb.charAt(3) == X_MARK && gb.charAt(4) == X_MARK && gb.charAt(5) == X_MARK) ||
        (gb.charAt(6) == X_MARK && gb.charAt(7) == X_MARK && gb.charAt(8) == X_MARK) ||
        (gb.charAt(0) == X_MARK && gb.charAt(3) == X_MARK && gb.charAt(6) == X_MARK) ||
        (gb.charAt(1) == X_MARK && gb.charAt(4) == X_MARK && gb.charAt(7) == X_MARK) ||
        (gb.charAt(2) == X_MARK && gb.charAt(5) == X_MARK && gb.charAt(8) == X_MARK) ||
        (gb.charAt(0) == X_MARK && gb.charAt(4) == X_MARK && gb.charAt(8) == X_MARK) ||
        (gb.charAt(2) == X_MARK && gb.charAt(4) == X_MARK && gb.charAt(6) == X_MARK)) {
      gameStatus = TicTacToeState.GAME_STATUS_X_WON;
    }
    else if ((gb.charAt(0) == O_MARK && gb.charAt(1) == O_MARK && gb.charAt(2) == O_MARK) ||
        (gb.charAt(3) == O_MARK && gb.charAt(4) == O_MARK && gb.charAt(5) == O_MARK) ||
        (gb.charAt(6) == O_MARK && gb.charAt(7) == O_MARK && gb.charAt(8) == O_MARK) ||
        (gb.charAt(0) == O_MARK && gb.charAt(3) == O_MARK && gb.charAt(6) == O_MARK) ||
        (gb.charAt(1) == O_MARK && gb.charAt(4) == O_MARK && gb.charAt(7) == O_MARK) ||
        (gb.charAt(2) == O_MARK && gb.charAt(5) == O_MARK && gb.charAt(8) == O_MARK) ||
        (gb.charAt(0) == O_MARK && gb.charAt(4) == O_MARK && gb.charAt(8) == O_MARK) ||
        (gb.charAt(2) == O_MARK && gb.charAt(4) == O_MARK && gb.charAt(6) == O_MARK)) {
      gameStatus = TicTacToeState.GAME_STATUS_O_WON;
    }
    return gameStatus;
  }
}
