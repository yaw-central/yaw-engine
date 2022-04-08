package yaw.engine.camera;

import org.joml.Quaternionf;
import yaw.engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Class which will hold the position and rotation state of our camera.
 * To move from a world described in 3D to a window with 2D pixels we must lose a dimension and therefore project.
 * The perspective is defined by the pyramid clipping.
 */
public class Camera {

    private Vector3f position; /* Position of the camera (the camera is represented by a point in space). */
    private Quaternionf orientation;/* Sets the position of the point fixed by the camera. */
    private Matrix4f perspectiveMat;
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
        this.position = new Vector3f(0f, 0f, 0f);
        this.orientation = new Quaternionf().identity();

        updateCameraMat();
    }

    public Camera(float zNear, float zFar) {
        this((float) Math.toRadians(60.0f), zNear, zFar);
    }

    public Camera() {
        this(0.01f, 1000f);
    }


    public Matrix4f getProjectionMat() {
        return perspectiveMat;
    }

    /**
     * Changes the position of the camera.
     *
     * @param x coordinate X of the camera
     * @param y coordinate Y of the camera
     * @param z coordinate Z of the camera
     */
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f pos) {
        this.position = pos;
    }

    /**
     * Changes the orientation of the camera.
     *
     * @param x coordinate X of the camera
     * @param y coordinate Y of the camera
     * @param z coordinate Z of the camera
     */
    public void setOrientation(float x, float y, float z) {
        this.orientation.rotationXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
    }

    public Quaternionf getOrientation() {
        return orientation;
    }

    public void setOrientation(Quaternionf pos) {
        this.orientation = pos;
    }

    /**
     * Allows to change the place of our object and therefore to make it navigate in our 3D scene.
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void rotateXYZ(float x, float y, float z) {
        orientation.rotateXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
    }

    /**
     * Changes the size of the object to adjust the 3D scene.
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void translate(float x, float y, float z) {
        position.add(x, y, z);
    }


    public void lookAt(Vector3f eye, Vector3f target, Vector3f up) {

        System.out.println(new Vector3f(target).sub(eye));

        orientation.lookAlong(new Vector3f(target).sub(eye), up);

        eye.get(position);

    }

    /**
     * Generate the Matrix for the camera position
     *
     * @return viewMatrix
     */
    public Matrix4f setupViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f().identity();
        viewMatrix.translate(position);
        viewMatrix.rotate(orientation);
        return viewMatrix.invert();
    }

    /*
    public Matrix4f setupViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f().identity();
        //viewMatrix.rotateX((float) Math.toRadians(-orientation.x)).rotateY((float) Math.toRadians(-orientation.y)).rotateZ((float) Math.toRadians(-orientation.z));
        viewMatrix.rotate((float) Math.toRadians(-orientation.x), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(-orientation.y), new Vector3f(0, 1, 0));
        viewMatrix.rotate((float) Math.toRadians(-orientation.z), new Vector3f(0, 0, 1));
        Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
        viewMatrix.translate(negativeCameraPos);
        return viewMatrix;
    }
    */

    /**
     * Updates the perspective of the scene.
     */
    public void updateCameraMat() {
        perspectiveMat = new Matrix4f().perspective(fieldOfView, (float) Window.aspectRatio(), zNear, zFar);
    }


    public float getzNear() {
        return zNear;
    }

    public void setzNear(float zNear) {
        this.zNear = zNear;
        updateCameraMat();
    }

    public float getzFar() {
        return zFar;
    }

    public void setzFar(float zFar) {
        this.zFar = zFar;
        updateCameraMat();
    }

    public float getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(float fov) {
        this.fieldOfView = fov;
        updateCameraMat();
    }


}
