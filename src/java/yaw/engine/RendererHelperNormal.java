package yaw.engine;

import yaw.engine.camera.Camera;
import yaw.engine.shader.ShaderManager;

public class RendererHelperNormal {
    /**
     * HelperNormal rendering.
     */


    public void render(Scene pScene, Camera pCamera, ShaderManager shaderManager) {
        //Debug
        /*  int err = GL11.GL_NO_ERROR;
        if ((err = GL11.glGetError()) != GL11.GL_NO_ERROR) {

            System.out.println(err);
        }*/

        /* Initialization of the window we currently use. */



        pScene.renderHelperNormal(pCamera, shaderManager);

    }
}
