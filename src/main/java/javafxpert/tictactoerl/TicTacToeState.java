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
  public static String VAR_PRIOR_MOVES = "priorMoves";
  public static String VAR_GAME_STATUS = "gameStatus";
  public static String VAR_FIRST_PLAYER = "firstPlayer";

  public static String GAME_STATUS_IN_PROGRESS = "I";
  public static String GAME_STATUS_X_WON =       "X";
  public static String GAME_STATUS_O_WON =       "O";
  public static String GAME_STATUS_CATS_GAME =   "C";

  private static String FIRST_PLAYER_X =          "X";

  /**
   * List of moves made in this game so far.  Will be an empty list if no moves have been made.
   * Cells on game board are notated as follows:
   *
   *   1 | 2 | 3
   *   ----------
   *   4 | 5 | 6
   *   ----------
   *   7 | 8 | 9
   *
   */
  private List<Integer> priorMoves = new ArrayList<>();

  /**
   * Game status, specifically, whether the game is in-progress, or if X won,
   * or if O won, or if it is cat's game (nobody won).
   */
  private String gameStatus = GAME_STATUS_IN_PROGRESS;

  /**
   * Status of who played first (X or O).  Currently, the computer always
   * moves first, and is X.
   */
  private String firstPlayer = FIRST_PLAYER_X;

  private final static List<Object> keys =
      Arrays.asList(VAR_PRIOR_MOVES, VAR_GAME_STATUS, VAR_FIRST_PLAYER);

  public TicTacToeState() {
  }

  public TicTacToeState(List<Integer> priorMoves, String gameStatus, String firstPlayer) {
    this.priorMoves = priorMoves;
    this.gameStatus = gameStatus;
    this.firstPlayer = firstPlayer;
  }

  @Override
  public MutableState set(Object variableKey, Object value) {
    if(variableKey.equals(VAR_PRIOR_MOVES)){
      this.priorMoves = (List<Integer>)value;
    }
    else if(variableKey.equals(VAR_GAME_STATUS)){
      this.gameStatus = (String)value;
    }
    else if(variableKey.equals(VAR_FIRST_PLAYER)){
      this.firstPlayer = (String)value;
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
    if(variableKey.equals(VAR_PRIOR_MOVES)){
      return this.priorMoves;
    }
    else if(variableKey.equals(VAR_GAME_STATUS)){
      return this.gameStatus;
    }
    else if(variableKey.equals(VAR_FIRST_PLAYER)){
      return this.firstPlayer;
    }
    throw new UnknownKeyException(variableKey);
  }

  @Override
  public TicTacToeState copy() {
    return new TicTacToeState(priorMoves, gameStatus, firstPlayer);
  }

  @Override
  public String toString() {
    return StateUtilities.stateToString(this);
  }
}
