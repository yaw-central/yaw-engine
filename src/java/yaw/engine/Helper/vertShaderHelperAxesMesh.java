
// This file is generated from `vertShaderHelperAxesMesh.fs` shader program
// Please do not edit directly
package yaw.engine.Helper;

public class vertShaderHelperAxesMesh {
    public final static String SHADER_STRING = "#version 330 core\n\nlayout(location = 0) in vec3 position;\n\nuniform mat4 viewMatrix;\nuniform mat4 modelMatrix;\n\nvoid main() {\n    gl_Position = modelMatrix * viewMatrix * vec4(position,1.0);\n}";
}
