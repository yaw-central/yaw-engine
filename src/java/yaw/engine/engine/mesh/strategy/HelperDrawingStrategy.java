package yaw.engine.mesh.strategy;

import org.lwjgl.opengles.GLES20;
import yaw.engine.mesh.Mesh;
import yaw.engine.mesh.MeshDrawingStrategy;
import yaw.engine.shader.ShaderProgram;

import static org.lwjgl.opengl.GL11.*;

public class HelperDrawingStrategy  {

    public static boolean enableSummitHelper;

    public static void drawMesh(Mesh pMesh) {
        enableSummitHelper = false;
        glDrawElements(GL_POINTS, pMesh.getIndices().length, GL_UNSIGNED_INT, 0);
    }
}
