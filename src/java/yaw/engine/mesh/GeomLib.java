package yaw.engine.mesh;

import org.joml.Vector2f;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

public class GeomLib {

    public static List<Vector2f> makeCircleApprox(float cx, float cy, float radius, int nbVertices) {
        List<Vector2f> coords = new ArrayList<>(nbVertices);

        float theta = 0;
        float delta = (float) (Math.PI / nbVertices);
        for (int i=0; i<nbVertices; i++) {
            coords.add(new Vector2f(cx + radius * Math.cos(theta),
                                    cy + radius * Math.sin(theta)));
            theta += delta;
        }
        return coords;
    }

    public static List<Vector2f> makeCircleApprox(int nbVertices) {
        return makeCircleApprox(0, 0, 1.0f, nbVertices);
    }
}
