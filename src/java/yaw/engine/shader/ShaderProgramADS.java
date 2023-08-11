package yaw.engine.shader;

import yaw.engine.light.LightModel;
import yaw.engine.mesh.Material;

public class ShaderProgramADS extends ShaderProgram {
    public ShaderProgramADS() {
    }

    /**
     * Create uniform for each attribute of the material
     *
     * @param uniformName uniform name
     */
    public void createMaterialUniform(String uniformName) {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    /**
     * Modifies the value of a uniform material with the specified material
     *
     * @param uniformName the uniform name
     * @param material    the material
     */
    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".color", material.getColor());
        setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    public void init() {
        /* Initialization of the shader program. */
        // System.out.println(vertexShader(true).toString());
        createVertexShader(vertexShader(true));
        createFragmentShader(fragShader.SHADER_STRING);

        /* Binds the code and checks that everything has been done correctly. */
        link();

        createUniform("worldMatrix");
        createUniform("modelMatrix");
        createUniform("normalMatrix");

        /* Initialization of the shadow map matrix uniform. */
        createUniform("directionalShadowMatrix");

        /* Create uniform for material. */
        createMaterialUniform("material");
        createUniform("texture_sampler");
        /* Initialization of the light's uniform. */
        createUniform("camera_pos");
        createUniform("specularPower");
        createUniform("ambientLight");
        createPointLightListUniform("pointLights", LightModel.MAX_POINTLIGHT);
        createSpotLightUniformList("spotLights", LightModel.MAX_SPOTLIGHT);
        createDirectionalLightUniform("directionalLight");
        createUniform("shadowMapSampler");
        createUniform("bias");
    }

    public ShaderCode vertexShader(boolean withShadows) {
        ShaderCode code = new ShaderCode("330", true)
                .l()
                .cmt("Input buffer components")
                .l("layout(location = 0) in vec3 position;")
                .l("layout(location = 1) in vec2 texCoord;")
                .l("layout(location = 2) in vec3 normal;")
                .l()
                .cmt("Output values")
                .l("out vec3 vPos;")
                .l("out vec2 outTexCoord;")
                .l("out vec3 vNorm;");

        if (withShadows) {
            code.l().cmt("Shadow properties")
                    .l("out vec4 vDirectionalShadowSpace;");
        }

        code.l().cmt("Camera-level uniforms")
                .l("uniform mat4 worldMatrix;")
                .l()
                .cmt("Model-level uniforms")
                .l("uniform mat4 modelMatrix;")
                .l("uniform mat3 normalMatrix;");

        if (withShadows) {
            code.l().cmt("Shadow uniforms")
                    .l("uniform mat4 directionalShadowMatrix;");
        }

        code.l().beginMain()
                .cmt("World vertex position")
                .l("vec4 mvPos = modelMatrix * vec4(position, 1.0);")
                .cmt("Projected position")
                .l("gl_Position = worldMatrix * mvPos;")
                .cmt("Output copy of position")
                .l("vPos = mvPos.xyz;")
                .cmt("Computation of normal vector")
                .l("vNorm = normalize(mat3(normalMatrix) * normal);")
                .cmt("Texture coordinates")
                .l("outTexCoord = texCoord;");

        if (withShadows) {
            code.l().cmt("Shadow output")
                    .l("vDirectionalShadowSpace = directionalShadowMatrix * mvPos;");
        }

        code.endMain();

        return code;
    }
}


