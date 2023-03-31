package yaw.engine;

import org.joml.Matrix4f;
import yaw.engine.camera.Camera;
import yaw.engine.light.SceneLight;
import yaw.engine.shader.ShaderManager;
import yaw.engine.skybox.Skybox;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

public class RendererHelperSummit {
    /**
     * HelperSummit rendering.
     */


    public void render(SceneVertex pSceneVertex, Camera pCamera, ShaderManager shaderManager) {
        pSceneVertex.renderHelperSummit(pCamera, shaderManager);

    }
}
