/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * @author James L. Weaver (Twitter: @JavaFXpert)
 */
public class TicTacToeEnv implements Environment, EnvironmentServerInterface {
  private static int WIN_REWARD = 7;
  private static int LOSE_REWARD = -7;
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

      // Uncomment to employ a strategy of playing the first empty cell with an "O"
      //playFirstEmptyCell();

      // Uncomment to employ a strategy of playing a completely random empty cell with an "O"
      //playRandomCell();

      // Uncomment to employ a strategy that randomly places "O" except when there are opportunities to block an "X" three-in-a row
      //blockOrPlayRandom();

      // Uncomment to employ a strategy that randomly places "O" except when
      // there are opportunities to play third "O" in a row, or to block an "X" three-in-a row
      winOrblockOrPlayRandom();

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

  /**
   * Simple strategy that plays the first empty cell with an "O"
   */
  private void playFirstEmptyCell() {
    gameBoard.setCharAt(gameBoard.indexOf(Character.toString(TicTacToeState.EMPTY)), TicTacToeState.O_MARK);
  }

  /**
   * Simple strategy that plays a completely random empty cell with an "O"
   */
  private void playRandomCell() {
    boolean played = false;
    while (!played) {
      int proposedCellIndex = (int)(Math.random() * TicTacToeState.NUM_CELLS);
      if (gameBoard.charAt(proposedCellIndex) == TicTacToeState.EMPTY) {
        gameBoard.setCharAt(proposedCellIndex, TicTacToeState.O_MARK);
        played = true;
      }
    }
  }

  /**
   * Strategy that randomly places "O" except when there are opportunities to block an "X" three-in-a row
   */
  private void blockOrPlayRandom() {
    int cellIndexToPlay = evalGameboardForBlock();
    if (cellIndexToPlay != -1) {
      gameBoard.setCharAt(cellIndexToPlay, TicTacToeState.O_MARK);
    }
    else {
      playRandomCell();
    }
  }

  /**
   * Strategy that randomly places "O" except when there are opportunities to
   * play third "O" in a row, to block an "X" three-in-a row
   */
  private void winOrblockOrPlayRandom() {
    int cellIndexToPlay = evalGameboardForWin();
    if (cellIndexToPlay != -1) {
      gameBoard.setCharAt(cellIndexToPlay, TicTacToeState.O_MARK);
      return;
    }
    cellIndexToPlay = evalGameboardForBlock();
    if (cellIndexToPlay != -1) {
      gameBoard.setCharAt(cellIndexToPlay, TicTacToeState.O_MARK);
    }
    else {
      playRandomCell();
    }
  }

