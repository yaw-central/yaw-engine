package yaw.engine.mesh.strategy;

import yaw.engine.mesh.MeshDrawingStrategy;
import yaw.engine.mesh.Mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;


public class BoundingBoxDrawingStrategy implements MeshDrawingStrategy {

    private Boolean isVisible;
    public BoundingBoxDrawingStrategy(Boolean isVisible){
        this.isVisible = isVisible;
    }
    @Override
    public void drawMesh(Mesh pMesh) {
        if(isVisible) {
            glEnable(GL_POLYGON_OFFSET_FILL);
            glEnable(GL_PROGRAM_POINT_SIZE);

            glPolygonOffset(1, 0);
            glDrawElements(GL_LINES, pMesh.getIndices().length, GL_UNSIGNED_INT, 0);
        }
    }
}
