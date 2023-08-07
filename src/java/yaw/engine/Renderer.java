package yaw.engine;

import org.joml.Matrix4f;
import yaw.engine.camera.Camera;
import yaw.engine.light.SceneLight;
import yaw.engine.shader.ShaderManager;
import yaw.engine.skybox.Skybox;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class allows to manage the rendering logic of our game.
 * The shader is used to configure a part of the rendering process performed by a graphics card.
 * The shader allows to describe the absorption, the diffusion of the light, the texture to be used, the reflections of the objects, the shading, etc ...
 */
public class Renderer {

    /**
     * Specific rendering.
     * Configuring rendering with the absorption, the diffusion of the light, the texture to be used, the reflections of the objects, the shading,
     * Which are passed by arguments
     *
     * @param pSceneVertex  sceneVertex
     * @param pSceneLight   sceneLight
     * @param isResized     isResized
     * @param pCamera       camera
     * @param pSkybox       skybox
     * @param shaderManager shaderManager
     */
    public void render(SceneVertex pSceneVertex, SceneLight pSceneLight, boolean isResized, Camera pCamera, Skybox pSkybox, ShaderManager shaderManager) {

        //Preparation of the camera
        if (isResized || pSceneVertex.isItemAdded()) {
            pCamera.updateProjectionMat();
        }

        /* Initialization of the window we currently use. */
        glViewport(0, 0, Window.getWidth(), Window.getHeight());

        /* Enable the option needed to render.*/
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glDepthMask(true);        /* Enable or disable writing to the depth buffer. */
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);       /* Specify the value used for depth buffer comparisons. */
        glClearDepth(1); /* GlClearDepth specifies the depth value used by glClear to clear the depth buffer.
                                                   The values specified by glClearDepth are set to range [0.1].*/

        glDisable(GL_BLEND);

        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        /* Rendering of the scene */
        pSceneVertex.render(pCamera, shaderManager);

        /* Rendering of lights */
        pSceneLight.render(new Matrix4f().identity(), shaderManager);

        /* skybox */
        if (pSkybox != null) {
            if (!pSkybox.init) {
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
