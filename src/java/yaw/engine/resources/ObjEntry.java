package yaw.engine.resources;

public abstract class ObjEntry {
    public enum EntryType {
        VERTEX
        , TEXT_COORD
        , NORMAL
        , FACE
        , LINE_COMMENT
        , NO_ENTRY
    }

    private EntryType type;
    private int linepos;

    protected ObjEntry(EntryType type, int linepos) {
        this.type = type;
        this.linepos = linepos;
    }

    public EntryType getType() {
        return type;
    }

    public int getLinepos() {
        return linepos;
    }

    @Override
    public String toString() {
        return type + "{" +
                "linepos=" + linepos +
                " + '}';";
    }
}


class NoEntry extends ObjEntry {
    public NoEntry(int linepos) {
        super(EntryType.NO_ENTRY, linepos);
    }
}

class LineComment extends ObjEntry {
    public String comment;

    public LineComment(String comment, int linepos) {
        super(EntryType.LINE_COMMENT, linepos);
        this.comment = comment;
    }
}

class VertexEntry extends ObjEntry {
    public float x;
    public float y;
    public float z;

    public VertexEntry(float x, float y, float z, int linepos) {
        super(EntryType.VERTEX, linepos);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

