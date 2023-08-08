package yaw.engine.items;

import org.joml.*;
import yaw.engine.mesh.Mesh;
import java.lang.Math;

/**
 * An ItemObject is a concrete 3D item associated to a Mesh
 */
public class ItemObject extends Item {

    /** The mesh (geometry) of the object. */
    private Mesh mesh;

    /** The transformation matrix to world coordinates */
    private Matrix4f worldMatrix;

    private boolean vertexHelpers;
    private boolean normalHelpers;
    private boolean axisHelpers;

    private boolean castShadows = true;

    public ItemObject(String id, Vector3f position, Quaternionf orientation, float scale, Mesh mesh){
        super(id, position, orientation, scale);
        this.mesh= mesh;
        worldMatrix = new Matrix4f();
        invalidate();
        vertexHelpers = false;
        normalHelpers = false;
        axisHelpers = false;
    }

    public void buildWorldMatrix() {
        worldMatrix.identity()
                .translate(getPosition())
                .rotate(getOrientation())
                .scale(getScale());
    }

    public Matrix4f getWorldMatrix() {
        return worldMatrix;
    }

    @Override
    public void invalidate() {
        buildWorldMatrix();
    }

    public void repelBy(Vector3f center, float dist) {
        Vector3f dif = new Vector3f(position.x - center.x, position.y - center.y, position.z - center.z);
        float norm = dif.length();
        if (norm != 0) {
            float move = (dist / norm) + 1;
            dif.mul(move);
            dif.add(center);
            position = dif;
        }
    }


    /**
     * Get the mesh (geometry) of the item
     */
    public Mesh getMesh(){ return mesh; }

    /**
     * Rotate along X axis
     * @param angle of rotation (in radians)
     */
    public void rotateX(float angle) {
        orientation.rotateX(angle);
        invalidate();
    }

    /**
     * Rotate along Y axis
     * @param angle of rotation (in radians)
     */
    public void rotateY(float angle) {
        orientation.rotateY(angle);
        invalidate();
    }

    /**
     * Rotate along Z axis
     * @param angle of rotation (in radians)
     */
    public void rotateZ(float angle) {
        orientation.rotateZ(angle);
        invalidate();
    }

    /**
     * Rotation along three axes (Euler angles rotation)
     * @param angleX angle of rotation along axis X (in radians)
     * @param angleY same for axis Y
     * @param angleZ same for axis Z
     */
    public void rotateXYZ(float angleX, float angleY, float angleZ) {
        orientation.rotateXYZ(angleX, angleY, angleZ);
        invalidate();
    }

    /**
     * Rotation of given angle along an axis
     * @param angle angle of rotation in radians
     * @param axis axis of rotation
     */
    public void rotateAxis(float angle, Vector3f axis) {
        orientation.rotateAxis(angle, axis);
        invalidate();
    }

    /**
     * Rotation of given angle along an axis, and aroung the specified center
     * @param angle the angle of rotation (in radians)
     * @param axis the axis of rotation
     * @param center the center of rotation
     */
    public void rotateAxisAround(float angle, Vector3f axis, Vector3f center) {
        axis = axis.normalize();
        // change orientation
        orientation.rotateAxis(angle, axis);

        // change position
        new Matrix4f().rotateAround(new Quaternionf(new AxisAngle4f(angle, axis)), center.x, center.y, center.z)
                .transformPosition(position);

        invalidate();
    }

    /**
     * Rotate along X axis, around center
     * @param angle of rotation (in radians)
     * @param center the center of rotation
     */
    public void rotateXAround(float angle, Vector3f center) {
        rotateAxisAround(angle, new Vector3f(1.0f, 0.0f, 0.0f), center);
    }

    /**
     * Rotate along Y axis, around center
     * @param angle of rotation (in radians)
     * @param center the center of rotation
     */
    public void rotateYAround(float angle, Vector3f center) {
        rotateAxisAround(angle, new Vector3f(0.0f, 1.0f, 0.0f), center);
    }

    /**
     * Rotate along Z axis, around center
     * @param angle of rotation (in radians)
     * @param center the center of rotation
     */
    public void rotateZAround(float angle, Vector3f center) {
        rotateAxisAround(angle, new Vector3f(0.0f, 0.0f, 1.0f), center);
    }

    /**
     * Rotation along three axes (Euler angles rotation), around center
     * @param angleX angle of rotation along axis X (in radians)
     * @param angleY same for axis Y
     * @param angleZ same for axis Z
     * @param center the center of rotation
     */
    public void rotateXYZAround(float angleX, float angleY, float angleZ, Vector3f center) {
        if(!(angleX == 0 && angleY == 0 && angleZ == 0)) {
            AxisAngle4f aaxis = new AxisAngle4f(new Quaternionf().rotationXYZ(angleX
                    , angleY
                    , angleZ)).normalize();
            System.out.println(aaxis.x + " " + aaxis.y + " " + aaxis.z + " ");
            rotateAxisAround(toDegrees(aaxis.angle), new Vector3f(aaxis.x, aaxis.y, aaxis.z), center);
        }
    }

    public boolean showVertexHelpers() { return vertexHelpers; }

    public void enableVertexHelpers() {
        vertexHelpers = true;
    }

    public void disableVertexHelpers() {
        vertexHelpers = false;
    }

    public boolean showNormalHelpers() { return normalHelpers; }

    public void enableNormalHelpers() {
        normalHelpers = true;
    }

    public void disableNormalHelpers() {
        normalHelpers = false;
    }

    public boolean showAxisHelpers() { return axisHelpers; }

    public void enableAxisHelpers() {
        axisHelpers = true;
    }

    public void disableAxisHelpers() {
        axisHelpers = false;
    }

    public boolean doesCastShadows() {
        return castShadows;
    }

    public void setCastShadows(boolean castShadows) {
        this.castShadows = castShadows;
    }


}
