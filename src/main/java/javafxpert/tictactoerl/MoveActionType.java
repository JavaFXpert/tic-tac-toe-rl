package javafxpert.tictactoerl;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamesweaver on 10/21/16.
 */
public class MoveActionType implements ActionType {
  public static String BASE_ACTION_NAME = "moveAction";

  @Override
  public String typeName() {
    return BASE_ACTION_NAME;
  }

  @Override
  public Action associatedAction(String strRep) {
    return new MoveAction(1); //TODO: Ascertain what is needed here
  }

  @Override
  public List<Action> allApplicableActions(State state) {
    List<Action> applicableActions = new ArrayList<>();
    TicTacToeState tttState = (TicTacToeState)state;

    String gameStatus = (String)tttState.get(TicTacToeState.VAR_GAME_STATUS);
    if (gameStatus.equals(TicTacToeState.GAME_STATUS_IN_PROGRESS)) {
      String gameBoard = (String)tttState.get(TicTacToeState.VAR_GAME_BOARD);
      for (int i = 0; i < TicTacToeState.NUM_CELLS; i++) {
        if (gameBoard.charAt(i) == TicTacToeState.EMPTY) {
          applicableActions.add(new MoveAction(i + 1));
        }
      }
    }
    return applicableActions;
  }
}
