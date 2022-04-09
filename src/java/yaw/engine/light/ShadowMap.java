package yaw.engine.light;

import clojure.core.Vec;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import yaw.engine.SceneVertex;
import yaw.engine.meshs.Material;
import yaw.engine.shader.ShaderProgram;
import yaw.engine.shader.shadowFragShader;
import yaw.engine.shader.shadowVertShader;


import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {

    private class ShadowShaderProgram extends ShaderProgram {

        public ShadowShaderProgram() throws Exception {
            super();
        }

        @Override
        public void setUniform(String uniformName, Material material) {
            // Don't do anything as there is no material
        }
    }

    private int width;
    private int height;

    private boolean initialized = false;

    private int framebuffer;

    private int depthMap;

    /**
     * Coverage settings for shadow map
     */
    private Vector3f center = new Vector3f();
    private float left = -10;
    private float right = 10;
    private float bottom = -10;
    private float top = 10;
    private float zNear = -10;
    private float zFar = 10;


    private Matrix4f projection;
    private Matrix4f view;

    private ShadowShaderProgram mShaderProgram;

    public ShadowMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ShadowMap() {
        this(2048, 2048);
    }

    public void init() throws Exception {

        mShaderProgram = new ShadowShaderProgram();
        mShaderProgram.createVertexShader(shadowVertShader.SHADER_STRING);
        mShaderProgram.createFragmentShader(shadowFragShader.SHADER_STRING);

        /* Binds the code and checks that everything has been done correctly. */
        mShaderProgram.link();

        mShaderProgram.createUniform("projectionMatrix");
        mShaderProgram.createUniform("viewMatrix");
        mShaderProgram.createUniform("modelMatrix");

        framebuffer = glGenFramebuffers();

        depthMap = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthMap);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        float borderColor[] = { 0,0,0,0 };
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap, 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public void render(SceneVertex pSceneVertex, DirectionalLight light) {

        if(!initialized) {
            try {
                init();
                initialized = true;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        glViewport(0, 0, width, height);
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        glClear(GL_DEPTH_BUFFER_BIT);
        glCullFace(GL_FRONT);

        mShaderProgram.bind();

        projection = new Matrix4f().identity().ortho(left, right, bottom, top, zNear, zFar);
        view = new Matrix4f().identity().lookAt(center, light.mDirection, new Vector3f(0,1,0));

        /* Set the camera to render. */
        mShaderProgram.setUniform("projectionMatrix", projection);
        mShaderProgram.setUniform("viewMatrix", view);

        pSceneVertex.draw(mShaderProgram, new Matrix4f().identity());

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public void bind(ShaderProgram shaderProgram) {
        if(!initialized) return;

        shaderProgram.setUniform("directionalShadowMatrix", new Matrix4f(projection).mul(view));

        shaderProgram.setUniform("shadowMapSampler", 1);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, depthMap);

    }

    public void cleanUp() {

        glDeleteProgram(mShaderProgram.getId());

        glDeleteFramebuffers(framebuffer);

        glDeleteTextures(depthMap);

    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getzNear() {
        return zNear;
    }

    public void setzNear(float zNear) {
        this.zNear = zNear;
    }

    public float getzFar() {
        return zFar;
    }

    public void setzFar(float zFar) {
        this.zFar = zFar;
    }
}
