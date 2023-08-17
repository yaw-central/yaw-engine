package yaw.engine.helper;

import yaw.engine.mesh.Material;
import yaw.engine.shader.ShaderCode;
import yaw.engine.shader.ShaderProgram;

public class HelperAxesShaders extends ShaderProgram {
    private ShaderCode gs;
    /**
     * Create a draw helper for axes
     */
    public HelperAxesShaders() {
        super();
    }

    public ShaderCode vertexShader() {
        ShaderCode code = new ShaderCode("330", true)
                .cmt("Uniforms")
                .l("uniform mat4 modelMatrix;")
                .l("uniform mat4 viewMatrix;")
                .l("uniform mat4 projectionMatrix;")
                .l("uniform vec3 center;")
                .beginMain()
                .l("gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(center, 1.0);")
                .endMain()
                ;
        return code;
    }
    public ShaderCode geometryShader() {
        ShaderCode code = new ShaderCode("330", true)
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
        return code;
    }

    public ShaderCode fragmentShader() {
        ShaderCode code = new ShaderCode("330", true)
                .cmt("Inputs")
                .l("in vec3 fColor;")
                .l()
                .cmt("Outputs")
                .l("out vec4 fragColor;")
                .l()
                .beginMain()
                .l("fragColor = vec4(fColor, 1.0);")
                .endMain();
        return code;
    }

    public void init() {
        createVertexShader(vertexShader());
        createGeometryShader(geometryShader());
        createFragmentShader(fragmentShader());

        link();
        createUniform("projectionMatrix");
        createUniform("viewMatrix");
        createUniform("modelMatrix");
        createUniform("center");
    }

}

