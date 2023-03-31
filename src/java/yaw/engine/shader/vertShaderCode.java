package yaw.engine.shader;

public class vertShaderCode {
    public ShaderCode code;

    public vertShaderCode() {
        this.buildCode();
    }

    void buildCode() {
        code = new ShaderCode("330", true)
                .cmt("Buffers")
                .l("layout(location = 0) in vec3 position")
                .l("layout(location = 1) in vec2 texCoord")
                .l("layout(location = 2) in vec3 normal")
                .l()
                .cmt("Outputs")
                .l("out vec3 vNorm")
                .l("out vec3 vNorm")
                .l("out vec2 outTexCoord")
                .cmt("Shadow")
                .l("out vec4 vDirectionalShadowSpace")
                .l()
                .cmt("Uniforms")
                .l("uniform mat4 projectionMatrix")
                .l("uniform mat4 viewMatrix")
                .l("uniform mat4 modelMatrix")
                .cmt("Shadow")
                .l("uniform mat4 directionalShadowMatrix")
                .l()
                .beginMain()
                  .l("vec4 mvPos = modelMatrix * vec4(position, 1.0)")
                  .l("gl_Position = projectionMatrix * viewMatrix * mvPos")
                  .l("vNorm = normalize(transpose(inverse(mat3(modelMatrix))) * normal)")
                  .l("vPos = mvPos.xyz")
                  .l("vDirectionalShadowSpace = directionalShadowMatrix * mvPos")
                  .l("outTexCoord=texCoord")
                .endMain();

    }
}
