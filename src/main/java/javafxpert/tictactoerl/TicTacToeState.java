package javafxpert.tictactoerl;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jamesweaver on 10/20/16.
 */
@DeepCopyState
public class TicTacToeState implements MutableState {
  /**
   * Constant for number of cells on a Tac-Tac-Toe board
   */
  public static int NUM_CELLS = 9;

  public static String VAR_GAME_BOARD = "gameBoard";
  public static String VAR_GAME_STATUS = "gameStatus";

  public static String GAME_STATUS_IN_PROGRESS = "I";
  public static String GAME_STATUS_X_WON =       "X";
  public static String GAME_STATUS_O_WON =       "O";
  public static String GAME_STATUS_CATS_GAME =   "C";

  //public static String EMPTY_BOARD = "IIIIIIII"; //TODO: Put back
  public static String EMPTY_BOARD = "XOXIIIOXO";
  /**
   * String representation of cells on the game board.
   * For example: "XOIIXOXIO"
   */
  private String gameBoard = EMPTY_BOARD;

  /**
   * Game status, specifically, whether the game is in-progress, or if X won,
   * or if O won, or if it is cat's game (nobody won).
   */
  private String gameStatus = GAME_STATUS_IN_PROGRESS;

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
