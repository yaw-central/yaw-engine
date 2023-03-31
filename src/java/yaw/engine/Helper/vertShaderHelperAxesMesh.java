
// This file is generated from `vertShaderHelperAxesMesh.fs` shader program
// Please do not edit directly
package yaw.engine.Helper;

public class vertShaderHelperAxesMesh {
    public final static String SHADER_STRING = "#version 330 core\n\nuniform mat4 modelMatrix;\nuniform mat4 viewMatrix;\nuniform mat4 projectionMatrix;\nuniform vec3 center;\n\n\n\nvoid main() {\n        gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(center, 1.0);\n}";
}
