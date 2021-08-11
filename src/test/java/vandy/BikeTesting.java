package vandy;

import org.junit.*;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;
import java.util.Iterator;
import com.serverless.LocationServices.GeoHash;
import com.serverless.LocationServices.OpenStreetMapUtils;

public class BikeTesting {
	
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void test_Constructor() {
    	
    	GeoHash.getInstance();
    	
        assertEquals("100110100110101100100111000111010101111011010000001111000100",  GeoHash.toHashString(GeoHash.geohash(38.897699700000004, -77.03655315, 60)));

    }
    

}

