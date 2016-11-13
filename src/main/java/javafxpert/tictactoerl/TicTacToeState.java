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

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

import java.util.Arrays;
import java.util.List;

/**
 * @author James L. Weaver (Twitter: @JavaFXpert)
 */
@DeepCopyState
public class TicTacToeState implements MutableState {
  /**
   * Constant for number of cells on a Tac-Tac-Toe board
   */
  public static int NUM_CELLS = 9;

  public static String VAR_GAME_BOARD = "gameBoard";
  public static String VAR_GAME_STATUS = "gameStatus";


  public static char X_MARK = 'X';
  public static char O_MARK = 'O';
  public static char EMPTY = 'I';

  public static String GAME_STATUS_IN_PROGRESS = "I";
  public static String GAME_STATUS_X_WON =       "X";
  public static String GAME_STATUS_O_WON =       "O";
  public static String GAME_STATUS_CATS_GAME =   "C";

  public static String EMPTY_BOARD = "IIIIIIIII"; //TODO: Put back
  public static String ONE_X_BOARD = "XIIIIIIII"; //TODO: Put back

  /**
   * String representation of cells on the game board.
   * For example: "XOIIXOXIO"
   */
  public String gameBoard = EMPTY_BOARD;

  /**
   * Game status, specifically, whether the game is in-progress, or if X won,
   * or if O won, or if it is cat's game (nobody won).
   */
  public String gameStatus = GAME_STATUS_IN_PROGRESS;

  private final static List<Object> keys =
      Arrays.asList(VAR_GAME_BOARD, VAR_GAME_STATUS);

  public TicTacToeState() {
  }

  public TicTacToeState(String gameBoard, String gameStatus) {
    this.gameBoard = gameBoard;
    this.gameStatus = gameStatus;
  }

  @Override
  public MutableState set(Object variableKey, Object value) {
    if(variableKey.equals(VAR_GAME_BOARD)){
      this.gameBoard = (String)value;
    }
    else if(variableKey.equals(VAR_GAME_STATUS)){
      this.gameStatus = (String)value;
    }
    else{
      throw new UnknownKeyException(variableKey);
    }
    return this;
  }

  @Override
  public List<Object> variableKeys() {
    return keys;
  }

  @Override
  public Object get(Object variableKey) {
    if(variableKey.equals(VAR_GAME_BOARD)){
      return this.gameBoard;
    }
    else if(variableKey.equals(VAR_GAME_STATUS)){
      return this.gameStatus;
    }
    throw new UnknownKeyException(variableKey);
  }

  @Override
  public TicTacToeState copy() {
    return new TicTacToeState(gameBoard, gameStatus);
  }

  @Override
  public String toString() {
    return StateUtilities.stateToString(this);
  }
}