  /**
   * Evaluate the gameboard for an opportunity to block "X" three-in-a row
   * TODO: Modify with a less brute-force, and less verbose, approach.  Possibly factor with evalGameboardForWin() method
   *
   * @return Zero-based index of cell that would block, or -1 if no cells apply
   */
  private int evalGameboardForBlock() {
    int blockingPlay = -1;
    if (gameBoard.charAt(0) == TicTacToeState.X_MARK && gameBoard.charAt(1) == TicTacToeState.X_MARK && gameBoard.charAt(1) == TicTacToeState.EMPTY) {
      blockingPlay = 2;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.X_MARK && gameBoard.charAt(1) == TicTacToeState.EMPTY && gameBoard.charAt(2) == TicTacToeState.X_MARK) {
      blockingPlay = 1;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.EMPTY && gameBoard.charAt(1) == TicTacToeState.X_MARK && gameBoard.charAt(2) == TicTacToeState.X_MARK) {
      blockingPlay = 0;
    }
    else if (gameBoard.charAt(3) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(5) == TicTacToeState.EMPTY) {
      blockingPlay = 5;
    }
    else if (gameBoard.charAt(3) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.EMPTY && gameBoard.charAt(5) == TicTacToeState.X_MARK) {
      blockingPlay = 4;
    }
    else if (gameBoard.charAt(3) == TicTacToeState.EMPTY && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(5) == TicTacToeState.X_MARK) {
      blockingPlay = 3;
    }
    else if (gameBoard.charAt(6) == TicTacToeState.X_MARK && gameBoard.charAt(7) == TicTacToeState.X_MARK && gameBoard.charAt(8) == TicTacToeState.EMPTY) {
      blockingPlay = 8;
    }
    else if (gameBoard.charAt(6) == TicTacToeState.X_MARK && gameBoard.charAt(7) == TicTacToeState.EMPTY && gameBoard.charAt(8) == TicTacToeState.X_MARK) {
      blockingPlay = 7;
    }
    else if (gameBoard.charAt(6) == TicTacToeState.EMPTY && gameBoard.charAt(7) == TicTacToeState.X_MARK && gameBoard.charAt(8) == TicTacToeState.X_MARK) {
      blockingPlay = 6;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.X_MARK && gameBoard.charAt(3) == TicTacToeState.X_MARK && gameBoard.charAt(6) == TicTacToeState.EMPTY) {
      blockingPlay = 6;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.X_MARK && gameBoard.charAt(3) == TicTacToeState.EMPTY && gameBoard.charAt(6) == TicTacToeState.X_MARK) {
      blockingPlay = 3;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.EMPTY && gameBoard.charAt(3) == TicTacToeState.X_MARK && gameBoard.charAt(6) == TicTacToeState.X_MARK) {
      blockingPlay = 0;
    }
    else if (gameBoard.charAt(1) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(7) == TicTacToeState.EMPTY) {
      blockingPlay = 7;
    }
    else if (gameBoard.charAt(1) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.EMPTY && gameBoard.charAt(7) == TicTacToeState.X_MARK) {
      blockingPlay = 4;
    }
    else if (gameBoard.charAt(1) == TicTacToeState.EMPTY && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(7) == TicTacToeState.X_MARK) {
      blockingPlay = 1;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.X_MARK && gameBoard.charAt(5) == TicTacToeState.X_MARK && gameBoard.charAt(8) == TicTacToeState.EMPTY) {
      blockingPlay = 8;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.X_MARK && gameBoard.charAt(5) == TicTacToeState.EMPTY && gameBoard.charAt(8) == TicTacToeState.X_MARK) {
      blockingPlay = 5;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.EMPTY && gameBoard.charAt(5) == TicTacToeState.X_MARK && gameBoard.charAt(8) == TicTacToeState.X_MARK) {
      blockingPlay = 2;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(8) == TicTacToeState.EMPTY) {
      blockingPlay = 8;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.EMPTY && gameBoard.charAt(8) == TicTacToeState.X_MARK) {
      blockingPlay = 4;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.EMPTY && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(8) == TicTacToeState.X_MARK) {
      blockingPlay = 0;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(6) == TicTacToeState.EMPTY) {
      blockingPlay = 6;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.X_MARK && gameBoard.charAt(4) == TicTacToeState.EMPTY && gameBoard.charAt(6) == TicTacToeState.X_MARK) {
      blockingPlay = 4;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.EMPTY && gameBoard.charAt(4) == TicTacToeState.X_MARK && gameBoard.charAt(6) == TicTacToeState.X_MARK) {
      blockingPlay = 2;
    }
    return blockingPlay;
  }

