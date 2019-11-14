/*
 * Gym.java
 *
 * I pledge my honor that I have abided by the Stevens Honor System.
 * Alex Saltstein, Brandon Soong
 *
 */

package Assignment2;

import java.util.*;
import java.util.concurrent.*;

public class Gym implements Runnable {
    private static final int GYM_SIZE = 30;
    private static final int GYM_REGISTERED_CLIENTS = 10000;
    private Map<WeightPlateSize,Integer> noOfWeightPlates;
    private Set<Integer> clients; // for generating fresh client ids
    private ExecutorService executor;
    //various Semaphores
    private Map<WeightPlateSize, Semaphore> weightSems;
    private Map<ApparatusType, Semaphore> apparatusSems;
    private Semaphore mutexClient = new Semaphore(1);
    private Semaphore mutexWeights = new Semaphore(1);
    private Semaphore mutexPrint = new Semaphore(1);

    //Static final variables that we use alot
    private static final WeightPlateSize SMALL = WeightPlateSize.values()[0];
    private static final WeightPlateSize MEDIUM = WeightPlateSize.values()[1];
    private static final WeightPlateSize LARGE = WeightPlateSize.values()[2];

    //Print
    private void printer(Client c, Exercise e, boolean isBeginning){
        System.out.println((isBeginning ? "Beginning:": "Ending:"));
        System.out.println("Client " + c.getId() + " at " + e);
    }

    public void run(){
        //setup noWeightPlates
        noOfWeightPlates = new HashMap<WeightPlateSize, Integer>();
        noOfWeightPlates.put(SMALL, 110);
        noOfWeightPlates.put(MEDIUM, 90);
        noOfWeightPlates.put(LARGE, 75);
      //Print for testing if it was put correctly
      //System.out.println(noOfWeightPlates.get(WeightPlateSize.SMALL_3KG) + " " + noOfWeightPlates.get(WeightPlateSize.MEDIUM_5KG) + " " +noOfWeightPlates.get(WeightPlateSize.LARGE_10KG));

        //setup clients
        clients = new HashSet<Integer>();

        //setup weightSems
        weightSems = new HashMap<WeightPlateSize, Semaphore>();
        weightSems.put(SMALL, new Semaphore(110));
        weightSems.put(MEDIUM, new Semaphore(90));
        weightSems.put(LARGE, new Semaphore(75));

        //setup ApparatusType
        apparatusSems = new HashMap<ApparatusType, Semaphore>();
        for (ApparatusType a: ApparatusType.values()){
            //for each type of apparatus create a semaphore of size 5
            apparatusSems.put(a,new Semaphore(5));
        }

        //setup executor
        executor = Executors.newFixedThreadPool(GYM_SIZE);

        for (int i = 0; i < GYM_REGISTERED_CLIENTS; i++) {
            executor.execute(new Runnable() {
                public void run(){
                    try{
                        mutexClient.acquire();
                    }catch(InterruptedException ie){
                        ie.printStackTrace();
                    }
                    Client c = Client.generateRandom(clients.size());
                    clients.add(clients.size());
                    mutexClient.release();

                    for (Exercise e: c.getRoutine()){
                        //for each exercise in the clients routine run through it
                        ApparatusType at = e.getAt();
                        Map<WeightPlateSize, Integer> weight = e.getWeight();
                        int duration = e.getDuration();
                        int weightsAvail = 0;
                        while (weightsAvail < 3){
                            //make sure that all three weight types for the exercise are available and that the apparatus is available too
                            try{
                                apparatusSems.get(at).acquire();
                                mutexWeights.acquire();
                            }catch(InterruptedException ie){
                                ie.printStackTrace();
                            }

                            for (Map.Entry<WeightPlateSize,Integer> w: weight.entrySet()){
                                //loop through the neccesary amount of weights and make sure
                                //the weights are actually available
                                if (w.getValue() <= noOfWeightPlates.get(w.getKey()))
                                    weightsAvail++;
                            }
                            if (weightsAvail < 3){
                                apparatusSems.get(at).release();
                                mutexWeights.release();
                                weightsAvail = 0;
                            }
                        }

                        for (Map.Entry<WeightPlateSize,Integer> w: weight.entrySet()){
                            //loop through each weight needed and acquire the amount of
                            //semaphores need for each weight
                            int amount = w.getValue();
                            WeightPlateSize type = w.getKey();
                            for (int j = 0; j < amount; j++){
                                try{
                                    weightSems.get(type).acquire();
                                }catch(InterruptedException ie){
                                    ie.printStackTrace();
                                }
                            }
                            noOfWeightPlates.replace(type, noOfWeightPlates.get(type)-amount);
                        }
                        mutexWeights.release();
                        //print Exercise
                        //Exercise beginning
                        try{
                            mutexPrint.acquire();
                        }catch(InterruptedException ie){
                            ie.printStackTrace();
                        }
                        printer(c, e, true);
                        mutexPrint.release();

                        //do the exercise (sleep)
                        try{
                            Thread.sleep(duration);
                        }catch(InterruptedException ie){
                            ie.printStackTrace();
                        }

                        //exercise ending
                        try{
                            mutexPrint.acquire();
                        }catch(InterruptedException ie){
                            ie.printStackTrace();
                        }
                        printer(c, e, false);
                        mutexPrint.release();
                        //release the Semaphores for apparatusType as well as the weight semaphores
                        //and add the weights back on to the number
                        apparatusSems.get(at).release();
                        try{
                            mutexWeights.acquire();
                        }catch(InterruptedException ie){
                            ie.printStackTrace();
                        }
                        for (Map.Entry<WeightPlateSize,Integer> w: weight.entrySet()){
                            //loop through each weight needed and acquire the amount of
                            //semaphores need for each weight
                            int amount = w.getValue();
                            WeightPlateSize type = w.getKey();
                            for (int j = 0; j < amount; j++){
                                weightSems.get(type).release();
                            }
                            noOfWeightPlates.replace(type, noOfWeightPlates.get(type)+amount);
                        }
                        mutexWeights.release();
                    }
                }});
        }
        //close executor
        executor.shutdown();
    }
}
