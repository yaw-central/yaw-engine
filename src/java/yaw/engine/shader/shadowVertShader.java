
// This file is generated from `shadowVertShader.fs` shader program
// Please do not edit directly
package yaw.engine.shader;

public class shadowVertShader {
    public final static String SHADER_STRING = "#version 330 core\n\nlayout(location = 0) in vec3 position;\n\nuniform mat4 projectionMatrix;\nuniform mat4 viewMatrix;\nuniform mat4 modelMatrix;\n\nvoid main()\n{\n	vec4 mvPos = modelMatrix * vec4(position, 1.0);\n    gl_Position = projectionMatrix * viewMatrix * mvPos;\n}";
}
