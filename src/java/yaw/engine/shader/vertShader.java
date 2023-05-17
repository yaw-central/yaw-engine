
// This file is generated from `vertShader.fs` shader program
// Please do not edit directly
package yaw.engine.shader;

public class vertShader {
    public final static String SHADER_STRING = "#version 330 core\n\nlayout(location = 0) in vec3 position;\nlayout(location = 1) in vec2 texCoord;\nlayout(location = 2) in vec3 normal;\n\nout vec3 vNorm;\nout vec3 vPos;\nout vec2 outTexCoord;\nout vec4 vDirectionalShadowSpace;\n\nuniform mat4 projectionMatrix;\nuniform mat4 viewMatrix;\nuniform mat4 modelMatrix;\nuniform mat4 directionalShadowMatrix;\n\nvoid main()\n{\n	vec4 mvPos = modelMatrix * vec4(position, 1.0);\n    gl_Position = projectionMatrix * viewMatrix * mvPos;\n    vNorm = normalize(transpose(inverse(mat3(modelMatrix))) * normal);\n    vPos = mvPos.xyz;\n    vDirectionalShadowSpace = directionalShadowMatrix * mvPos;\n    outTexCoord=texCoord;\n}";
}
