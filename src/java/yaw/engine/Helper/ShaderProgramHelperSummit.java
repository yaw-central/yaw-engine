package yaw.engine.Helper;

import yaw.engine.mesh.Material;
import yaw.engine.shader.ShaderCode;
import yaw.engine.shader.ShaderProgram;

public class ShaderProgramHelperSummit extends ShaderProgram {

    public ShaderProgramHelperSummit() throws Exception {
        super();
        vs = new ShaderCode("330", true)
                .cmt("Buffers")
                .l("layout(location = 0) in vec3 position;")
                .l("uniform mat4 projectionMatrix;")
                .l("uniform mat4 viewMatrix;")
                .l("uniform mat4 modelMatrix;")
                .beginMain()
                    .l("vec4 mvPos = modelMatrix * vec4(position, 1.0);")
                    .l("gl_Position = projectionMatrix * viewMatrix * mvPos;")
                .endMain();

        fs = new ShaderCode("330", true)
                .cmt("Outputs")
                .l("out vec4 fragColor;")
                .l()
                .beginMain()
                    .l("fragColor = vec4(0.0, 1.0, 0.0, 1.0);")
                .endMain();
    }
}


