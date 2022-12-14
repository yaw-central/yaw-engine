package test.yaw.unittests;

import org.joml.Vector2f;
import yaw.engine.mesh.GeomLib;

import java.util.List;

public class GeomLibTest {

    public static boolean almostZero(float x) {
        return Math.abs(x) <= 1.0e-7f;
    }
    public static void testMakeCircleApprox() {
        List<Vector2f> coords = GeomLib.makeCircleApprox(4);
        if(coords.size() != 4) { throw new TestLib.TestError("Wrong size"); }

        if(coords.get(0).x != 1.0f) { throw new TestLib.TestError("Wrong coordinate"); }
        if(!almostZero(coords.get(0).y)) { throw new TestLib.TestError("Wrong coordinate"); }

        if(!almostZero(coords.get(1).x)) { throw new TestLib.TestError("Wrong coordinate"); }
        if(coords.get(1).y != 1.0f) { throw new TestLib.TestError("Wrong coordinate"); }

        if(coords.get(2).x != -1.0f) { throw new TestLib.TestError("Wrong coordinate"); }
        if(!almostZero(coords.get(2).y)) { throw new TestLib.TestError("Wrong coordinate"); }

        if(!almostZero(coords.get(3).x)) { throw new TestLib.TestError("Wrong coordinate"); }
        if(coords.get(3).y != -1.0f) { throw new TestLib.TestError("Wrong coordinate"); }
    }

    public static void testAll() {
        testMakeCircleApprox();
    }

    public static void main(String... args) {
        testAll();
    }
}
