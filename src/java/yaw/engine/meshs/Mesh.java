package yaw.engine.meshs;

import org.joml.Vector3f;
import yaw.engine.items.ItemObject;
import yaw.engine.shader.ShaderProgram;
import yaw.engine.util.LoggerYAW;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 *
 */
public class Mesh {
    private final List<Integer> vboIdList;
    //reference to the VAO(wrapper)
    private int mVaoId;
    //VBO's ID

    //VBO
    private float[] mVertices;//mVertices
    private float[] mNormals;
    private int[] mIndices; //order into which  mVertices should be drawn by referring to their  position
    private float[] mTextCoords;
    private int mWeight;  // the mWeight of an object in a group (e.g. a mass in a group planets)
    private Material mMaterial;
    private Map<String, String> mOptionalAttributes;
    //strategy when we draw the elements
    private MeshDrawingStrategy mDrawingStrategy;

    /**
     * Construct a Mesh with the specified mMaterial , mVertices, mNormals , mTextureCoordinate and mIndices.
     *
     * @param pVertices   Vertex array
     * @param pTextCoords texture coodinate
     * @param pNormals    mNormals
     * @param pIndices    order into which  mVertices should be drawn by referring to their  position
     */
    public Mesh(float[] pVertices, float[] pTextCoords, float[] pNormals, int[] pIndices) {
        this(pVertices, pTextCoords, pNormals, pIndices, pVertices.length);
    }

    /**
     * Construct a Mesh with the specified mVertices, mNormals and mIndices.
     *
     * @param pVertices   Vertex array
     * @param pNormals    Normal vectors
     * @param pIndices    Triangles
     */
    public Mesh(float[] pVertices, float[] pNormals, int[] pIndices) {
        this(pVertices, null, pNormals, pIndices, pVertices.length);
    }

    public Mesh(float[] pVertices, int[] pIndices) {
        this(pVertices, null, null, pIndices, pVertices.length);
    }

    /**
     * Construct a Mesh with the specified  mVertices, mNormals, mIndices , mTextureCoordinate and mWeight
     *
     * @param pVertices   Vertex array
     * @param pTextCoords texture coodinate
     * @param pNormals    mNormals
     * @param pIndices    order into which  mVertices should be drawn by referring to their  position
     * @param pWeight     mWeight numbre of vertices
     */
    public Mesh(float[] pVertices, float[] pTextCoords, float[] pNormals, int[] pIndices, int pWeight) {
        this.mMaterial = new Material();
        this.mVertices = pVertices;
        this.mIndices = pIndices;
        this.mNormals = pNormals == null ? generateNormals() : pNormals;
        this.mWeight = pWeight;
        this.mTextCoords = pTextCoords == null ? new float[1] : pTextCoords;
        this.mOptionalAttributes = new HashMap<>();
        this.vboIdList = new ArrayList<>();
    }

