package yaw.engine.resources;

public abstract class ObjEntry {
    public enum EntryType {
        VERTEX
        , TEXT_COORD
        , NORMAL
        , FACE
        , LINE_COMMENT
        , UNSUPPORTED
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

class NormalEntry extends ObjEntry {
    public float nx;
    public float ny;
    public float nz;

    public NormalEntry(float nx, float ny, float nz, int linepos) {
        super(EntryType.NORMAL, linepos);
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
    }
}

class TextureEntry extends ObjEntry {
    public float tx;
    public float ty;

    public TextureEntry(float tx, float ty, int linepos) {
        super(EntryType.TEXT_COORD, linepos);
        this.tx = tx;
        this.ty = ty;
    }
}

class FaceEntry extends ObjEntry {
    FaceVertex[] face;

    public FaceEntry(FaceVertex[] face, int linepos) {
        super(EntryType.FACE, linepos);
        this.face = face;
    }
}

class FaceVertex {
    public int vertexId;
    public int textId;
    public int normId;

    public FaceVertex(int vertexId, int textId, int normId) {
        // remark : Id=0 means no id
        this.vertexId = vertexId;
        this.textId = textId;
        this.normId = normId;
    }
}

class UnsupportedEntry extends ObjEntry {
    public String keyword;
    public String line;

    public UnsupportedEntry(String keyword, String line, int linepos) {
        super(EntryType.UNSUPPORTED, linepos);
        this.keyword = keyword;
        this.line = line;
    }
}
