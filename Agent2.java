import java.awt.Color;
import java.awt.event.KeyEvent;

/**
 *
 * @author Kevin
 */
public class Agent2 extends RecurrentNeuralNetwork {
    int x, y;
    int lx, ly;
    int moves;
    double score;

    public Agent2(int[] shape, float[] neurons, float[] weights, float[] bias) {
        super(shape, neurons, weights, bias);
    }
    public Agent2(int[] shape, String filename) {
        super(shape);
        load(filename);
    }
    public Agent2(int[] shape) {
        super(shape);
    }

    public double getScore(int food_x, int food_y) {
        //Use with >
        //return (double)Math.sqrt(Math.pow(x - food_x, 2) + Math.pow(y - food_y, 2))/moves;
//        return (double)Math.sqrt(Math.pow(x - food_x, 2) + Math.pow(y - food_y, 2))/(Main.TURNS+1-(double)moves*0.2);
        //return (double)(Math.pow(x - food_x, 2) + Math.pow(y - food_y, 2))*Math.pow(1.01, moves);
        
        //Use with <
        return (Main.TURNS+1-(double)(Math.abs(x - food_x) + Math.abs(y - food_y))) * Math.pow(0.95, moves);
        //return (Main.TURNS+1-(double)(Main.getDist(Main.map, x, y, food_x, food_y))) * Math.pow(0.95, moves);
    }

    public void move(int food_x, int food_y) {
        if (x == food_x && y == food_y) return;
        //float[] state = {x-Main.MAP_SIZE/2, y-Main.MAP_SIZE/2, food_x-Main.MAP_SIZE/2, food_y-Main.MAP_SIZE/2};
        //float[] state = {x-food_x, y-food_y};
        //float[] state = {x-food_x, y-food_y, 0, 0, 0, 0, lx, ly};
        float[] state = {x-food_x, y-food_y, 0, 0, 0, 0};
        if (y == Main.MAP_SIZE-1 || Main.map[y+1][x]) state[2] = 1;
        if (y == 0 || Main.map[y-1][x]) state[3] = 1;
        if (x == Main.MAP_SIZE-1 || Main.map[y][x+1]) state[4] = 1;
        if (x == 0 || Main.map[y][x-1]) state[5] = 1;
        
        for (int i = 0; i < 2; i++) 
            state[i] /= (float)Main.MAP_SIZE;
        input(state);
        engage();
        int move = getOutputIndex();

        if (StdDraw.isKeyPressed(KeyEvent.VK_O))
            move = getOutput();
        lx = 0;
        ly = 0;
        switch (move) {
            case 0: 
                if (y < Main.MAP_SIZE-1 && !outOfBounds(move)) y++;
                ly = 1;
                moves++; 
                break;
            case 1: 
                if (y > 0 && !outOfBounds(move)) y--;
                ly = -1;
                moves++; 
                break;
            case 2: //LEFT
                if (x < Main.MAP_SIZE-1 && !outOfBounds(move)) x++;
                lx = 1;
                moves++; 
                break;
            case 3: //RIGHT
                if (x > 0 && !outOfBounds(move)) x--;
                lx = -1;
                moves++; 
                break;
            //4 do nothing                    //4 do nothing
        }
    }

    public void draw() {
        StdDraw.filledSquare((x+0.5)/(double)Main.MAP_SIZE, (y+0.5)/(double)Main.MAP_SIZE, 0.5/(double)Main.MAP_SIZE);
    }

    @Override
    public Agent2 duplicate() {
        return new Agent2(shape, neurons, weights, bias);
    }
    
    public boolean outOfBounds(int dir) {
        switch (dir) {
            case 0: // UP
                if (y < Main.MAP_SIZE-1) 
                    if (Main.map[y+1][x]) return true;
                break;
            case 1: // DOWN
                if (y > 0) 
                    if (Main.map[y-1][x]) return true;
                break;
            case 2: // RIGHT
                if (x < Main.MAP_SIZE-1) 
                    if (Main.map[y][x+1]) return true;
                break;
            case 3: // LEFT
                if (x > 0) 
                    if (Main.map[y][x-1]) return true;
                break;
        }
        return false;
    }
    
   public void drawNetwork(float x, float y, float scale) {
       int w = 0;
       for (int i = 0; i < shape.length; i++) {
           
           for (int j = 0; j < shape[i]; j++) {
//               StdDraw.filledCircle(x + scale/shape.length*i, y + scale/shape[i]*(j-shape[i]/2) + scale/2, scale/50);
               StdDraw.setPenColor(Color.BLACK);
               StdDraw.filledCircle(x + (scale/shape.length)*(i), y + scale/2 + (scale/(double)shape[i])*(j-shape[i]/2+0.5), scale/100);
               if (i != shape.length-1) {
                   for (int k = 0; k < shape[i+1]; k++) {
                        if (weights[w] < -0.2) {
                            StdDraw.setPenColor(new Color(0, 1-Math.abs(weights[w]), 1-Math.abs(weights[w])));
                            StdDraw.line(x + (scale/shape.length)*(i), y + scale/2 + (scale/(double)shape[i])*(j-shape[i]/2+0.5),
                                x + (scale/shape.length)*(i+1), y + scale/2 + (scale/(double)shape[i+1])*(k-shape[i+1]/2+0.5));
                        } else if (weights[w] > 0.2) {
                            StdDraw.setPenColor(new Color(1-Math.abs(weights[w]), 0, 1-Math.abs(weights[w])));
                            StdDraw.line(x + (scale/shape.length)*(i), y + scale/2 + (scale/(double)shape[i])*(j-shape[i]/2+0.5),
                                x + (scale/shape.length)*(i+1), y + scale/2 + (scale/(double)shape[i+1])*(k-shape[i+1]/2+0.5));
                        }
                        w++;
                   }
               }
           }
       }
   }
}
