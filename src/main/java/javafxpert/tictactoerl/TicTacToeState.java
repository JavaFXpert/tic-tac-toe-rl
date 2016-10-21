package javafxpert.tictactoerl;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.annotations.DeepCopyState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamesweaver on 10/20/16.
 */
@DeepCopyState
public class TicTacToeState implements MutableState {
  private static String GAME_STATUS_IN_PROGRESS = "I";
  private static String GAME_STATUS_X_WON =       "X";
  private static String GAME_STATUS_O_WON =       "O";
  private static String GAME_STATUS_CATS_GAME =   "C";
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



  public MutableState set(Object o, Object o1) {
    return null;
  }

  public List<Object> variableKeys() {
    return null;
  }

  public Object get(Object o) {
    return null;
  }

  public State copy() {
    return null;
  }
}
