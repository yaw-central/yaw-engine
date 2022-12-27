package test.yaw.unittests;

import yaw.engine.resources.ObjEntry;
import yaw.engine.resources.ObjLoader;

public class ObjLoaderTest {
    public static void testVEntry() {
        String line = "v 1.000000 1.000000 -1.000000";
        ObjEntry entry = ObjLoader.parseLine(1, line);
        if(entry.getType() != ObjEntry.EntryType.VERTEX) {
            throw new TestLib.TestError("Wrong entry type '" + entry.getType() + "': should be 'VERTEX'");
        }
    }

    public static void testAll() {
        testVEntry();
    }

    public static void main(String ... args) {
        testAll();
    }
}
