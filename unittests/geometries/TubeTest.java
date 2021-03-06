package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class TubeTest {

    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============
        // tc00: creating a simple tube, and calculating its normal by hand.
        //expect: the two vectors should be equal.
        Tube t = new Tube(new Ray(new Point(0,0,0),new Vector(1,0,0)),1);
        Vector normal1 = t.getNormal(new Point(1,1,0));
        assertEquals(normal1,(new Vector(0,1,0)), "Wrong tube normal");
        // =============== Boundary Values Tests ==================
        Vector normal2 = t.getNormal(new Point(0,1,0));
        assertEquals(normal2,(new Vector(0,1,0)), "Wrong tube normal");

    }

    @Test
    void testFindIntsersections() {
    }
}