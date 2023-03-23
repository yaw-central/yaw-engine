package yaw.engine.shader;

import yaw.engine.Window;
import yaw.engine.light.SceneLight;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.glViewport;


public class ShaderManager {
    private ArrayList<ShaderProgram> shaderlist;

    public ShaderManager(){
        this.shaderlist = new ArrayList<ShaderProgram>();

        try {
            /* Initialization of the shader program. */
            ShaderProgramADS mShaderProgram = new ShaderProgramADS();
            mShaderProgram.createVertexShader(vertShader.SHADER_STRING);
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

            shaderlist.add(mShaderProgram);
        }catch(Exception e){
            System.out.println("Erreur constructeur ShaderManager");
        }
    }

    public void addShader(ShaderProgram mShaderProgram){
        shaderlist.add(mShaderProgram);
    }

    public int sizeList(){
        return shaderlist.size();
    }

    public ShaderProgram getShaderProgram(int id){
        return shaderlist.get(id);
    }

    public void cleanUp(){
        for(ShaderProgram sp : shaderlist){
            sp.cleanup();
        }
    }


}
