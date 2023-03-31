
// This file is generated from `vertShaderHelperNormal.fs` shader program
// Please do not edit directly
package yaw.engine.Helper;

public class vertShaderHelperNormal {
    public final static String SHADER_STRING = "#version 330 core\n\nlayout(location = 0) in vec3 position;\nlayout(location = 2) in vec3 normal;\n\nuniform mat4 projectionMatrix;\nuniform mat4 viewMatrix;\nuniform mat4 modelMatrix;\n\nout vec3 vNormal;\n\n\nvoid main() {\n    vec4 mvPos = modelMatrix * vec4(position, 1.0);\n    gl_Position = projectionMatrix * viewMatrix * mvPos;\n    vNormal = normalize(transpose(inverse(mat3(modelMatrix))) * normal);\n}";
}
