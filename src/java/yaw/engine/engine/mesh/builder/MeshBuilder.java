package yaw.engine.mesh.builder;

import yaw.engine.mesh.Mesh;

public interface MeshBuilder {
    /**
     * Generate a Mesh from an algorithmic build process.
     * @return a Mesh ready for usage in Yaw
     */
    public Mesh generate();
}
