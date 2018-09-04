package se.kth.integral.mecenat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.kth.integral.mecenat.model.MecenatCSVRecord;

public class MecenatCSVRecordTest {
    @Test
    public void studentUIDConversionTest() {
        String uid = "801cc908-5c18-11e7-82f8-4a99985b4246";
        assertEquals(uid, MecenatCSVRecord.guidByteArrayToString(MecenatCSVRecord.guidStringToByteArray(uid)));
    }
}
