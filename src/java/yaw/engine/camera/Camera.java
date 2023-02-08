package yaw.engine.camera;

import yaw.engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import yaw.engine.shader.ShaderCode;

/**
 * Class which will hold the position and rotation state of our camera.
 * To move from a world described in 3D to a window with 2D pixels we must lose a dimension and therefore project.
 * The perspective is defined by the pyramid clipping.
 */
public class Camera {

    private Matrix4f projectionMat;
    private Matrix4f cameraMat;
    /* Angle of the field of view
       A small angle gives a zoom effect.
       Like a zoom on a pair of binoculars.. */
    private float fieldOfView;
    /* Scope of vision min and max.
       Outside this range objects will not be displayed.*/
    private float zNear;
    private float zFar;

    /**
     * The constructors of the class Camera Creates the camera of the 3D scene with the position of the camera, the perspective.
     * The near and far parameters determine the minimum and maximum distances of objects. Outside this range objects will not be displayed.
     *
     * @param fieldOfView fieldOfView
     * @param zNear       zNear
     * @param zFar        zFar
     */

    public Camera(float fieldOfView, float zNear, float zFar) {

        this.fieldOfView = fieldOfView;
        this.zFar = zFar;
        this.zNear = zNear;
        cameraMat = new Matrix4f().identity();

        updateProjectionMat();
    }

    public Camera(float zNear, float zFar) {
        this((float) Math.toRadians(60.0f), zNear, zFar);
    }

    public Camera() {
        this(0.01f, 1000f);
    }

    /**
     * Get the Matrix that transforms from camera space to screenspace
     *
     * @return projection matrix
     */
    public Matrix4f getProjectionMat() {
        return projectionMat;
    }

    /**
     * Updates the projection matrix of the scene.
     */
    public void updateProjectionMat() {
        projectionMat = new Matrix4f().perspective(fieldOfView, (float) Window.aspectRatio(), zNear, zFar);
    }


    /**
     * Get the Matrix that transforms the camera space into world space
     *
     * @return camera matrix
     */
    public Matrix4f getCameraMat() {
        return cameraMat;
    }

    /**
     * Generate the Matrix that transforms from world space to camera space
     *
     * @return view matrix
     */
    public Matrix4f getViewMat() {
        return new Matrix4f(cameraMat).invert();
    }


    public Vector3f getPosition() {
        return cameraMat.getTranslation(new Vector3f());
    }

    /**
     * Changes the position of the camera.
     *
     * @param x coordinate X of the camera
     * @param y coordinate Y of the camera
     * @param z coordinate Z of the camera
     */
    public void setPosition(float x, float y, float z) {
        setPosition(new Vector3f(x, y, z));
    }

    public void setPosition(Vector3f pos) {
        translate(pos.sub(getPosition()));
    }

    /**
     * Adds vector to the position of the camera.
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void translate(float x, float y, float z) {
        translate(new Vector3f(x, y, z));
    }

    public void translate(Vector3f t) {
        cameraMat.translate(t);
    }


    /**
     * Changes the camera to look at a target from a position
     *
     * @param eye position of the camera
     * @param target position to look at
     * @param up the direction of the cameras up vector
     */
    public void lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        cameraMat.lookAt(eye, target, up).invert();
    }

    public void lookAt(Vector3f target, Vector3f up) {
	lookAt(getPosition(), target, up);
    }

    public void lookAt(float x, float y, float z, float ux, float uy, float uz) {
	lookAt(new Vector3f(x, y, z), new Vector3f(ux, uy, uz));
    }

    public void lookAt(Vector3f target) {
	    Vector3f camPos = getPosition();
        Vector3f camDir = new Vector3f();
        camPos.sub(target, camDir);
        camDir.normalize();
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f camRight = up.cross(camDir).normalize();
        Vector3f camUp = camDir.cross(camRight);
        lookAt(camPos, target, camUp);
    }

    public void lookAtTarget(float targetX, float targetY, float targetZ) {
	    lookAt(new Vector3f(targetX, targetY, targetZ));
    }


    public Vector3f getRotation() {
        return cameraMat.getEulerAnglesXYZ(new Vector3f());
    }

    public void setRotation(float x, float y, float z) {
        cameraMat.setRotationXYZ(x, y, z);
    }

    public void setRotation(Vector3f rot) {
        setRotation(rot.x, rot.y, rot.z);
    }

    /**
     * Rotates the camera along the x y and z axes
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void rotateXYZ(float x, float y, float z) {
        cameraMat.rotateXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
    }

    public void rotateXYZ(Vector3f rot) {
        rotateXYZ(rot.x, rot.y, rot.z);
    }


    public float getzNear() {
        return zNear;
    }

    public void setzNear(float zNear) {
        this.zNear = zNear;
        updateProjectionMat();
    }

    public float getzFar() {
        return zFar;
    }

    public void setzFar(float zFar) {
        this.zFar = zFar;
        updateProjectionMat();
    }

    public float getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(float fov) {
        this.fieldOfView = fov;
        updateProjectionMat();
    }


    public void glslPrepareUniforms(ShaderCode shader) {
        shader.l("uniform mat4 viewMatrix;")
              .l("uniform mat4 projectionMatrix;");
    }

    public String glslGetViewMatrixUniform() {
        return "viewMatrix";
    }

    public String glslGetProjectionMatrixUniform() {
        return "projectionMatrix";
    }

}
