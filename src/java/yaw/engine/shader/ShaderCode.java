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
        code.append(" ".repeat(indentLevel * INDENT_SPACES));
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

    public ShaderCode l() {
        code.append("\n");
        return this;
    }

    public ShaderCode beginMain() {
        mkIndent();
        code.append("void main()\n");
        mkIndent();
        code.append("{\n");
        indent();
        return this;
    }

    public ShaderCode endMain() {
        dedent();
        mkIndent();
        code.append("}\n");
        return this;
    }

    @Override
    public String toString() {
        return code.toString();
    }
}
