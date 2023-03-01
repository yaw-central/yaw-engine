package yaw.engine.mesh.strategy;

import org.lwjgl.opengles.GLES20;
import yaw.engine.mesh.MeshDrawingStrategy;
import yaw.engine.mesh.Mesh;

import static org.lwjgl.opengl.GL11.*;


public class DefaultDrawingStrategy implements MeshDrawingStrategy {



    public void drawMesh(Mesh pMesh) {
        // Draw the mVertices
        HelperDrawingStrategy.enableSummitHelper = true;
        glDrawElements(GL_TRIANGLES, pMesh.getIndices().length, GL_UNSIGNED_INT, 0);


    }



}
