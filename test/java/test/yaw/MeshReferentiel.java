package test.yaw;

import org.joml.Vector3f;
import yaw.engine.mesh.Mesh;
import yaw.engine.mesh.strategy.AxesHelperDrawingStrategy;

public class MeshReferentiel {
    public static Mesh makeReferentiel(){
        float[] vertices = new float[] {
                0,0,0, 1,0,0,
                0,0,0, 0,1,0,
                0,0,0, 0,0,1
        };

        int[] indices = {
                0, 1, // X axis
                2, 3, // Y axis
                4, 5  // Z axis
        };

        float[] normals = new float[]{
                1,0,0,
                0,1,0,
                0,0,1
        };

        Mesh axes = new Mesh(vertices, null, normals,indices,6);
        axes.setDrawingStrategy(new AxesHelperDrawingStrategy());

        return axes;
    }
}
