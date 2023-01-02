package test.yaw.unittests;

import clojure.lang.Obj;
import yaw.engine.resources.ObjEntry;
import yaw.engine.resources.ObjLoader;

public class ObjLoaderTest {
    public static void testEntry() {
        ObjLoader loader = new ObjLoader();
        String line = "v 1.000000 1.000000 -1.000000";
        ObjEntry entry = loader.parseLine(1, line);
        if(entry.getType() != ObjEntry.EntryType.VERTEX) {
            throw new TestLib.TestError("Wrong entry type '" + entry.getType() + "': should be 'VERTEX'");
        }
        line = "vn 0.0000 1.0000 0.0000";
        entry = loader.parseLine(1, line);
        if(entry.getType() != ObjEntry.EntryType.NORMAL) {
            throw new TestLib.TestError("Wrong entry type '" + entry.getType() + "': should be 'NORMAL'");
        }
        line = "vt 0.375000 0.000000";
        entry = loader.parseLine(1, line);
        if(entry.getType() != ObjEntry.EntryType.TEXT_COORD) {
            throw new TestLib.TestError("Wrong entry type '" + entry.getType() + "': should be 'TEXT_COORD'");
        }
        line = "f 1/1/1 2/2/2 3/3/3";
        entry = loader.parseLine(1, line);
        if(entry.getType() != ObjEntry.EntryType.FACE) {
            throw new TestLib.TestError("Wrong entry type '" + entry.getType() + "': should be 'FACE'");
        }
        line = "f 1/1 2/2 3/3";
        entry = loader.parseLine(1, line);
        if(entry.getType() != ObjEntry.EntryType.FACE) {
            throw new TestLib.TestError("Wrong entry type '" + entry.getType() + "': should be 'FACE'");
        }

    }

    public static void testAll() {
        testEntry();
    }

    public static void main(String ... args) {
        testAll();
    }
}
