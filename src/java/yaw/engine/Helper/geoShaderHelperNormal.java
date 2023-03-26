
// This file is generated from `geoShaderHelperNormal.fs` shader program
// Please do not edit directly
package yaw.engine.Helper;

public class geoShaderHelperNormal {
    public final static String SHADER_STRING = "#version 330 core\n\nlayout (points) in;\nlayout (line_strip, max_vertices = 2) out;\n\nin vec3 vNormal[];\n\n\nvoid main() {\n\n    gl_Position = gl_in[0].gl_Position;\n    EmitVertex();\n\n    gl_Position = gl_in[0].gl_Position + vec4(vNormal[0], 0.0) * 0.1;\n    EmitVertex();\n\n    EndPrimitive();\n}\n\n";
}
