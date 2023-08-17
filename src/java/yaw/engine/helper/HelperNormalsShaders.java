package yaw.engine.helper;

import yaw.engine.mesh.Material;
import yaw.engine.shader.ShaderCode;
import yaw.engine.shader.ShaderProgram;

public class HelperNormalsShaders extends ShaderProgram {
    private ShaderCode gs;
    public HelperNormalsShaders() {
    }

    public ShaderCode vertexShaderCode() {
        ShaderCode code = new ShaderCode("330", true)
                .cmt("Buffers")
                .l("layout(location = 0) in vec3 position;")
                .l("layout(location = 2) in vec3 normal;")
                .l()
                .cmt("Uniforms")
                .l("uniform mat4 projectionMatrix;")
                .l("uniform mat4 viewMatrix;")
                .l("uniform mat4 modelMatrix;")
                .l()
                .cmt("Outputs")
                .l("out vec3 vNormal;\n")
                .l()
                .beginMain()
                .l("vec4 mvPos = modelMatrix * vec4(position, 1.0);")
                .l("gl_Position = projectionMatrix * viewMatrix * mvPos;")
                .l("vNormal = normalize(transpose(inverse(mat3(modelMatrix))) * normal);")
                .endMain()
                ;
        return code;
    }
    public ShaderCode geometryShaderCode() {
        ShaderCode code = new ShaderCode("330", true)
                .cmt("Buffers")
                .l("layout (points) in;")
                .l("layout (line_strip, max_vertices = 2) out;\n")
                .l()
                .cmt("Inputs")
                .l("in vec3 vNormal[];")
                .l()
                .beginMain()
                .l("gl_Position = gl_in[0].gl_Position;")
                .l("EmitVertex();")
                .l("gl_Position = gl_in[0].gl_Position + vec4(vNormal[0], 0.0) * 0.1 * vec4(1.0, 1.0, -1.0, 1.0);")
                .l("EmitVertex();")
                .l("EndPrimitive();")
                .endMain()
                ;
        return code;
    }

    public ShaderCode fragmentShaderCode() {
        ShaderCode code = new ShaderCode("330", true)
                .cmt("Outputs")
                .l("out vec4 fragColor;")
                .l()
                .beginMain()
                .l("fragColor = vec4(0.0, 1.0, 0.0, 1.0);")
                .endMain();
        return code;
    }

    public void init() {
        createVertexShader(vertexShaderCode());
        createGeometryShader(geometryShaderCode());
        createFragmentShader(fragmentShaderCode());

        link();

        createUniform("projectionMatrix");
        createUniform("viewMatrix");
        createUniform("modelMatrix");
    }

}


