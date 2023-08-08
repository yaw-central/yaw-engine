package yaw.engine.shader;

import yaw.engine.light.LightModel;
import yaw.engine.mesh.Material;

public class ShaderProgramADS extends ShaderProgram {
    public ShaderProgramADS() {
        super();
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

    public void init(LightModel scenelight) {
        /* Initialization of the shader program. */
        createVertexShader(vertShader.SHADER_STRING);
        createFragmentShader(fragShader.SHADER_STRING);

        /* Binds the code and checks that everything has been done correctly. */
        link();

        createUniform("projectionMatrix");
        createUniform("viewMatrix");
        createUniform("modelMatrix");

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
}


