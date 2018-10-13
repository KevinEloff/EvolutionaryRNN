/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machinelearningtests;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 *
 * @author Kevin
 */
public class Main {
    
    //private static float food_x, food_y, player_x, player_y;
    //private static int[] nShape = {4, 32, 32, 4};
    public static int[] nShape = {6, 64, 64, 4};
    public static final int GENERATIONS = 100000;
    public static final int TURNS = 35;
    public static final int SAMPLES = 200;
    public static final float MUTATION = 0.03f; //Old mute 0.1
    public static final int MAP_SIZE = 10;
    //public static final String INPUT = "out8-64-64-4-LINE.txt"; //in2 for double L
    public static final String INPUT = "rnn-6-64-64-4-b-hard.txt"; //complex-8-64-64-4.txt
    public static final String MAPNAME = "map_hard";
    public static final boolean TRAIN = true;
    
    public static boolean[][] map;
    
    public static void main(String[] args) {
        
        NeuralNetwork t = new NeuralNetwork(nShape);
        System.out.println(t.toString());
        
        //StdDraw.setCanvasSize(800, 800);
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.setPenRadius(0.003);
        
        loadMap(MAPNAME);
        Agent2[] agents = new Agent2[SAMPLES];
        for (int i = 0; i < SAMPLES; i++)
            agents[i] = new Agent2(nShape);
        
        if (!INPUT.isEmpty()) for (int i = 0; i < agents.length/4; i++)
            agents[i] = new Agent2(nShape, INPUT);
        
        for (int generation = 0; generation < GENERATIONS && TRAIN; generation++) {
            
            //init agents
            for (Agent2 agent: agents) {
                agent.score = 0;
            }
            for (int j = 0; j < 4; j++) {
                int[] map_stuff = new int[4]; //Food x,y; Player x,y;
                for (int i = 0; i < 4; i++)
                    map_stuff[i] = (int)(Math.random()*MAP_SIZE);
                    //map_stuff[i] = (int)(Math.random()*4 + 6*(int)(Math.random()*2));
                    
                //make sure not invalid
                while (map[map_stuff[1]][map_stuff[0]]) map_stuff[(int)(Math.random()*2)] = (int)(Math.random()*MAP_SIZE);
                while (map[map_stuff[3]][map_stuff[2]]) map_stuff[(int)(Math.random()*2)+2] = (int)(Math.random()*MAP_SIZE);
                
//                map_stuff[2] = 0;
//                map_stuff[3] = 0;
                for (Agent2 agent: agents) {
                    agent.x = map_stuff[2];
                    agent.y = map_stuff[3];
                    agent.lx = 0;
                    agent.ly = 0;
                    agent.moves = 0;
                    agent.reset();
                }
                
                for (int i = 0; i < TURNS; i++) for (Agent2 agent: agents)
                    agent.move(map_stuff[0], map_stuff[1]);
                
                for(Agent2 agent: agents)
                    agent.score += agent.getScore(map_stuff[0], map_stuff[1]);
            }
            
            //Sort best score
            boolean sorted = false;
            while (!sorted) {
                sorted = true;
                for (int i = 0; i < agents.length-1; i++) {
                    if (agents[i].score < agents[i+1].score) {
                        sorted = false;
                        Agent2 temp = agents[i];
                        agents[i] = agents[i+1];
                        agents[i+1] = temp;
                    }
                }
            }
            
            for (int i = 0; i < SAMPLES/4; i++) {
                agents[i+SAMPLES/4*3] = agents[i].duplicate();
                agents[i+SAMPLES/4*3].mutate(MUTATION);
                agents[i+SAMPLES/4*2] = agents[i].duplicate();
                agents[i+SAMPLES/4*2].mutate(MUTATION*4);
            }
            
            if ((float)(generation)/5 == generation/5) {
                System.out.println("[Generation " + generation + "]: score = " + agents[0].score);
            }
            
            
            if (StdDraw.isKeyPressed(KeyEvent.VK_N)) {
                StdDraw.clear();
                agents[0].drawNetwork(0.1f, 0.1f, 0.8f);
                StdDraw.show();
            }
            
            while (StdDraw.isKeyPressed(KeyEvent.VK_P)) {
                int[] map_stuff = new int[4]; //Food x,y; Player x,y;
                for (int i = 0; i < 4; i++)
                    map_stuff[i] = (int)(Math.random()*MAP_SIZE);
                    //map_stuff[i] = (int)(Math.random()*4 + 6*(int)(Math.random()*2));
                //*4 + 6
//                map_stuff[2] = 0;
//                map_stuff[3] = 0;
                
                while (map[map_stuff[1]][map_stuff[0]]) map_stuff[(int)(Math.random()*2)] = (int)(Math.random()*MAP_SIZE);
                while (map[map_stuff[3]][map_stuff[2]]) map_stuff[(int)(Math.random()*2)+2] = (int)(Math.random()*MAP_SIZE);
                 
                
                agents[0].x = map_stuff[2];
                agents[0].y = map_stuff[3];
                
                agents[0].lx = 0;
                agents[0].ly = 0;
                agents[0].reset();
                agents[1].x = map_stuff[2];
                agents[1].y = map_stuff[3];
                
                for (int i = 0; i < TURNS; i++) {
                    StdDraw.clear();
                    
                    drawMap();
                    
                    StdDraw.setPenColor(Color.BLACK);
                    agents[0].move(map_stuff[0], map_stuff[1]);
                    agents[0].draw();
                    agents[1].move(map_stuff[0], map_stuff[1]);
                    agents[1].draw();

                    StdDraw.setPenColor(Color.RED);
                    StdDraw.filledSquare((map_stuff[0]+0.5)/(double)MAP_SIZE, (map_stuff[1]+0.5)/(double)MAP_SIZE, 0.4/(double)MAP_SIZE);
                    
//                    StdDraw.setPenColor(Color.GREEN);
//                    StdDraw.filledSquare((5)/(double)Main.MAP_SIZE, (5)/(double)Main.MAP_SIZE, 1/(double)Main.MAP_SIZE);
                    
                    //agents[0].drawNetwork(0.1f, 0.1f, 0.8f);
                    
                    if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE))
                        agents[0].save("out.txt");

                    StdDraw.show();
                    StdDraw.pause(50);
                }
            }
            
