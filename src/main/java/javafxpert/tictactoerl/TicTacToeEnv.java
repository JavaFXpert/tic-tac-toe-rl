package javafxpert.tictactoerl;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.environment.extensions.EnvironmentObserver;
import burlap.mdp.singleagent.environment.extensions.EnvironmentServerInterface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jamesweaver on 10/21/16.
 */
public class TicTacToeEnv implements Environment, EnvironmentServerInterface {
  private static int WIN_REWARD = 7;
  private static int LOSE_REWARD = -3;
  private static int MOVE_REWARD = -1;

  /**
   * String representation of cells on the game board.
   * For example: "XOIIXOXIO"
   */
  private StringBuffer gameBoard;

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

  /**
   * Most recent state, to be returned by currentObservation() method
   */
  TicTacToeState currentObservationState;

  protected List<EnvironmentObserver> observers = new LinkedList<EnvironmentObserver>();

  public TicTacToeEnv() {
    resetEnvironment();
  }

  @Override
  public void addObservers(EnvironmentObserver... observers) {
    for(EnvironmentObserver o : observers){
      this.observers.add(o);
    }
  }

  @Override
  public void clearAllObservers() {
    this.observers.clear();
  }

  @Override
  public void removeObservers(EnvironmentObserver... observers) {
    for(EnvironmentObserver o : observers){
      this.observers.remove(o);
    }
  }

  @Override
  public List<EnvironmentObserver> observers() {
    return this.observers;
  }

  @Override
  public State currentObservation() {
    return currentObservationState;
  }

  @Override
  public EnvironmentOutcome executeAction(Action action) {
    MoveAction moveAction = (MoveAction)action;

    TicTacToeState priorState = new TicTacToeState(gameBoard.toString(), gameStatus);

    // actionId is the same as the cell number (1 - 9) of the move
    int cellNum = moveAction.getActionId();

    if (cellNum < 1 || cellNum > TicTacToeState.NUM_CELLS ||
        (gameBoard.charAt(cellNum - 1) != TicTacToeState.EMPTY)) {

      // Illegal move attempted so don't change
      System.out.println("Illegal move attempted to cell " + cellNum);
    }
    else {
      gameBoard.setCharAt(cellNum - 1, TicTacToeState.X_MARK);
    }

    gameStatus = evalGameStatus();
    if (gameStatus.equals(TicTacToeState.GAME_STATUS_X_WON)) {
      reward = WIN_REWARD;
      terminated = true;
    }
    else if (gameStatus.equals(TicTacToeState.GAME_STATUS_O_WON)) {

      // TODO: Consider removing this condition, as it doen't seem possible to encounter
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
      gameBoard.setCharAt(gameBoard.indexOf(Character.toString(TicTacToeState.EMPTY)), TicTacToeState.O_MARK);

      gameStatus = evalGameStatus();  // Evaluate game status after O has responded, and update terminated state
      if (gameStatus.equals(TicTacToeState.GAME_STATUS_O_WON)) {
        reward = LOSE_REWARD;
        terminated = true;
      }
      else if (gameStatus.equals(TicTacToeState.GAME_STATUS_CATS_GAME)) {
        // TODO: Consider removing this condition, as it doen't seem possible to encounter
        reward = MOVE_REWARD;
        terminated = true;
      }
    }

    TicTacToeState newState = new TicTacToeState(gameBoard.toString(), gameStatus);

    currentObservationState = newState.copy();

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
    gameBoard = new StringBuffer(TicTacToeState.EMPTY_BOARD);
    gameStatus = TicTacToeState.GAME_STATUS_IN_PROGRESS;

    currentObservationState = new TicTacToeState(gameBoard.toString(), gameStatus);

    terminated = false;
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
      if (gameBoard.charAt(idx) ==  TicTacToeState.EMPTY) {
        gameStatus = TicTacToeState.GAME_STATUS_IN_PROGRESS;
        break;
      }
    }

    // Check if X won
    if ((gameBoard.charAt(0) == TicTacToeState.X_MARK && gameBoard.charAt(1) == TicTacToeState.X_MARK && gameBoard.charAt(2) == TicTacToeState.X_MARK) ||
        (gameBoard.charAt(3) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(5) == TicTacToeState.X_MARK) ||
        (gameBoard.charAt(6) == TicTacToeState.X_MARK && gameBoard.charAt(7) == TicTacToeState.X_MARK && gameBoard.charAt(8) == TicTacToeState.X_MARK) ||
        (gameBoard.charAt(0) == TicTacToeState.X_MARK && gameBoard.charAt(3) == TicTacToeState.X_MARK && gameBoard.charAt(6) == TicTacToeState.X_MARK) ||
        (gameBoard.charAt(1) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(7) == TicTacToeState.X_MARK) ||
        (gameBoard.charAt(2) == TicTacToeState.X_MARK && gameBoard.charAt(5) == TicTacToeState.X_MARK && gameBoard.charAt(8) == TicTacToeState.X_MARK) ||
        (gameBoard.charAt(0) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(8) == TicTacToeState.X_MARK) ||
        (gameBoard.charAt(2) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(6) == TicTacToeState.X_MARK)) {
      gameStatus = TicTacToeState.GAME_STATUS_X_WON;
      //System.out.println("X won");
      System.out.print("X");
    }
    else if ((gameBoard.charAt(0) == TicTacToeState.O_MARK && gameBoard.charAt(1) == TicTacToeState.O_MARK && gameBoard.charAt(2) == TicTacToeState.O_MARK) ||
        (gameBoard.charAt(3) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(5) == TicTacToeState.O_MARK) ||
        (gameBoard.charAt(6) == TicTacToeState.O_MARK && gameBoard.charAt(7) == TicTacToeState.O_MARK && gameBoard.charAt(8) == TicTacToeState.O_MARK) ||
        (gameBoard.charAt(0) == TicTacToeState.O_MARK && gameBoard.charAt(3) == TicTacToeState.O_MARK && gameBoard.charAt(6) == TicTacToeState.O_MARK) ||
        (gameBoard.charAt(1) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(7) == TicTacToeState.O_MARK) ||
        (gameBoard.charAt(2) == TicTacToeState.O_MARK && gameBoard.charAt(5) == TicTacToeState.O_MARK && gameBoard.charAt(8) == TicTacToeState.O_MARK) ||
        (gameBoard.charAt(0) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(8) == TicTacToeState.O_MARK) ||
        (gameBoard.charAt(2) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(6) == TicTacToeState.O_MARK)) {
      gameStatus = TicTacToeState.GAME_STATUS_O_WON;
      //System.out.println("O won");
      System.out.print("o");
    }

    if (gameStatus.equals(TicTacToeState.GAME_STATUS_CATS_GAME)) {
      //System.out.println("Cat's game");
      System.out.print(".");
    }
    return gameStatus;
  }
}
