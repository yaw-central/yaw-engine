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
                0, 1,
                2, 3,
                4, 5
        };

        Mesh axes = new Mesh(vertices, null, null,indices,6);
        axes.setDrawingStrategy(new AxesHelperDrawingStrategy());

        return axes;
    }
}