    /**
     * Initialize  vertex, mNormals, mIndices and mTextureCoordinate buffer
     */
    public void init() {
        //initialization order is important do not change unless you know what to do
        mVaoId = glGenVertexArrays();
        glBindVertexArray(mVaoId);

        //Initialization of VBO

        //VBO of vertex layout 0 in vertShader.vs
        FloatBuffer verticeBuffer = BufferUtils.createFloatBuffer(mVertices.length);
        verticeBuffer.put(mVertices).flip();
        int lVboVertexId = glGenBuffers();
        vboIdList.add(lVboVertexId);
        glBindBuffer(GL_ARRAY_BUFFER, lVboVertexId);
        glBufferData(GL_ARRAY_BUFFER, verticeBuffer, GL_STATIC_DRAW);

        //We explain to OpenGL how to read our Buffers.
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        // Texture coordinates VBO
        int lVboCoordTextureId = glGenBuffers();
        vboIdList.add(lVboCoordTextureId);
        FloatBuffer textCoordsBuffer = BufferUtils.createFloatBuffer(mTextCoords.length);
        textCoordsBuffer.put(mTextCoords).flip();
        glBindBuffer(GL_ARRAY_BUFFER, lVboCoordTextureId);
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        //VBO of mNormals layout 2 in vertShader.vs
        FloatBuffer normBuffer = BufferUtils.createFloatBuffer(mNormals.length);
        normBuffer.put(mNormals).flip();
        int lVboNormId = glGenBuffers();
        vboIdList.add(lVboNormId);
        glBindBuffer(GL_ARRAY_BUFFER, lVboNormId);
        glBufferData(GL_ARRAY_BUFFER, normBuffer, GL_STATIC_DRAW);

        //We explain to OpenGL how to read our Buffers.
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        //VBO of mIndices
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(mIndices.length);
        indicesBuffer.put(mIndices).flip();
        int lVboIndicesId = glGenBuffers();
        vboIdList.add(lVboIndicesId);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, lVboIndicesId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    /**
     * Render the specified items
     *
     * @param pItems         item
     * @param pShaderProgram shaderProgram
     */
    public void render(List<ItemObject> pItems, ShaderProgram pShaderProgram) {

        //initRender
        initRender();


        pShaderProgram.setUniform("material", mMaterial);
        for (ItemObject lItem : pItems) {
            //can be moved to Item class
            //Matrix4f modelViewMat = new Matrix4f(pViewMatrix).mul(lItem.getWorldMatrix());

            pShaderProgram.setUniform("modelMatrix", lItem.getWorldMatrix());
            if (mDrawingStrategy != null) {
                //delegate the drawing
                mDrawingStrategy.drawMesh(this);
            } else {
                LoggerYAW.getLogger().severe("No drawing strategy has been set for the mesh");
                throw new RuntimeException("No drawing strategy has been set for the mesh");
            }

        }
        //end render

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
        Texture texture = mMaterial.getTexture();
        if (texture != null) {
            texture.cleanup();
        }
        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(mVaoId);


    }

    /**
     * Returns the value to which the specified @attributeName is mapped,
     * or null if this map contains no mapping for the key.
     *
     * @param pAttributeName name of the attribute (most of the time it will be clojure keywords)
     * @return the corresponding value if exist null otherwise
     */
    public Object getAttribute(String pAttributeName) {
        return this.mOptionalAttributes.get(pAttributeName);
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
        this.mOptionalAttributes.putAll(pOptionalAttributes);
    }

    public void initRender() {

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);

        Texture texture = mMaterial != null ? mMaterial.getTexture() : null;
        if (texture != null) {
            //load the texture if needed
            if (!texture.isActivated()) {
                texture.init();
            }
            // Activate first texture bank
            glActiveTexture(GL_TEXTURE0);


            // Bind the texture
            texture.bind();
        }

        // Draw the mesh
        glBindVertexArray(mVaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

    }

    protected void endRender() {
        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        //glBindTexture(GL_TEXTURE_2D, 0);
    }

    private Vector3f getVec(float[] arr, int i) {
        return new Vector3f(arr[i], arr[i+1], arr[i+2]);
    }

    private void setVec(float[] arr, int i, Vector3f vec) {
        arr[i] = vec.x;
        arr[i+1] = vec.y;
        arr[i+2] = vec.z;
    }

    public float[] generateNormals() {
        float[] normals = new float[mVertices.length];

        for(int i = 0; i<mIndices.length; i+=3) {
            var i1 = mIndices[i]*3;
            var i2 = mIndices[i+1]*3;
            var i3 = mIndices[i+2]*3;

            var v1 = getVec(mVertices, i1);
            var v2 = getVec(mVertices, i2);
            var v3 = getVec(mVertices, i3);

            var n1 = getVec(normals, i1);
            var n2 = getVec(normals, i2);
            var n3 = getVec(normals, i3);

            var trinorm = v2.sub(v1).cross(v3.sub(v1)).normalize();

            setVec(normals, i1, n1.add(trinorm));
            setVec(normals, i2, n2.add(trinorm));
            setVec(normals, i3, n3.add(trinorm));
        }

        for(int i = 0; i<normals.length; i+=3) {
            var n = getVec(normals, i);
            setVec(normals, i, n.normalize());
        }

        return normals;
    }

    public float[] getVertices() {
        return mVertices;
    }

    public Material getMaterial() {
        return mMaterial;
    }

    public void setMaterial(Material pMaterial) {
        this.mMaterial = pMaterial;
    }

    public float[] getNormals() {
        return mNormals;
    }

    public int[] getIndices() {
        return mIndices;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setDrawingStrategy(MeshDrawingStrategy pDrawingStrategy) {
        mDrawingStrategy = pDrawingStrategy;
    }

    public void setTextCoords(float[] pTextCoord) {
        mTextCoords = pTextCoord;
    }
}
