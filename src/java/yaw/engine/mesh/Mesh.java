package yaw.engine.mesh;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import yaw.engine.camera.Camera;
import yaw.engine.geom.Geometry;
import yaw.engine.items.ItemObject;
import yaw.engine.light.LightModel;
import yaw.engine.mesh.strategy.DefaultDrawingStrategy;
import yaw.engine.shader.ShaderProgram;
import yaw.engine.shader.ShaderProgramADS;
import yaw.engine.shader.ShaderProperties;
import yaw.engine.util.LoggerYAW;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

/**
 * A Mesh is the visual component of a 3D object. It comprises :
 *
 *   - a geometry with vertices, normals, etc.
 *   - a material
 *
 *   The class is responsible for the rendering of an object, but
 *   several objects can share the same Mesh.
 */
public class Mesh {
    private final List<Integer> vboIdList;
    //reference to the VAO(wrapper)
    private int vaoId;

    private Geometry geometry;

    private Material material;

    private final Map<String, String> attributes;

    //strategy when we draw the elements
    private MeshDrawingStrategy drawingStrategy;
    private boolean drawADS;

    /**
     * Construct a Mesh
     *
     * @param geometry    The Geometry of the Mesh
     * @param material    The Material of the Mesh
     */
    public Mesh(Geometry geometry, Material material) {
        this.geometry = geometry;
        this.material = material;
        this.attributes = new HashMap<>();
        this.vboIdList = new ArrayList<>();
        this.drawADS = false;
        drawingStrategy = new DefaultDrawingStrategy();
    }

    public Mesh(Geometry geometry) {
        this(geometry, new Material());
    }

    public ShaderProperties getShaderProperties(LightModel lightModel) {
        return new ShaderProperties(lightModel.hasDirectionalLight,
                lightModel.maxPointLights,
                lightModel.maxSpotLights,
                material.isTextured(),
                material.withShadows && lightModel.hasDirectionalLight);
    }

    /**
     * Initialize  vertex, mNormals, mIndices and mTextureCoordinate buffer
     */
    public void initBuffers() {
        //initialization order is important do not change unless you know what to do
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        //Initialization of VBO

        //VBO of vertex layout 0 in vertShader.vs
        float[] vertices = geometry.getVertices();
        FloatBuffer verticeBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticeBuffer.put(vertices).flip();
        int lVboVertexId = glGenBuffers();
        vboIdList.add(lVboVertexId);
        glBindBuffer(GL_ARRAY_BUFFER, lVboVertexId);
        glBufferData(GL_ARRAY_BUFFER, verticeBuffer, GL_STATIC_DRAW);

        //We explain to OpenGL how to read our Buffers.
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        // Texture coordinates VBO
        int lVboCoordTextureId = glGenBuffers();
        vboIdList.add(lVboCoordTextureId);
        float[] textCoords = geometry.getTextCoords();
        FloatBuffer textCoordsBuffer = BufferUtils.createFloatBuffer(textCoords.length);
        textCoordsBuffer.put(textCoords).flip();
        glBindBuffer(GL_ARRAY_BUFFER, lVboCoordTextureId);
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        //VBO of mNormals layout 2 in vertShader.vs
        float[] normals = geometry.getNormals();
        FloatBuffer normBuffer = BufferUtils.createFloatBuffer(normals.length);
        normBuffer.put(normals).flip();
        int lVboNormId = glGenBuffers();
        vboIdList.add(lVboNormId);
        glBindBuffer(GL_ARRAY_BUFFER, lVboNormId);
        glBufferData(GL_ARRAY_BUFFER, normBuffer, GL_STATIC_DRAW);

        //We explain to OpenGL how to read our Buffers.
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        //VBO of mIndices
        int[] indices = geometry.getIndices();
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();
        int lVboIndicesId = glGenBuffers();
        vboIdList.add(lVboIndicesId);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, lVboIndicesId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

    }

    public void renderSetup(Camera pCamera, ShaderProgramADS shaderProgram) {
        initRender();
        shaderProgram.bind();
        /* Set the camera to render. */
        shaderProgram.setUniform("worldMatrix", pCamera.getWorldMat());
        shaderProgram.setUniform("camera_pos", pCamera.getPosition());

        /* uniforms PBR */
        shaderProgram.setUniform("material.texture_sampler", 0);
        shaderProgram.setUniform("material.specularMap", 1);
        //shaderProgram.setUniform("material.normalMap", 2);

        shaderProgram.setUniform("material", material);
    }

    public void renderItem(ItemObject item, ShaderProgram shaderProgram) {
        shaderProgram.setUniform("modelMatrix", item.getModelMatrix());
        Matrix3f normalMatrix = new Matrix3f(item.getModelMatrix());
        normalMatrix.invert().transpose();
        shaderProgram.setUniform("normalMatrix", normalMatrix);
        if (drawingStrategy != null) {
            //delegate the drawing
            drawingStrategy.drawMesh(this);
        } else {
            LoggerYAW.getLogger().severe("No drawing strategy has been set for the mesh");
            throw new RuntimeException("No drawing strategy has been set for the mesh");
        }
    }

