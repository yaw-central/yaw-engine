package yaw.engine.shader;

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
}
