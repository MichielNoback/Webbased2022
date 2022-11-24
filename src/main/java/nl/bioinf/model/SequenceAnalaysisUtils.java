package nl.bioinf.model;

import javax.sound.midi.Sequence;

public class SequenceAnalaysisUtils {
    public static double getGCpercentage(String sequence) {
        if (sequence == null || sequence.isEmpty()) {
            throw new IllegalArgumentException("No sequence");
        }
        sequence = sequence.toUpperCase();
        int gcCount = 0;
        for (char n: sequence.toCharArray()) {
            if (n == 'C' || n == 'G') {
                gcCount++;
            }
        }
        double percentage = (double)gcCount / sequence.length() * 100;
        return percentage;
    }
}
