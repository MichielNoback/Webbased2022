package nl.bioinf.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SequenceAnalaysisUtilsTest {

    @Test
    void getGCpercentage() {
        String seq1 = "GGGGAAAATTTTCCCC";
        double gCpercentage = SequenceAnalaysisUtils.getGCpercentage(seq1);
        assertEquals(50, gCpercentage);

        String seq2 = "ggggggggggtttttgcccc";
        double gCpercentage2 = SequenceAnalaysisUtils.getGCpercentage(seq2);
        assertEquals(75, gCpercentage2, 0E-5);


    }
}