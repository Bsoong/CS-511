/*
 * Exercise.java
 *
 * I pledge my honor that I have abided by the Stevens Honor System.
 * Alex Saltstein, Brandon Soong
 *
 */

package Assignment2;

import java.util.*;

public class Exercise {
    private ApparatusType at;
    private Map<WeightPlateSize,Integer> weight;
    private int duration;

    public Exercise(ApparatusType at, Map<WeightPlateSize,Integer> weight, int duration){
        this.at = at;
        this.weight = weight;
        this.duration = duration;
    }

    public ApparatusType getAt(){
        return at;
    }

    public Map<WeightPlateSize, Integer> getWeight(){
        return weight;
    }

    public int getDuration(){
        return duration;
    }

    public String toString()
    {
        return "exercise with " + getAt() + " with weight " + getWeight() + " for " + getDuration() + " seconds.";
    }

    public static Exercise generateRandom(){
        Map<WeightPlateSize, Integer> w = new HashMap<WeightPlateSize, Integer>();
        Random r = new Random();
        int[] amountWeights = {
            r.nextInt(11),
            r.nextInt(11),
            r.nextInt(11)
        };
        //Should never be 0
        while((amountWeights[0] + amountWeights[1] + amountWeights[2]) == 0){
            amountWeights[0] = r.nextInt(11);
            amountWeights[1] = r.nextInt(11);
            amountWeights[2] = r.nextInt(11);
        }
        w.put(WeightPlateSize.values()[0],amountWeights[0]);
        w.put(WeightPlateSize.values()[1],amountWeights[1]);
        w.put(WeightPlateSize.values()[2],amountWeights[2]);
        return new Exercise(ApparatusType.rand(), w, r.nextInt(100)+5);
    }
}
