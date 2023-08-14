package yaw.engine.shader;

import javax.management.remote.rmi.RMIConnectionImpl;

public class ShaderCode {
    public static final int INDENT_SPACES = 2;

    public StringBuilder code;
    public StringBuilder cLayout;
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
        code.append("/* "); code.append(comment); code.append(" */\n");
        return this;
    }

    public ShaderCode l(String line) {
        mkIndent();
        code.append(line);
        if (!line.endsWith(";")) {
            code.append(";");
        }
        code.append("\n");
        return this;
    }

    public ShaderCode l() {
        code.append("\n");
        return this;
    }

    public ShaderCode beginBlock(){
        mkIndent();
        indent();
        code.append("{\n");
        return this;
    }

    public ShaderCode endBlock(){
        dedent();
        mkIndent();
        code.append("}\n");
        return this;
    }

    public ShaderCode beginStruct(String structName, String cmt) {
        if (cmt != null && !cmt.equals("")) {
            mkIndent();
            code.append("/* "); code.append(cmt); code.append(" */\n");
        }
        mkIndent();
        code.append("struct "); code.append(structName); code.append("\n");
        beginBlock();
        return this;
    }

    public ShaderCode beginStruct(String structName) {
        return beginStruct(structName, "");
    }

    public ShaderCode item(String itemType, String itemName, String itemCmt) {
        if (itemCmt != null && !itemCmt.equals("")) {
            mkIndent();
            code.append("// "); code.append(itemCmt); code.append("\n");
        }
        mkIndent();
        code.append(itemType); code.append(' '); code.append(itemName); code.append(";\n");
        return this;
    }

    public ShaderCode item(String itemType, String itemName) {
        return item(itemType, itemName, "");
    }

    public ShaderCode endStruct() {
        return endBlock();
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