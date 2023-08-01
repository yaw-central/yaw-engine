package yaw.engine.shader;

import yaw.engine.Helper.*;
import yaw.engine.light.SceneLight;

import java.util.ArrayList;



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

            /* HelperSummit ShaderProgram */

            ShaderProgramHelperSummit mShaderProgramHelperSummit = new ShaderProgramHelperSummit();
            mShaderProgramHelperSummit.createVertexShader(mShaderProgramHelperSummit.getVs());
            mShaderProgramHelperSummit.createFragmentShader(mShaderProgramHelperSummit.getFs());

            mShaderProgramHelperSummit.link();


            mShaderProgramHelperSummit.createUniform("projectionMatrix");
            mShaderProgramHelperSummit.createUniform("viewMatrix");
            mShaderProgramHelperSummit.createUniform("modelMatrix");


            shaderlist.add(mShaderProgramHelperSummit);

            /* helperNormal ShaderProgram */

            ShaderProgramHelperNormal mShaderProgramHelperNormal = new ShaderProgramHelperNormal();

            mShaderProgramHelperNormal.createVertexShader(mShaderProgramHelperNormal.getVs());
            mShaderProgramHelperNormal.createGeometryShader(mShaderProgramHelperNormal.getGs());
            mShaderProgramHelperNormal.createFragmentShader(mShaderProgramHelperNormal.getFs());

            mShaderProgramHelperNormal.link();

            mShaderProgramHelperNormal.createUniform("projectionMatrix");
            mShaderProgramHelperNormal.createUniform("viewMatrix");
            mShaderProgramHelperNormal.createUniform("modelMatrix");

            shaderlist.add(mShaderProgramHelperNormal);

            /* helperAxesMesh ShaderProgram */

            ShaderProgramHelperAxesMesh mShaderProgramHelperAxesMesh = new ShaderProgramHelperAxesMesh();
            mShaderProgramHelperAxesMesh.createVertexShader(mShaderProgramHelperAxesMesh.getVs());
            mShaderProgramHelperAxesMesh.createGeometryShader(mShaderProgramHelperAxesMesh.getGs());
            mShaderProgramHelperAxesMesh.createFragmentShader(mShaderProgramHelperAxesMesh.getFs());

            mShaderProgramHelperAxesMesh.link();
            mShaderProgramHelperAxesMesh.createUniform("projectionMatrix");
            mShaderProgramHelperAxesMesh.createUniform("viewMatrix");
            mShaderProgramHelperAxesMesh.createUniform("modelMatrix");
            mShaderProgramHelperAxesMesh.createUniform("center");

            shaderlist.add(mShaderProgramHelperAxesMesh);

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

    public ShaderProgramADS getShaderProgramAds() { return (ShaderProgramADS) shaderlist.get(0);}
    public ShaderProgramHelperSummit getShaderProgramHelperSummit() { return (ShaderProgramHelperSummit) shaderlist.get(1);}
    public ShaderProgramHelperNormal getShaderProgramHelperNormals() { return (ShaderProgramHelperNormal) shaderlist.get(2);}
    public ShaderProgramHelperAxesMesh getShaderProgramHelperAxesMesh() { return (ShaderProgramHelperAxesMesh) shaderlist.get(3);}

    /**
     * The Shader Program is deallocated
     */
    public void cleanUp(){
        for(ShaderProgram sp : shaderlist){
            sp.cleanup();
        }
    }
}