  /**
   * Evaluate the gameboard for an opportunity to get "O" three-in-a row
   * TODO: Modify with a less brute-force, and less verbose, approach.  Possibly factor with evalGameboardForBlock() method
   *
   * @return Zero-based index of cell that would win, or -1 if no cells apply
   */
  private int evalGameboardForWin() {
    int winningPlay = -1;
    if (gameBoard.charAt(0) == TicTacToeState.O_MARK && gameBoard.charAt(1) == TicTacToeState.O_MARK && gameBoard.charAt(1) == TicTacToeState.EMPTY) {
      winningPlay = 2;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.O_MARK && gameBoard.charAt(1) == TicTacToeState.EMPTY && gameBoard.charAt(2) == TicTacToeState.O_MARK) {
      winningPlay = 1;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.EMPTY && gameBoard.charAt(1) == TicTacToeState.O_MARK && gameBoard.charAt(2) == TicTacToeState.O_MARK) {
      winningPlay = 0;
    }
    else if (gameBoard.charAt(3) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(5) == TicTacToeState.EMPTY) {
      winningPlay = 5;
    }
    else if (gameBoard.charAt(3) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.EMPTY && gameBoard.charAt(5) == TicTacToeState.O_MARK) {
      winningPlay = 4;
    }
    else if (gameBoard.charAt(3) == TicTacToeState.EMPTY && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(5) == TicTacToeState.O_MARK) {
      winningPlay = 3;
    }
    else if (gameBoard.charAt(6) == TicTacToeState.O_MARK && gameBoard.charAt(7) == TicTacToeState.O_MARK && gameBoard.charAt(8) == TicTacToeState.EMPTY) {
      winningPlay = 8;
    }
    else if (gameBoard.charAt(6) == TicTacToeState.O_MARK && gameBoard.charAt(7) == TicTacToeState.EMPTY && gameBoard.charAt(8) == TicTacToeState.O_MARK) {
      winningPlay = 7;
    }
    else if (gameBoard.charAt(6) == TicTacToeState.EMPTY && gameBoard.charAt(7) == TicTacToeState.O_MARK && gameBoard.charAt(8) == TicTacToeState.O_MARK) {
      winningPlay = 6;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.O_MARK && gameBoard.charAt(3) == TicTacToeState.O_MARK && gameBoard.charAt(6) == TicTacToeState.EMPTY) {
      winningPlay = 6;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.O_MARK && gameBoard.charAt(3) == TicTacToeState.EMPTY && gameBoard.charAt(6) == TicTacToeState.O_MARK) {
      winningPlay = 3;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.EMPTY && gameBoard.charAt(3) == TicTacToeState.O_MARK && gameBoard.charAt(6) == TicTacToeState.O_MARK) {
      winningPlay = 0;
    }
    else if (gameBoard.charAt(1) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(7) == TicTacToeState.EMPTY) {
      winningPlay = 7;
    }
    else if (gameBoard.charAt(1) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.EMPTY && gameBoard.charAt(7) == TicTacToeState.O_MARK) {
      winningPlay = 4;
    }
    else if (gameBoard.charAt(1) == TicTacToeState.EMPTY && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(7) == TicTacToeState.O_MARK) {
      winningPlay = 1;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.O_MARK && gameBoard.charAt(5) == TicTacToeState.O_MARK && gameBoard.charAt(8) == TicTacToeState.EMPTY) {
      winningPlay = 8;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.O_MARK && gameBoard.charAt(5) == TicTacToeState.EMPTY && gameBoard.charAt(8) == TicTacToeState.O_MARK) {
      winningPlay = 5;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.EMPTY && gameBoard.charAt(5) == TicTacToeState.O_MARK && gameBoard.charAt(8) == TicTacToeState.O_MARK) {
      winningPlay = 2;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(8) == TicTacToeState.EMPTY) {
      winningPlay = 8;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.EMPTY && gameBoard.charAt(8) == TicTacToeState.O_MARK) {
      winningPlay = 4;
    }
    else if (gameBoard.charAt(0) == TicTacToeState.EMPTY && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(8) == TicTacToeState.O_MARK) {
      winningPlay = 0;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(6) == TicTacToeState.EMPTY) {
      winningPlay = 6;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.O_MARK && gameBoard.charAt(4) == TicTacToeState.EMPTY && gameBoard.charAt(6) == TicTacToeState.O_MARK) {
      winningPlay = 4;
    }
    else if (gameBoard.charAt(2) == TicTacToeState.EMPTY && gameBoard.charAt(4) == TicTacToeState.O_MARK && gameBoard.charAt(6) == TicTacToeState.O_MARK) {
      winningPlay = 2;
    }
    return winningPlay;
  }


}
