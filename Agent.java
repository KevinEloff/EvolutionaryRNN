import java.awt.event.KeyEvent;

/**
 *
 * @author Kevin
 */
public class Agent extends NeuralNetwork {
    int x, y;
    int moves;
    double score;

    public Agent(int[] shape, float[] neurons, float[] weights) {
        super(shape, neurons, weights);
    }
    public Agent(int[] shape) {
        super(shape);
    }

    public double getScore(int food_x, int food_y) {
        //return (double)Math.sqrt(Math.pow(x - food_x, 2) + Math.pow(y - food_y, 2))/moves;
        return (double)Math.sqrt(Math.pow(x - food_x, 2) + Math.pow(y - food_y, 2))/(Main.TURNS+1-(double)moves*0.2);
        //return (double)(Math.pow(x - food_x, 2) + Math.pow(y - food_y, 2))*Math.pow(1.01, moves);
    }

    public void move(int food_x, int food_y) {
        if (x == food_x && y == food_y) return;
        //float[] state = {x-MAP_SIZE/2, y-MAP_SIZE/2, food_x-MAP_SIZE/2, food_y-MAP_SIZE/2};
        float[] state = {x-food_x, y-food_y};
        for (int i = 0; i < state.length; i++) 
            state[i] /= (float)Main.MAP_SIZE;
        input(state);
        engage();
        int move = getOutputIndex();

        if (StdDraw.isKeyPressed(KeyEvent.VK_O))
            move = getOutput();

        switch (move) {
            case 0: if (y < Main.MAP_SIZE-1) y++;
                moves++; break;
            case 1: if (y > 0) y--;
                moves++; break;
            case 2: if (x < Main.MAP_SIZE-1) x++;
                moves++; break;
            case 3: if (x > 0) x--;
                moves++; break;
            //4 do nothing                    //4 do nothing
        }
    }

    public void draw() {
        StdDraw.filledSquare((x+0.5)/(double)Main.MAP_SIZE, (y+0.5)/(double)Main.MAP_SIZE, 0.5/(double)Main.MAP_SIZE);
    }
    
    @Override
    public Agent duplicate() {
        return new Agent(shape, neurons, weights);
    }
}
