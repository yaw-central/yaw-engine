package yaw.engine.Helper;

import yaw.engine.shader.ShaderCode;
import yaw.engine.shader.ShaderProgram;

public class ShaderProgramHelperAxesMesh extends ShaderProgram{
    private ShaderCode gs;
    /**
     * Constructor throws exception if the program could not create the shader
     *
     * @throws Exception the exception
     */
    public ShaderProgramHelperAxesMesh() throws Exception {
        super();
        vs = new ShaderCode("330", true)
                .cmt("Uniforms")
                .l("uniform mat4 modelMatrix;")
                .l("uniform mat4 viewMatrix;")
                .l("uniform mat4 projectionMatrix;")
                .l("uniform vec3 center;")
                .beginMain()
                    .l("gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(center, 1.0);")
                .endMain()
        ;
        gs = new ShaderCode("330", true)
                .cmt("Buffers")
                .l("layout (lines) in;")
                .l("layout (line_strip, max_vertices = 4) out;")
                .l()
                .cmt("Uniforms")
                .l("uniform vec3 center;")
                .l()
                .cmt("Outputs")
                .l("out vec3 fColor;")
                .l()
                .beginMain()
                    .l("fColor = vec3(1.0,0.0,0.0);")
                    .l("gl_Position = vec4(center,0.0);")
                    .l("EmitVertex();")
                    .l("gl_Position = (gl_in[0].gl_Position + vec4(1.0, 0.0, 0.0, 0.0));")
                    .l("EmitVertex();")
                    .l("EndPrimitive();")
                    .l()
                    .l("fColor = vec3(0.0,1.0,0.0);")
                    .l("gl_Position = vec4(center,0.0);")
                    .l("EmitVertex();")
                    .l("gl_Position = (gl_in[0].gl_Position + vec4(0.0, 1.0, 0.0, 0.0));")
                    .l("EmitVertex();")
                    .l("EndPrimitive();")
                    .l()
                    .l("fColor = vec3(0.0,0.0,1.0);")
                    .l("gl_Position = vec4(center,0.0);")
                    .l("EmitVertex();")
                    .l("gl_Position = gl_in[0].gl_Position + vec4(0.0, 0.0, 1.0, 0.0) * 2 ;")
                    .l("EmitVertex();")
                    .l("EndPrimitive();")
                .endMain();
        fs = new ShaderCode("330", true)
                .cmt("Inputs")
                .l("in vec3 fColor;")
                .l()
                .cmt("Outputs")
                .l("out vec4 fragColor;")
                .l()
                .beginMain()
                    .l("fragColor = vec4(fColor, 1.0);")
                .endMain();

    }

    public String getGs() {
        return gs.code.toString();
    }
}

