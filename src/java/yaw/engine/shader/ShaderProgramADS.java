package yaw.engine.shader;

import yaw.engine.light.SceneLight;
import yaw.engine.mesh.Material;

public class ShaderProgramADS extends ShaderProgram {
    public ShaderProgramADS() throws Exception {
        super();
    }

    /**
     * Create uniform for each attribute of the material
     *
     * @param uniformName uniform name
     * @throws Exception the exception
     */
    public void createMaterialUniform(String uniformName) throws Exception {
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

        setUniform(uniformName + ".hasTexture", (int) (material.isTextured() ? 1 : 0));

        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    public void init(SceneLight scenelight) {
         /* Initialization of the shader program. */
        createVertexShader(vertShader.SHADER_STRING);
            mShaderProgram.createFragmentShader(fragShader.SHADER_STRING);

            /* Binds the code and checks that everything has been done correctly. */
            mShaderProgram.link();

            mShaderProgram.createUniform("projectionMatrix");
            mShaderProgram.createUniform("viewMatrix");
            mShaderProgram.createUniform("modelMatrix");

            /* Initialization of the shadow map matrix uniform. */
            mShaderProgram.createUniform("directionalShadowMatrix");

            /* Create uniform for material. */
            mShaderProgram.createMaterialUniform("material");
            mShaderProgram.createUniform("texture_sampler");
            /* Initialization of the light's uniform. */
            mShaderProgram.createUniform("camera_pos");
            mShaderProgram.createUniform("specularPower");
            mShaderProgram.createUniform("ambientLight");
            mShaderProgram.createPointLightListUniform("pointLights", SceneLight.MAX_POINTLIGHT);
            mShaderProgram.createSpotLightUniformList("spotLights", SceneLight.MAX_SPOTLIGHT);
            mShaderProgram.createDirectionalLightUniform("directionalLight");
            mShaderProgram.createUniform("shadowMapSampler");
            mShaderProgram.createUniform("bias");
}


