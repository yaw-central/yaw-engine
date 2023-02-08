package yaw.engine.shader;

public class ShaderCode {
    public static final int INDENT_SPACES = 2;

    public StringBuilder code;
    public int indentLevel;

    public ShaderCode(String version, boolean coreProfile) {
        code = new StringBuilder();
        code.append("#version ");
        code.append(version);
        if(coreProfile) {
            code.append(" core");
        }
        code.append("\n");
    }

    public void mkIndent() {
        for(int i = 1; i < indentLevel * INDENT_SPACES; i++) {
            code.append(' ');
        }
    }

    public ShaderCode indent() {
        indentLevel += 1;
        return this;
    }

    public ShaderCode dedent() {
        indentLevel -= 1;
        return this;
    }

    public ShaderCode cmt(String comment) {
        mkIndent();
        code.append("/* "); code.append(comment); code.append("*/\n");
        return this;
    }

    public ShaderCode l(String line) {
        mkIndent();
        code.append(line);
        code.append("\n");
        return this;
    }

    @Override
    public String toString() {
        return code.toString();
    }
}
