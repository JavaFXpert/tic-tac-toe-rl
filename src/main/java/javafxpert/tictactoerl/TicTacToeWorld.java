/*
  Development choices:
  [] Implement an ActionType (because pre-conditions are required)

  Development questions:
  [] Use SimpleAction?
  [] Use SampleModel or Enviromment?

 */
package javafxpert.tictactoerl;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;

import java.util.ArrayList;

/**
 * Created by jamesweaver on 10/20/16.
 */
public class TicTacToeWorld implements DomainGenerator {
  @Override
  public SADomain generateDomain() {
    SADomain domain = new SADomain();

    domain.addActionType(new MoveActionType());
    return domain;
  }

  public static void main(String [] args){
    TicTacToeWorld gen = new TicTacToeWorld();
    SADomain domain = gen.generateDomain();
    State initialState = new TicTacToeState(new ArrayList<Integer>(),
        TicTacToeState.GAME_STATUS_IN_PROGRESS, TicTacToeState.FIRST_PLAYER_X);
  }

}
