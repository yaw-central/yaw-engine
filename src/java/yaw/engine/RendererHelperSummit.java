package yaw.engine;

import yaw.engine.camera.Camera;
import yaw.engine.shader.ShaderManager;

public class RendererHelperSummit {
    /**
     * HelperSummit rendering.
     */


    public void render(Scene pScene, Camera pCamera, ShaderManager shaderManager) {
        pScene.renderHelperSummit(pCamera, shaderManager);

    }
}
