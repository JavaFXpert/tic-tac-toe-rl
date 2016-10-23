package javafxpert.tictactoerl;

import burlap.mdp.core.action.Action;

/**
 * Created by jamesweaver on 10/21/16.
 */
public class MoveAction implements Action {
  /**
   * The action ID of this action.  Corresponds to a tic-tac-toe cell (1-9)
   */
  private int actionId;

  //public MoveAction() {}

  public MoveAction(int actionId) {
    this.actionId = actionId;
  }

  public int getActionId() {
    return actionId;
  }

  @Override
  public String actionName() {
    return MoveActionType.BASE_ACTION_NAME + actionId;
  }

  @Override
  public Action copy() {
    return new MoveAction(actionId);
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) return true;
    if(o == null || getClass() != o.getClass()) return false;

    MoveAction that = (MoveAction) o;

    return actionId == that.actionId;

  }

  @Override
  public int hashCode() {
    return actionId;
  }

  @Override
  public String toString() {
    return actionName();
  }
}
