
// This file is generated from `vertShaderHelperSummit.fs` shader program
// Please do not edit directly
package yaw.engine.Helper;

public class vertShaderHelperSummit {
    public final static String SHADER_STRING = "#version 330 core\n\nlayout(location = 0) in vec3 position;\n\n\nuniform mat4 projectionMatrix;\nuniform mat4 viewMatrix;\nuniform mat4 modelMatrix;\n\n\nvoid main() {\n    vec4 mvPos = modelMatrix * vec4(position, 1.0);\n    gl_Position = projectionMatrix * viewMatrix * mvPos;\n}";
}