            if (StdDraw.isKeyPressed(KeyEvent.VK_X))
                break;
        }
        
        
        while (true) {
            int[] map_stuff = new int[4]; //Food x,y; Player x,y;
            for (int i = 0; i < 4; i++)
                map_stuff[i] = (int)(Math.random()*MAP_SIZE);
                //map_stuff[i] = (int)(Math.random()*4 + 6*(int)(Math.random()*2));
            
            while (map[map_stuff[1]][map_stuff[0]]) map_stuff[(int)(Math.random()*2)] = (int)(Math.random()*MAP_SIZE);
            
//            map_stuff[2] = 0;
//            map_stuff[3] = 0;
            
            //agents[0].x = map_stuff[2];
            //agents[0].y = map_stuff[3];
            agents[0].lx = 0;
            agents[0].ly = 0;
            agents[0].reset();
            agents[1].x = map_stuff[2];
            agents[1].y = map_stuff[3];
            
            for (int i = 0; i < TURNS; i++) {
                StdDraw.clear();
                
                drawMap();
                
                StdDraw.setPenColor(Color.BLACK);
                agents[0].move(map_stuff[0], map_stuff[1]);
                agents[0].draw();
                agents[1].move(map_stuff[0], map_stuff[1]);
                //agents[1].draw();
                
                StdDraw.setPenColor(Color.RED);
                StdDraw.filledSquare((map_stuff[0]+0.5)/(double)MAP_SIZE, (map_stuff[1]+0.5)/(double)MAP_SIZE, 0.4/(double)MAP_SIZE);
                
//                StdDraw.setPenColor(Color.GREEN);
//                StdDraw.filledSquare((5)/(double)Main.MAP_SIZE, (5)/(double)Main.MAP_SIZE, 1/(double)Main.MAP_SIZE);
                
                if (StdDraw.isKeyPressed(KeyEvent.VK_R)) {
                    agents[0].x = (int)(Math.random()*4 + 6*(int)(Math.random()*2));
                    agents[0].y = (int)(Math.random()*4 + 6*(int)(Math.random()*2));
                    agents[0].reset();
                }
                    
                
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE))
                    agents[0].save("out.txt");
                
                StdDraw.show();
                StdDraw.pause(20);
            }
        }
    }
    
    public static void loadMap(String file) {
        if (!file.contains(".png")) file += ".png";
        Picture pc = new Picture(file);
        System.out.println("[Loading]: " + file + " " + pc.width() + "x" + pc.height());
        map = new boolean[pc.height()][pc.width()];
        for (int i = 0; i < pc.height(); i++) {
            for (int j = 0; j < pc.width(); j++) {
                map[pc.width()-j-1][i] = (pc.get(i, j).getBlue()+pc.get(i, j).getRed()+pc.get(i, j).getGreen())/3 < 127;
            }
        }
    }
    
    public static void drawMap() {
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                if (map[i][j]) {
                    StdDraw.setPenColor(Color.BLUE);
                    StdDraw.filledSquare((j+0.5)/(double)Main.MAP_SIZE, (i+0.5)/(double)Main.MAP_SIZE, 0.5/(double)Main.MAP_SIZE);
                } else if(i%2 == 0 && j%2 == 1) {
                    StdDraw.setPenColor(new Color(240, 240, 240));
                    StdDraw.filledSquare((j+0.5)/(double)Main.MAP_SIZE, (i+0.5)/(double)Main.MAP_SIZE, 0.5/(double)Main.MAP_SIZE);
                } else if(i%2 == 1 && j%2 == 0) {
                    StdDraw.setPenColor(new Color(240, 240, 240));
                    StdDraw.filledSquare((j+0.5)/(double)Main.MAP_SIZE, (i+0.5)/(double)Main.MAP_SIZE, 0.5/(double)Main.MAP_SIZE);
                }
            }
        }
    }
    
    public static int getDist(boolean[][] maze_, int startx, int starty, int endx, int endy) {
        int r = maze_[0].length, c = maze_.length;
        boolean[][] maze = new boolean[r][c];
        boolean[][] solve = new boolean[c][r];
        
        for (int y = 0; y<r; y++)
          for (int x = 0; x<c; x++)
            maze[x][y] = maze_[x][y];
        
        for (int y = 0; y<r; y++)
          for (int x = 0; x<c; x++)
            solve[x][y] = false;
        
        
        ArrayList<Path> paths = new ArrayList<>();
        Path last;
        paths.add(new Path(startx, starty, null));

        //Generate A*
        while(!paths.isEmpty()) {
          Path cur = paths.get(0);
          maze[cur.x][cur.y] = false;
          if (cur.x == endx && cur.y == endy) break;
          if (cur.x < maze_.length-1 && maze[cur.x+1][cur.y]) paths.add(new Path(cur.x+1, cur.y, cur));
          if (cur.x > 0 && maze[cur.x-1][cur.y]) paths.add(new Path(cur.x-1, cur.y, cur));
          if (cur.y < maze_.length-1 && maze[cur.x][cur.y+1]) paths.add(new Path(cur.x, cur.y+1, cur));
          if (cur.y > 0 && maze[cur.x][cur.y-1]) paths.add(new Path(cur.x, cur.y-1, cur));
          paths.remove(0);
        }

        if (paths.isEmpty()) return 0;

        //Mark maze
        Path best = paths.get(0);
        while (best!=null) {
          solve[best.x][best.y] = true;
          best = best.prev;
        }

        return solve.length;
    }
}
class Path {
    int x, y;
    Path prev;
    public Path(int x, int y, Path prev) {
        this.x = x;
        this.y = y;
        this.prev = prev;
    }
}