    public void renderCleanup(ShaderProgram shaderProgram) {
        shaderProgram.unbind();
        endRender();
    }

    public void renderHelperVertices(List<ItemObject> pItems, Camera pCamera, ShaderProgram helperProgram) {
        //initRender
        initRender();
        helperProgram.bind();
        helperProgram.setUniform("projectionMatrix", pCamera.getProjectionMat());
        Matrix4f viewMat = pCamera.getViewMat();
        helperProgram.setUniform("viewMatrix", viewMat);
        for (ItemObject lItem : pItems) {
            helperProgram.setUniform("modelMatrix", lItem.getModelMatrix());
            glDrawElements(GL_POINTS, geometry.getIndices().length, GL_UNSIGNED_INT, 0);
        }

        helperProgram.unbind();
        endRender();

    }

    public void renderHelperNormals(List<ItemObject> pItems, Camera pCamera, ShaderProgram helperProgram) {
        //initRender
        initRender();

        helperProgram.bind();
        helperProgram.setUniform("projectionMatrix", pCamera.getProjectionMat());
        Matrix4f viewMat = pCamera.getViewMat();
        helperProgram.setUniform("viewMatrix", viewMat);
        for (ItemObject lItem : pItems) {
            helperProgram.setUniform("modelMatrix", lItem.getModelMatrix());
            glDrawElements(GL_POINTS, geometry.getIndices().length, GL_UNSIGNED_INT, 0);
        }

        helperProgram.unbind();
        endRender();
    }

    public void renderHelperAxes(List<ItemObject> pItems, Camera pCamera, ShaderProgram helperProgram) {
        initRender();
        helperProgram.bind();
        helperProgram.setUniform("projectionMatrix", pCamera.getProjectionMat());

        Matrix4f viewMat = pCamera.getViewMat();
        helperProgram.setUniform("viewMatrix", viewMat);
        for (ItemObject lItem : pItems) {
            helperProgram.setUniform("center", lItem.getPosition());
            helperProgram.setUniform("modelMatrix", lItem.getModelMatrix());
            glDrawElements(GL_LINES, geometry.getIndices().length, GL_UNSIGNED_INT, 0);
        }

        helperProgram.unbind();
        endRender();
    }


    public void cleanUp() {
        //de-allocation of VAO and VBO
        glDisableVertexAttribArray(0);

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }
        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanup();
        }

        Texture spec = material.getSpecularTexture();
        if (spec != null) spec.cleanup();

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);


    }

    /**
     * Returns the value to which the specified @attributeName is mapped,
     * or null if this map contains no mapping for the key.
     *
     * @param pAttributeName name of the attribute (most of the time it will be clojure keywords)
     * @return the corresponding value if exist null otherwise
     */
    public Object getAttribute(String pAttributeName) {
        return this.attributes.get(pAttributeName);
    }

    /**
     * Copies all of the mappings from the specified map to this map (optional operation).
     * The effect of this call is equivalent to that of calling put(k, v) on this map once for each mapping
     * from key k to value v in the specified map.
     * The behavior of this operation is undefined if the specified map is modified while the operation is in progress.
     *
     * @param pOptionalAttributes mappings to be stored in this map
     */
    public void putOptionalAttributes(Map<String, String> pOptionalAttributes) {
        this.attributes.putAll(pOptionalAttributes);
    }

    public void initRender() {

        //glActiveTexture(GL_TEXTURE0);
        //glActiveTexture(GL_TEXTURE1);
        //glBindTexture(GL_TEXTURE_2D, 0);
        //glBindTexture(GL_TEXTURE_2D, 1);
        Texture texture = material != null ? material.getTexture() : null;
        if (texture != null) {
            //load the texture if needed
            if (!texture.isActivated()) {
                texture.init();
            }
            // Activate first texture bank
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getId()); // Binds the texture to its mId
            System.out.println("texutre diffuse"+texture.getId());
            // Bind the texture
            texture.bind();
        }
        Texture specularMap = material != null ? material.getSpecularTexture() : null;
        if (specularMap != null){
            //System.out.println("specular not null");
            if (!specularMap.isActivated()){
                specularMap.init();
            }
            // Activate second texture bank
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, specularMap.getId()); // Binds the texture to its mId
            System.out.println("texutre specular"+specularMap.getId());
            //bind specularMap
            specularMap.bind();
        } else{
            //if no specular map is provided use a default white texture
            if (material != null){
                material.setSpecularTexture(Texture.createWhiteTexture()); // TODO : create this texture only once ? to avoid multiple identical white textures
            }
        }
        // Draw the mesh
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        //color
        glEnableVertexAttribArray(3);
    }

    protected void endRender() {
        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindVertexArray(0);

        //glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material pMaterial) {
        this.material = pMaterial;
    }

    public void setDrawingStrategy(MeshDrawingStrategy pDrawingStrategy) {
        drawingStrategy = pDrawingStrategy;
    }

    public Geometry getGeometry() {
        return geometry;
    }
}
