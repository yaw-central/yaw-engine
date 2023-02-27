package yaw.engine.mesh.strategy;

import yaw.engine.mesh.Mesh;
import yaw.engine.mesh.MeshDrawingStrategy;

import static org.lwjgl.opengl.GL11.*;

public class AxesHelperDrawingStrategy implements MeshDrawingStrategy {

    @Override
    public void drawMesh(Mesh pMesh) {
        glDrawElements(GL_LINES, pMesh.getIndices().length, GL_UNSIGNED_INT, 0);
    }

}
