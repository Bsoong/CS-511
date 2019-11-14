/*
 * ApparatusType.java
 *
 * I pledge my honor that I have abided by the Stevens Honor System.
 * Alex Saltstein, Brandon Soong
 *
 */

package Assignment2;

import java.util.Random;

public enum ApparatusType {
    LEGPRESSMACHINE,
    BARBELL,
    HACKSQUATMACHINE,
    LEGEXTENSIONMACHINE,
    LEGCURLMACHINE,
    LATPULLDOWNMACHINE,
    PECDECKMACHINE,
    CABLECROSSOVERMACHINE;

    public static ApparatusType rand(){
        int r = new Random().nextInt(ApparatusType.values().length);
        return ApparatusType.values()[r];
    }
}

