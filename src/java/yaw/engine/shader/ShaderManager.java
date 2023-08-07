package yaw.engine.shader;

import yaw.engine.helper.*;
import yaw.engine.light.SceneLight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages all the shaders (shader programs)
 * used to render a frame in the gameloop.
 *
 * This applies to the following "times" :
 *
 *  - initialization time : before entering the gameloop or when
 *  a "reinitialization" must be performed, e.g. if the (maximum) number of lights change,
 *  which require a reconstruction of the shader programs. This concerns e.g. the compilation
 *  of the shader programs, the creation of uniforms, etc.
 *  The corresponding methods are prefixed with  `init...`
 *
 *  - (frame) setup time `setup` : before the rendering, when e.g. all the global (shared) uniforms must be set.
 *  The method prefix is `setup...`
 *
 *  - rendering time :  mostly setting the mesh-specific uniforms and issuing the draw commands.
 *  The method prefix is `render...`, and it is the responsability of the renderer.
 */
public class ShaderManager {

    private Map<String, ShaderProgram> shaderMap;

    private ArrayList<ShaderProgram> shaderlist;

    public ShaderManager() {
        shaderMap = new HashMap<>();
        shaderlist = new ArrayList<>();
        init(null);
    }

    public void init(SceneLight scenelight) {

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
