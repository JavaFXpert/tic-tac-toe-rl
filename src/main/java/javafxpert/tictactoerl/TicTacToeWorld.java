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
import burlap.mdp.singleagent.environment.Environment;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.StatePainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
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

      //walls will be filled in black
      g2.setColor(Color.BLACK);

      //set up floats for the width and height of our domain
      float fWidth = NUM_ROWS_COLS;
      float fHeight = NUM_ROWS_COLS;

      //determine the width of a single cell
      //on our canvas such that the whole map can be painted
      float width = cWidth / fWidth;
      float height = cHeight / fHeight;

      //pass through each cell of our map and if it's a wall, paint a black rectangle on our
      //canvas of dimension widthxheight
      for(int i = 0; i < NUM_ROWS_COLS; i++){
        for(int j = 0; j < NUM_ROWS_COLS; j++){

          //is there a wall here?
          //if((i % 2 == 1) || (j % 2 == 1)) {
          if (true) {

            //left coordinate of cell on our canvas
            float rx = i * width;

            //top coordinate of cell on our canvas
            //coordinate system adjustment because the java canvas
            //origin is in the top left instead of the bottom right
            float ry = cHeight - height - j * height;

            //paint the rectangle
            g2.draw(new Rectangle2D.Float(rx, ry, width, height));
          }


        }
      }

    }
  }

  public class AgentPainter implements StatePainter {

    private int NUM_ROWS = 3;
    private int NUM_COLS = 3;

    @Override
    public void paint(Graphics2D g2, State s,
                      float cWidth, float cHeight) {

      //agent will be filled in gray
      g2.setColor(Color.GRAY);

      //set up floats for the width and height of our domain
      float fWidth = NUM_COLS;
      float fHeight = NUM_ROWS;

      //determine the width of a single cell on our canvas
      //such that the whole map can be painted
      float width = cWidth / fWidth;
      float height = cHeight / fHeight;

//      int ax = (Integer)s.get(VAR_X);
//      int ay = (Integer)s.get(VAR_Y);

      String gameBoard = (String)s.get(TicTacToeState.VAR_GAME_BOARD);

      //pass through each cell of our board, and it it's an X or O, it on our
      //canvas of dimension width x height
      for(int col = 0; col < NUM_ROWS; col++){
        for(int row = 0; row < NUM_COLS; row++){

          //TODO: Create utilities, perhaps with an associated enum, for reading contents of cells from the gameboard

          char cellMark = gameBoard.charAt(row * NUM_ROWS + col);

          //is there a mark here?
          if(cellMark != TicTacToeState.EMPTY) {
            //if (true) {

            //left coordinate of cell on our canvas
            float rx = col * width;

            //top coordinate of cell on our canvas
            //coordinate system adjustment because the java canvas
            //origin is in the top left instead of the bottom right
            float ry = cHeight - height - row * height;

            //paint the mark (X or O)
            if (cellMark == TicTacToeState.O_MARK) {
              g2.draw(new Ellipse2D.Float(rx, ry, width, height));
            }
            else {
              g2.drawLine((int)rx, (int)ry, (int)(rx + width), (int)(ry + height));
              g2.drawLine((int)(rx + width), (int)ry, (int)rx, (int)(ry + height));
            }
            //g2.fill(new Ellipse2D.Float(rx, ry, width, height));

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

    // TODO: Ascertain whether necessary to pass domain or initial state to constructor
    //       like ExampleGridWorld does
    TicTacToeEnv env = new TicTacToeEnv();

    Visualizer v = gen.getVisualizer();
    VisualExplorer exp = new VisualExplorer(domain, env, v);

    exp.initGUI();
  }

}
