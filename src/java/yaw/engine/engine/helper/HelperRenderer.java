package yaw.engine.helper;


import org.joml.Matrix4f;
import yaw.engine.SceneVertex;
import yaw.engine.Window;

import yaw.engine.camera.Camera;
import yaw.engine.light.SceneLight;
import yaw.engine.shader.ShaderProgram;
import yaw.engine.skybox.Skybox;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

public class HelperRenderer {
    protected ShaderProgram mShaderProgram;
    public void init() throws Exception {
        /* Initialization of the shader program. */
        mShaderProgram = new ShaderProgram();
        mShaderProgram.createVertexShader(originHelperVertShader.SHADER_STRING);
        mShaderProgram.createFragmentShader(originHelperFragShader.SHADER_STRING);


        /* Binds the code and checks that everything has been done correctly. */
        mShaderProgram.link();

        mShaderProgram.createUniform("projectionMatrix");
        mShaderProgram.createUniform("viewMatrix");
        mShaderProgram.createUniform("modelMatrix");


    }

    public void cleanUp() {
        if (mShaderProgram != null) {
            mShaderProgram.cleanup();
        }
    }

    public void render(SceneVertex pSceneVertex, SceneLight pSceneLight, boolean isResized, Camera pCamera, Skybox pSkybox) {

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

        mShaderProgram.setUniform("projectionMatrix", pCamera.getProjectionMat());
        Matrix4f viewMat = pCamera.getViewMat();
        mShaderProgram.setUniform("viewMatrix", viewMat);

        /* Set the camera to render. */



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



        /* Init Objects. */
        pSceneVertex.initMesh();

        /* Update objects
        XXX useless?  sc.update(); */


        /* Rendering of the object. */
        pSceneVertex.draw(mShaderProgram);
        /* Cleans all services. */
        mShaderProgram.unbind();

    }
}
