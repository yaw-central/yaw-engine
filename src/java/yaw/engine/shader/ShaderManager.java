package yaw.engine.shader;

import yaw.engine.helper.*;
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

            HelperVerticesShaders mHelperVerticesShaders = new HelperVerticesShaders();
            mHelperVerticesShaders.createVertexShader(mHelperVerticesShaders.getVs());
            mHelperVerticesShaders.createFragmentShader(mHelperVerticesShaders.getFs());

            mHelperVerticesShaders.link();


            mHelperVerticesShaders.createUniform("projectionMatrix");
            mHelperVerticesShaders.createUniform("viewMatrix");
            mHelperVerticesShaders.createUniform("modelMatrix");


            shaderlist.add(mHelperVerticesShaders);

            /* helperNormal ShaderProgram */

            HelperNormalsShaders mHelperNormalsShaders = new HelperNormalsShaders();

            mHelperNormalsShaders.createVertexShader(mHelperNormalsShaders.getVs());
            mHelperNormalsShaders.createGeometryShader(mHelperNormalsShaders.getGs());
            mHelperNormalsShaders.createFragmentShader(mHelperNormalsShaders.getFs());

            mHelperNormalsShaders.link();

            mHelperNormalsShaders.createUniform("projectionMatrix");
            mHelperNormalsShaders.createUniform("viewMatrix");
            mHelperNormalsShaders.createUniform("modelMatrix");

            shaderlist.add(mHelperNormalsShaders);

            /* helperAxesMesh ShaderProgram */

            HelperAxesShaders mHelperAxesShaders = new HelperAxesShaders();
            mHelperAxesShaders.createVertexShader(mHelperAxesShaders.getVs());
            mHelperAxesShaders.createGeometryShader(mHelperAxesShaders.getGs());
            mHelperAxesShaders.createFragmentShader(mHelperAxesShaders.getFs());

            mHelperAxesShaders.link();
            mHelperAxesShaders.createUniform("projectionMatrix");
            mHelperAxesShaders.createUniform("viewMatrix");
            mHelperAxesShaders.createUniform("modelMatrix");
            mHelperAxesShaders.createUniform("center");

            shaderlist.add(mHelperAxesShaders);

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
    public HelperVerticesShaders getShaderProgramHelperSummit() { return (HelperVerticesShaders) shaderlist.get(1);}
    public HelperNormalsShaders getShaderProgramHelperNormals() { return (HelperNormalsShaders) shaderlist.get(2);}
    public HelperAxesShaders getShaderProgramHelperAxesMesh() { return (HelperAxesShaders) shaderlist.get(3);}

    /**
     * The Shader Program is deallocated
     */
    public void cleanUp(){
        for(ShaderProgram sp : shaderlist){
            sp.cleanup();
        }
    }
}
