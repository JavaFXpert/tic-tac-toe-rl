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

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.StatePainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * @author James L. Weaver (Twitter: @JavaFXpert)
 */
public class TicTacToeWorld implements DomainGenerator {
  @Override
  public SADomain generateDomain() {
    SADomain domain = new SADomain();

    domain.addActionType(new MoveActionType());
    return domain;
  }

  public StateRenderLayer getStateRenderLayer(){
    StateRenderLayer rl = new StateRenderLayer();
    rl.addStatePainter(new TicTacToeWorld.WallPainter());
    rl.addStatePainter(new TicTacToeWorld.AgentPainter());


    return rl;
  }

  public Visualizer getVisualizer(){
    return new Visualizer(this.getStateRenderLayer());
  }

  public class WallPainter implements StatePainter {
    private int NUM_ROWS_COLS = 3;

    public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {

      g2.setStroke(new BasicStroke(5));

      g2.setColor(Color.BLACK);

      //set up floats for the width and height of our domain
      float fWidth = NUM_ROWS_COLS;
      float fHeight = NUM_ROWS_COLS;

      //determine the width of a single cell
      //on our canvas such that the whole map can be painted
      float width = cWidth / fWidth;
      float height = cHeight / fHeight;

      for(int col = 1; col < NUM_ROWS_COLS; col++){
        float rx = col * width;
        g2.drawLine((int)rx, 0, (int)rx, (int)cHeight);
      }

      for(int row = 1; row < NUM_ROWS_COLS; row++){
        float ry = row * height;
        g2.drawLine(0, (int)ry, (int)cWidth, (int)ry);
      }
    }
  }

  public class AgentPainter implements StatePainter {

    private int NUM_ROWS = 3;
    private int NUM_COLS = 3;

    @Override
    public void paint(Graphics2D g2, State s,
                      float cWidth, float cHeight) {

      g2.setStroke(new BasicStroke(5));

      //marks will be filled in gray
      g2.setColor(Color.BLUE);

      //set up floats for the width and height of our domain
      float fWidth = NUM_COLS;
      float fHeight = NUM_ROWS;

      //determine the width of a single cell on our canvas
      //such that the whole map can be painted
      float width = cWidth / fWidth;
      float height = cHeight / fHeight;

      String gameBoard = (String)s.get(TicTacToeState.VAR_GAME_BOARD);

      //pass through each cell of our board, and it it's an X or O, it on our
      //canvas of dimension width x height
      for(int col = 0; col < NUM_ROWS; col++){
        for(int row = 0; row < NUM_COLS; row++){

          //TODO: Create utilities, perhaps with an associated enum, for reading contents of cells from the gameboard

          char cellMark = gameBoard.charAt(row * NUM_ROWS + col);

          //is there a mark here?
          if(cellMark != TicTacToeState.EMPTY) {

            //left coordinate of cell on our canvas
            float rx = col * width;

            //top coordinate of cell on our canvas
            float ry = row * height;

            //paint the mark (X or O)
            if (cellMark == TicTacToeState.O_MARK) {
              g2.draw(new Ellipse2D.Float(rx + width * 0.25f, ry + height * 0.25f, width * .50f, height * .50f));
            }
            else {
              g2.drawLine((int)(rx + width * 0.25f), (int)(ry + height * 0.25f), (int)(rx + width * 0.75), (int)(ry + height * 0.75));
              g2.drawLine((int)(rx + width * 0.75), (int)(ry + height * 0.25f), (int)(rx + width * 0.25f), (int)(ry + height * 0.75));
            }
          }
        }
      }
    }
  }


  public static void main(String [] args){
    TicTacToeWorld gen = new TicTacToeWorld();
    SADomain domain = gen.generateDomain();
    State initialState = new TicTacToeState(TicTacToeState.EMPTY_BOARD,
        TicTacToeState.GAME_STATUS_IN_PROGRESS);

    TicTacToeEnv env = new TicTacToeEnv();

    Visualizer v = gen.getVisualizer();
    VisualExplorer exp = new VisualExplorer(domain, env, v);

    exp.addKeyAction("0", new MoveAction(0));
    exp.addKeyAction("1", new MoveAction(1));
    exp.addKeyAction("2", new MoveAction(2));
    exp.addKeyAction("3", new MoveAction(3));
    exp.addKeyAction("4", new MoveAction(4));
    exp.addKeyAction("5", new MoveAction(5));
    exp.addKeyAction("6", new MoveAction(6));
    exp.addKeyAction("7", new MoveAction(7));
    exp.addKeyAction("8", new MoveAction(8));

    exp.initGUI();
  }
}
