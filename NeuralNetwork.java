import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin
 */
public class NeuralNetwork {
    protected final int[] shape;
    protected final float[] neurons;
    protected final float[] weights;
    
    //Used for duplicate() method
    public NeuralNetwork(int[] shape, float[] neurons, float[] weights) {
        this.shape = shape;
        this.neurons = new float[neurons.length];
        int i = 0;
        for (float val: neurons) this.neurons[i++] = val;
        this.weights = new float[weights.length];
        i = 0;
        for (float val: weights) this.weights[i++] = val;
    }
    /**
     * Create a new neural network from a given shape.
     * @param shape Shape of neural network per layer
     */
    public NeuralNetwork(int[] shape) {
        this.shape = shape;
        //initialise neuron matrix
        int sum = 0;
        for (int i: shape) sum += i;
        neurons = new float[sum];
        
        //initialise weight matrix
        sum = 0;
        for (int i = 0; i < shape.length-1; i++)
            sum += shape[i]*shape[i+1];
        weights = new float[sum];
        
        //Random weight init -1 to 1
        for (int i = 0; i < sum; i++)
            weights[i] = (float)(Math.random()*2 - 1);
    }
    
    /**
     * Do weight calculations on the neural network
     */
    public void engage() {
        //Reset values
        for (int i = shape[0]; i < neurons.length; i++)
            neurons[i] = 0;
        
        int currentLayer = 0, nextLayerIndex = shape[0], w = 0;
        
        for (int i = 0; i < neurons.length-shape[shape.length-1]; i++) {
            if (currentLayer != 0)
                neurons[i] = sigmoid(neurons[i]);
            
            for (int j = 0; j < shape[currentLayer+1]; j++)
                neurons[nextLayerIndex+j] += neurons[i] * weights[w++];
            
            if (i == nextLayerIndex - 1) {
                currentLayer++;
                nextLayerIndex += shape[currentLayer];
            }
        }
    }
    
    /**
     * Set input state for processing
     * @param inputs Array of inputs of range (-1, 1)
     */
    public void input(float[] inputs) {
        if (inputs.length != shape[0]) return;
        int j = 0;
        for (float i: inputs) neurons[j++] = i;
    }
    
    /**
     * Get specific weight at layer/index
     */
    public float getWeight(int layer, int index) {
        int pos = 0;
        for (int i = 0; i<layer; i++) pos += shape[i];
        pos += index;
        return neurons[pos];
    }

    public float[] getLayer(int layer) {
        int start = 0;
        float[] out = new float[shape[layer]];
        for (int i = 0; i<layer; i++) start += shape[i];
        for (int i = 0; i < out.length; i++) out[i] = neurons[start++];
        
        return out;
    }
    
    /**
     * Get the output layer of the neural network
     * @return Array of output values
     */
    public float[] getOutputLayer() {
        return getLayer(shape.length-1);
    }
    
    /**
     * Get the index of highest value output
     * @return index of best output
     */
    public int getOutputIndex() {
        float[] output = getLayer(shape.length-1);
        int i = -1;
        for (int j = 0; j < output.length; j++)
            if (i == -1 || output[j] > output[i]) i = j;
        return i;
    }
    
    /**
     * Get output using softmax probability
     * @return randomised output
     */
    public int getOutput() {
        double sum = 0, r = Math.random();
        float[] output = getLayer(shape.length-1);
        for (int i = 0; i < output.length; i++) output[i] = Math.abs(output[i]);
        for (float i: output) sum += (double)i;
        for (int i = 0; i < output.length; i++)
            output[i] /= sum;
        int i = 0;
        sum = output[i];
        while (sum < r) sum += output[++i];
        i--;
        return i;
    }
    
    /**
     * Save weight matrix and bias to file
     * @param filename Path of file to be saved
     */
    public void save(String filename) {
        try {
            FileWriter fw = new FileWriter(new File(filename));
            
            for (float w: weights) fw.write(w + ",");
            
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Load weight matrix and bias to network
     * @param filename Path of file to be loaded
     */
    public void load(String filename) {
        try {
            Scanner fs = new Scanner(new File(filename));
            int i = 0;
            
            String[] input = fs.nextLine().split(",");
            while (i<weights.length) {
                weights[i] = (float)Double.parseDouble(input[i++]);
            }
            fs.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Mutate weight matrix and bias
     */
    public void mutate(float rate) {
        for (int i = 0; i < weights.length; i++) {
            double r = Math.random();
            
            if (r < rate/10) {
                weights[i] = (float)(Math.random()*2 - 1);
            } else if (r < rate*1.1) {
                weights[i] *= (1 + (float)(Math.random()*2-1)*0.05);
                if (weights[i] < -1) weights[i] = -1;
                if (weights[i] > 1) weights[i] = 1;
            }
        }
    }

    /**
     * Activation function
     */
    public static float sigmoid(double x) {
        return (float)(1/( 1 + Math.pow(Math.E,(-1*x))));
    }
    
    /**
     * Create duplicated instance of neural network
     * @return Duplicate RecurrentNeuralNetwork
     */
    public NeuralNetwork duplicate() {
        return new NeuralNetwork(shape, neurons, weights);
    }
    
    @Override
    public String toString() {
        String out = "[" + shape[0];
        for (int i = 1; i < shape.length; i++)
            out += ", " + shape[i];
        
        return out + "]: Neurons = " + neurons.length + ", Weights = " + weights.length;
    }
}
