package yaw.engine;

import yaw.engine.camera.Camera;
import yaw.engine.shader.ShaderManager;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

public class RendererHelperNormal {
    /**
     * HelperNormal rendering.
     */


    public void render(SceneVertex pSceneVertex, Camera pCamera, ShaderManager shaderManager) {
        //Debug
        /*  int err = GL11.GL_NO_ERROR;
        if ((err = GL11.glGetError()) != GL11.GL_NO_ERROR) {

            System.out.println(err);
        }*/

        /* Initialization of the window we currently use. */



        pSceneVertex.renderHelperNormal(pCamera, shaderManager);

    }
}
