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

/**
 * @author James L. Weaver (Twitter: @JavaFXpert)
 */
public class MoveAction implements Action {
  /**
   * The action ID of this action.  Corresponds to a tic-tac-toe cell (zero based 0-8)
   */
  private int actionId;

  public MoveAction() {}

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
