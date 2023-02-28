
// This file is generated from `originHelperVertShader.fs` shader program
// Please do not edit directly
package yaw.engine.helper;

public class originHelperVertShader {
    public final static String SHADER_STRING = "#version 330 core\n\nlayout(location = 0) in vec3 position;\n\nuniform mat4 modelMatrix;\nuniform mat4 viewMatrix;\nuniform mat4 projectionMatrix;\n\nvoid main() {\n    vec4 mvPos = modelMatrix * vec4(position,1);\n    gl_Position = projectionMatrix * viewMatrix * modelMatrix * mvPos;\n}";
}
