
// This file is generated from `geoShaderHelperAxesMesh.fs` shader program
// Please do not edit directly
package yaw.engine.Helper;

public class geoShaderHelperAxesMesh {
    public final static String SHADER_STRING = "#version 330 core\n\nlayout (lines) in;\nlayout (line_strip, max_vertices = 4) out;\n\nout vec3 fColor;\n\nvoid main()\n{\n    fColor = vec3(1.0,0.0,0.0);\n    gl_Position = gl_in[0].gl_Position;\n    EmitVertex();\n    gl_Position = (gl_in[0].gl_Position + vec4(1.0, 0.0, 0.0, 0.0));\n    EmitVertex();\n    EndPrimitive();\n\n    fColor = vec3(0.0,1.0,0.0);\n    gl_Position = gl_in[0].gl_Position;\n    EmitVertex();\n    gl_Position = gl_in[0].gl_Position + vec4(0.0, 1.0, 0.0, 0.0);\n    EmitVertex();\n    EndPrimitive();\n\n    fColor = vec3(0.0,0.0,1.0);\n    gl_Position = gl_in[0].gl_Position;\n    EmitVertex();\n    gl_Position = gl_in[0].gl_Position + vec4(0.0, 0.0, 1.0, 0.0) ;\n    EmitVertex();\n    EndPrimitive();\n}";
}
