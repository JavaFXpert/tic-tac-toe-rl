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

import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;

/**
 * @author James L. Weaver (Twitter: @JavaFXpert)
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
