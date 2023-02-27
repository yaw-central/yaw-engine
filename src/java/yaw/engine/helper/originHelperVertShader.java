
// This file is generated from `fragShader.fs` shader program
// Please do not edit directly
package yaw.engine.helper;

public class originHelperVertShader {
    public final static String SHADER_STRING = "#version 330 core\n\nlayout(location = 0) in vec3 aPosition;\n\nuniform mat4 modelMatrix;\nuniform mat4 viewMatrix;\nuniform mat4 projectionMatrix;\n\nvoid main() {\ngl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPosition, 1.0);\n}";
}