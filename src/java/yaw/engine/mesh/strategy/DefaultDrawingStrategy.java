package yaw.engine.mesh.strategy;

import yaw.engine.mesh.MeshDrawingStrategy;
import yaw.engine.mesh.Mesh;

import static org.lwjgl.opengl.GL11.*;


public class DefaultDrawingStrategy implements MeshDrawingStrategy {


    
    public void drawMesh(Mesh pMesh) {
        // Draw the mVertices
        glDrawElements(GL_TRIANGLES, pMesh.getIndices().length, GL_UNSIGNED_INT, 0);
    }

}
