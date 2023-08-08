package yaw.engine.helper;

import yaw.engine.shader.ShaderCode;
import yaw.engine.shader.ShaderProgram;

public class HelperVerticesShaders extends ShaderProgram {

    public HelperVerticesShaders() {
    }

    public ShaderCode vertexShaderCode() {
        ShaderCode code = new ShaderCode("330", true)
                .cmt("Buffers")
                .l("layout(location = 0) in vec3 position;")
                .l("uniform mat4 projectionMatrix;")
                .l("uniform mat4 viewMatrix;")
                .l("uniform mat4 modelMatrix;")
                .beginMain()
                .l("vec4 mvPos = modelMatrix * vec4(position, 1.0);")
                .l("gl_Position = projectionMatrix * viewMatrix * mvPos;")
                .endMain();
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
        createFragmentShader(fragmentShaderCode());

        link();

        createUniform("projectionMatrix");
        createUniform("viewMatrix");
        createUniform("modelMatrix");
    }
}


