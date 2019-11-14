/*
 * Client.java
 *
 * I pledge my honor that I have abided by the Stevens Honor System.
 * Alex Saltstein, Brandon Soong
 *
 */

package Assignment2;

import java.util.*;

public class Client {
    private int id;
    private List<Exercise> routine;
    
    public Client(int id){
        this.id = id;
        this.routine = new Vector<Exercise>();
    }
    
    public int getId(){
        return id;
    }
    
    public List<Exercise> getRoutine(){
        return routine;
    }
    
    public void addExercise(Exercise e){
        routine.add(e);
    }
    
    public static Client generateRandom(int id){
        Client c = new Client(id);
        int numExercises = (int)((Math.random() * 6) + 15);
        for (int i = 0; i < numExercises; i++){
            c.addExercise(Exercise.generateRandom());
        }
        return c;
    }
}
