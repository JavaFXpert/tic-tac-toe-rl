package javafxpert.tictactoerl;

import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;

/**
 * Created by jamesweaver on 10/24/16.
 */
public class TicTacToeQLearning {
  public static void main(String[] args) {
    TicTacToeWorld ticTacToeWorld = new TicTacToeWorld();
    SADomain domain = ticTacToeWorld.generateDomain();
    HashableStateFactory hashingFactory = new SimpleHashableStateFactory();
    LearningAgent agent = new QLearning(domain, 0.99, hashingFactory, 0.0, 1.0);
    TicTacToeEnv env = new TicTacToeEnv();

    //run learning for 200 episodes
    String outputPath = "output/";
    for(int i = 0; i < 200; i++){
      Episode e = agent.runLearningEpisode(env);

      e.write(outputPath + "ql_" + i);

      //reset environment for next learning episode
      env.resetEnvironment();
    }

//TODO: Attempt to get this visualization working
//    VisualActionObserver observer = new VisualActionObserver(ticTacToeWorld.getVisualizer());
//		observer.initGUI();
//		env.addObservers(observer);

    Visualizer v = ticTacToeWorld.getVisualizer();
    new EpisodeSequenceVisualizer(v, domain, outputPath);
  }
}
