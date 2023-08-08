package yaw.engine;

import yaw.engine.camera.Camera;
import yaw.engine.shader.ShaderManager;

public class RendererHelperAxesMesh {

    public void render(Scene pScene, Camera pCamera, ShaderManager shaderManager) {
        pScene.renderHelperAxesMesh(pCamera, shaderManager);
    }
}
