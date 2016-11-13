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

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.support.ActionProb;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.environment.extensions.EnvironmentObserver;
import burlap.mdp.singleagent.environment.extensions.EnvironmentServerInterface;

import java.util.LinkedList;
import java.util.List;

/**
 * @author James L. Weaver (Twitter: @JavaFXpert)
 */
public class TicTacToePlayerEnv implements Environment, EnvironmentServerInterface {
  private static int WIN_REWARD = 10;
  private static int LOSE_REWARD = -10;
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

  EpsilonGreedy epsilonGreedyPolicy;

  MoveActionType moveActionType;

  public TicTacToePlayerEnv(EpsilonGreedy epsilonGreedyPolicy) {
    this.epsilonGreedyPolicy = epsilonGreedyPolicy;

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
    MoveAction humanAction = (MoveAction)action;

    TicTacToeState priorState = new TicTacToeState(gameBoard.toString(), gameStatus);

    // actionId is the same as the cell number (0 - 8) of the move
    int cellNum = humanAction.getActionId();

    if (cellNum < 0 || cellNum >= TicTacToeState.NUM_CELLS ||
        (gameBoard.charAt(cellNum) != TicTacToeState.EMPTY)) {

      // Illegal move attempted so don't change
      System.out.println("Illegal move attempted to cell " + cellNum);
    }
    else {
      gameBoard.setCharAt(cellNum, TicTacToeState.O_MARK);
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
      reward = 0;
      terminated = false;

      priorState.set(TicTacToeState.VAR_GAME_BOARD, gameBoard.toString());
      priorState.set(TicTacToeState.VAR_GAME_STATUS, gameStatus);

      // Play according the policy passed in to this environment

      // Get the probability distribution of all actions from this state
      List<ActionProb> actionProbs = epsilonGreedyPolicy.policyDistribution(priorState);
      System.out.println("actionProbs: " + actionProbs);

      MoveAction playerAction = (MoveAction)epsilonGreedyPolicy.action(priorState);
      System.out.println("playerAction.getActionId(): " + (playerAction.getActionId()));
      int proposedCellIndex = playerAction.getActionId();

      if (gameBoard.charAt(proposedCellIndex) == TicTacToeState.EMPTY) {
        gameBoard.setCharAt(proposedCellIndex, TicTacToeState.X_MARK);
      }
      else {
        System.out.println("Invalid move by player to cell: " + proposedCellIndex);
      }


      gameStatus = evalGameStatus();  // Evaluate game status after O has responded, and update terminated state
      if (gameStatus.equals(TicTacToeState.GAME_STATUS_O_WON)) {
        reward = 0;
        terminated = true;
      }
      else if (gameStatus.equals(TicTacToeState.GAME_STATUS_CATS_GAME)) {
        // TODO: Consider removing this condition, as it doen't seem possible to encounter
        reward = 0;
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
    gameBoard = new StringBuffer(TicTacToeState.ONE_X_BOARD);
    gameStatus = TicTacToeState.GAME_STATUS_IN_PROGRESS;

    currentObservationState = new TicTacToeState(gameBoard.toString(), gameStatus);

    terminated = false;
  }

  public EnvironmentOutcome executeActionWithGameBoard(MoveAction moveAction, String gameBoardStr) {
    this.gameBoard = new StringBuffer(gameBoardStr);
    this.gameStatus = TicTacToeState.GAME_STATUS_IN_PROGRESS;
    return executeAction(moveAction);
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
