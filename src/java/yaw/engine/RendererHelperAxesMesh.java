package yaw.engine;

import yaw.engine.camera.Camera;
import yaw.engine.shader.ShaderManager;

public class RendererHelperAxesMesh {

    public void render(SceneVertex pSceneVertex, Camera pCamera, ShaderManager shaderManager) {
        pSceneVertex.renderHelperAxesMesh(pCamera, shaderManager);
    }
}
