package yaw.engine;

import yaw.engine.camera.Camera;
import yaw.engine.light.SceneLight;
import yaw.engine.shader.ShaderProgram;
import yaw.engine.shader.fragShader;
import yaw.engine.shader.vertShader;
import yaw.engine.skybox.Skybox;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class allows to manage the rendering logic of our game.
 * The shader is used to configure a part of the rendering process performed by a graphics card.
 * The shader allows to describe the absorption, the diffusion of the light, the texture to be used, the reflections of the objects, the shading, etc ...
 */
public class Renderer {
    protected ShaderProgram mShaderProgram;

    /**
     * Basic rendering.
     */
    public void init() throws Exception {
        /* Initialization of the shader program. */
        mShaderProgram = new ShaderProgram();
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
    }

    /**
     * The Shader Program is deallocated
     */
    public void cleanUp() {
        if (mShaderProgram != null) {
            mShaderProgram.cleanup();
        }
    }

    /**
     * Specific rendering.
     * Configuring rendering with the absorption, the diffusion of the light, the texture to be used, the reflections of the objects, the shading,
     * Which are passed by arguments
     *
     * @param pSceneVertex sceneVertex
     * @param pSceneLight  sceneLight
     * @param isResized    isResized
     * @param pCamera      camera
     * @param pSkybox      skybox
     */
    public void render(SceneVertex pSceneVertex, SceneLight pSceneLight, boolean isResized, Camera pCamera, Skybox pSkybox) {

        //Preparation of the camera
        if (isResized || pSceneVertex.isItemAdded()) {
            pCamera.updateProjectionMat();
        }


        //Debug
        /*  int err = GL11.GL_NO_ERROR;
        if ((err = GL11.glGetError()) != GL11.GL_NO_ERROR) {

            System.out.println(err);
        }*/

        /* Initialization of the window we currently use. */
        glViewport(0, 0, Window.getWidth(), Window.getHeight());

        mShaderProgram.bind();

        /* Set the camera to render. */
        mShaderProgram.setUniform("projectionMatrix", pCamera.getProjectionMat());
        mShaderProgram.setUniform("texture_sampler", 0);
        mShaderProgram.setUniform("camera_pos", pCamera.getPosition());
        Matrix4f viewMat = pCamera.getViewMat();
        mShaderProgram.setUniform("viewMatrix", viewMat);

        /* Enable the option needed to render.*/
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glDepthMask(true);        /* Enable or disable writing to the depth buffer. */
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);       /* Specify the value used for depth buffer comparisons. */
        glClearDepth(1); /* GlClearDepth specifies the depth value used by glClear to clear the depth buffer.
                                                   The values ​​specified by glClearDepth are set to range [0.1].*/

        glDisable(GL_BLEND);

        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        /* Rendering of the light. */
        pSceneLight.render(mShaderProgram, new Matrix4f().identity());

        /* Init Objects. */
        pSceneVertex.initMesh();

        /* Update objects
        XXX useless?  sc.update(); */


        /* Rendering of the object. */
        pSceneVertex.draw(mShaderProgram);
        /* Cleans all services. */
        mShaderProgram.unbind();
        if (pSkybox != null) {
            if (pSkybox.init == false) {
                try {
                    pSkybox.init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pSkybox.draw(pCamera);
        }
    }
}
