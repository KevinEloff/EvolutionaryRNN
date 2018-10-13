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
public class RecurrentNeuralNetwork {
    protected final int[] shape;
    protected final float[] neurons;
    protected final float[] weights;
    protected final float[] bias;
    
    public RecurrentNeuralNetwork(int[] shape, float[] neurons, float[] weights, float[] bias) {
        this.shape = shape;
        this.neurons = new float[neurons.length];
        this.bias = new float[bias.length];
        this.weights = new float[weights.length];
        
        int i = 0;
        for (float val: neurons) this.neurons[i++] = val;
        
        i = 0;
        for (float val: bias) this.bias[i++] = val;
        
        i = 0;
        for (float val: weights) this.weights[i++] = val;
    }
    public RecurrentNeuralNetwork(int[] shape) {
        this.shape = shape;
        //Initialise neuron matrix and neuron bias matrix
        int sum = shape[shape.length-1];
        for (int i: shape) sum += i;
        neurons = new float[sum];
        bias = new float[sum-shape[0]-shape[shape.length-1]];
        
        //Initialise weight matrix
        sum = shape[shape.length-1]*shape[1];
        for (int i = 0; i < shape.length-1; i++)
            sum += shape[i]*shape[i+1];
        weights = new float[sum];
        
        //Random weight initilisation -1 to 1
        for (int i = 0; i < weights.length; i++)
            weights[i] = (float)(Math.random()*2 - 1);
        for (int i = 0; i < bias.length; i++)
            bias[i] = (float)(Math.random()*2 - 1);
    }
    
    public void reset() {
        //Reset of recurrent neurons
        for (int i = 0; i < shape[shape.length-1]; i++)
            neurons[i] = 0;
    }
    
    public void engage() {
        int pos = shape[shape.length-1];
        for (int i = 0; i < shape.length-1; i++) pos+= shape[i];
        for (int i = 0; i < shape[shape.length-1]; i++) {
            neurons[i] = neurons[pos++];
        }
        
        //Reset values to a bias
        int j = 0;
        for (int i = shape[0] + shape[shape.length-1]; i < neurons.length; i++)
            neurons[i] = bias[j++];
        
        int currentLayer = 0, nextLayerIndex = shape[0] + shape[shape.length-1], w = 0;
        
        for (int i = 0; i < neurons.length-shape[shape.length-1]; i++) {
            if (currentLayer != 0)
                neurons[i] = sigmoid(neurons[i]);
            
            for (j = 0; j < shape[currentLayer+1]; j++)
                neurons[nextLayerIndex+j] += neurons[i] * weights[w++];
            
            if (i == nextLayerIndex - 1) {
                currentLayer++;
                nextLayerIndex += shape[currentLayer];
            }
        }
    }
    
    public void input(float[] inputs) {
        if (inputs.length != shape[0]) return;
        int j = shape[shape.length-1];
        for (float i: inputs) neurons[j++] = i;
    }
    
    public float getWeight(int layer, int index) {
        int pos = shape[shape.length-1];
        for (int i = 0; i<layer; i++) pos += shape[i];
        pos += index;
        return neurons[pos];
    }
    
    public float[] getLayer(int layer) {
        int start = shape[shape.length-1];
        float[] out = new float[shape[layer]];
        for (int i = 0; i<layer; i++) start += shape[i];
        for (int i = 0; i < out.length; i++) out[i] = neurons[start++];
        
        return out;
    }
    
    public float[] getOutputLayer() {
        return getLayer(shape.length-1);
    }
    
    public int getOutputIndex() {
        float[] output = getLayer(shape.length-1);
        int i = -1;
        for (int j = 0; j < output.length; j++)
            if (i == -1 || output[j] > output[i]) i = j;
        return i;
    }
    
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
    
    public void save(String filename) {
        try {
            FileWriter fw = new FileWriter(new File(filename));
            
            for (float w: weights) fw.write(w + ",");
            fw.write("#");
            for (float b: bias) fw.write(b + ",");
            
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(RecurrentNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void load(String filename) {
        try {
            Scanner fs = new Scanner(new File(filename));
            int i = 0;
            String line = fs.nextLine();
            String[] input = line.split("#")[0].split(",");
            while (i<weights.length)
                weights[i] = (float)Double.parseDouble(input[i++]);
            
            i = 0;
            input = line.split("#")[1].split(",");
            while (i<bias.length)
                bias[i] = (float)Double.parseDouble(input[i++]);
            
            fs.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RecurrentNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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
        
        for (int i = 0; i < bias.length; i++) {
            double r = Math.random();
            
            if (r < rate/10) {
                bias[i] = (float)(Math.random()*2 - 1);
            } else if (r < rate*1.1) {
                bias[i] *= (1 + (float)(Math.random()*2-1)*0.05);
                if (bias[i] < -1) weights[i] = -1;
                if (bias[i] > 1) weights[i] = 1;
            }
        }
        //for (float i: bias) if (i != 0) System.out.println(i);
    }
    
    public static float sigmoid(double x) {
        return (float)(1/( 1 + Math.pow(Math.E,(-1*x))));
    }
    
    public RecurrentNeuralNetwork duplicate() {
        return new RecurrentNeuralNetwork(shape, neurons, weights, bias);
    }
    
    @Override
    public String toString() {
        String out = "[" + shape[0];
        for (int i = 1; i < shape.length; i++)
            out += ", " + shape[i];
        
        return out + "]: Neurons = " + neurons.length + ", Weights = " + weights.length;
    }
}
