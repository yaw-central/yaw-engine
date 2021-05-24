package yaw.engine;

import loader.v1.Model;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import yaw.engine.camera.Camera;
import yaw.engine.items.HitBox;
import yaw.engine.items.ItemGroup;
import yaw.engine.items.ItemObject;
import yaw.engine.light.SceneLight;
import yaw.engine.meshs.Material;
import yaw.engine.meshs.Mesh;
import yaw.engine.meshs.Texture;
import yaw.engine.meshs.strategy.DefaultDrawingStrategy;
import yaw.engine.skybox.Skybox;

import java.util.Vector;

/**
 * This is the facade of the engine, most Clojure calls are
 * made on an instance of this object. The stateful part
 * is delegated to the underlying MainLoop.
 */
public class World {

    // ========== Attributes ==========

    private MainLoop mainLoop;
    private Thread mloopThread = null;
    private boolean isRunning = false;

    // ========== Constructors ==========

    /**
     * Initializes the elements to create the window
     *
     * @param pInitX      initX
     * @param pInitY      initY
     * @param pInitWidth  initWidth
     * @param pInitHeight initHeight
     * @param pInitVSYNC  initVSYNC
     */
    public World(int pInitX, int pInitY, int pInitWidth, int pInitHeight, boolean pInitVSYNC) {
        this.mainLoop = new MainLoop(pInitX, pInitY, pInitWidth, pInitHeight, pInitVSYNC);
    }

    /**
     * Initializes the elements to create the window
     *
     * @param pInitX      initX
     * @param pInitY      initY
     * @param pInitWidth  initWidth
     * @param pInitHeight initHeight
     */
    public World(int pInitX, int pInitY, int pInitWidth, int pInitHeight) {
        this.mainLoop = new MainLoop(pInitX, pInitY, pInitWidth, pInitHeight);
    }

    public World() {
        this.mainLoop = new MainLoop();
    }

    public void launch() {
        if (isRunning) {
            throw new Error("World is already running (multiple calls to launch() method)");
        }
        isRunning = true;
        mloopThread = new Thread(mainLoop);
        mloopThread.start();
    }


    // ========== Methods ==========


    /**
     * Input of critical section, allows to protect the resource share loop.
     * Stop the game loop and stop the thread that manage the world.
     */
    public void terminate() throws InterruptedException {
        mainLoop.close();
    }

    public void waitTermination() {
        try {
            mloopThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create an item with the specified parameters and add it to the world
     *
     * @param id     id
     * @param x      x coordinate
     * @param y      y coordinate
     * @param z      z coordinate
     * @param pScale scale
     * @param pMesh  mesh
     * @return the item
     */
    public ItemObject createItemObject(String id, float x, float y, float z, float pScale, Mesh pMesh) {
        ItemObject lItem = new ItemObject(id, new Vector3f(x, y, z), new Quaternionf(), pScale, pMesh);
        mainLoop.addToScene(lItem);
        return lItem;
    }

    /**
     * Create a mesh with the specified parameters
     * MeshOld won't be load into the graphic cards unless you bind it to an item
     *
     * @param pVertices    vertices
     * @param pTextCoords  texture coordonates
     * @param pNormals     normals
     * @param pIndices     indices
     * @param pWeight      weight
     * @param rgb          rgb array
     * @param pTextureName texture path
     * @return the mesh
     */
    public Mesh createMesh(float[] pVertices, float[] pTextCoords, float[] pNormals, int[] pIndices, int pWeight, float[] rgb, String pTextureName) {
        if (rgb.length != 3) {
            throw new RuntimeException("RGB must represent 3 colors");
        }
        Vector3f lMaterialColor = new Vector3f(rgb[0], rgb[1], rgb[2]);
        Material lMaterial = new Material(lMaterialColor);

        // Texture part
        if (pTextureName != null && !pTextureName.isEmpty()) {
            Texture lTexture = mainLoop.fetchTexture(pTextureName);
            if (lTexture == null) { // ?????
                lTexture = new Texture(pTextureName);
            } else {
                System.err.println("[Warning] Texture not found: " + pTextureName);
            }
            lMaterial.setTexture(lTexture);
        }
        Mesh lMesh = new Mesh(pVertices, pTextCoords, pNormals, pIndices, pWeight);
        lMesh.setDrawingStrategy(new DefaultDrawingStrategy());
        lMesh.setMaterial(lMaterial);
        return lMesh;
    }

    /**
     * Create a mesh with the specified parameters
     * MeshOld won't be load into the graphic cards unless you bind it to an item
     *
     * @param pVertices vertices
     * @param pNormals  normals
     * @param pIndices  indices
     * @return the mesh
     */
    public Mesh createMesh(float[] pVertices, float[] pNormals, int[] pIndices, float[] rgb) {
        if (rgb.length != 3) {
            throw new RuntimeException("RGB must represent 3 colors");
        }
        Vector3f lMaterialColor = new Vector3f(rgb[0], rgb[1], rgb[2]);
        Material lMaterial = new Material(lMaterialColor);
        Mesh lMesh = new Mesh(pVertices, pNormals, pIndices);
        lMesh.setDrawingStrategy(new DefaultDrawingStrategy());
        lMesh.setMaterial(lMaterial);
        return lMesh;
    }


    /**
     * Create a bounding box with the specified parameters and add it to the  world
     *
     * @param id     id
     * @param x      x coordinate
     * @param y      y coordinate
     * @param z      z coordinate
     * @param pScale scale
     * @return BoundingBox
     */
    public HitBox createHitBox(String id, float x, float y, float z, float pScale, float xLength, float yLength, float zLength) {
        HitBox hb = new HitBox(id, new Vector3f(x, y, z), new Quaternionf(), pScale, xLength, yLength, zLength);
        mainLoop.addToScene(hb);
        return hb;
    }


    /**
     * Detects the collision between two hitboxes
     *
     * @param hb1 the first hitbox
     * @param hb2 the second hitbox
     * @return true if they are in collision, false otherwise
     */
    public boolean isInCollision(HitBox hb1, HitBox hb2) {
        return hb1.collidesWith(hb2);
    }

    public void registerUpdateCallback(UpdateCallback cb) {
        mainLoop.registerUpdateCallback(cb);
    }

    public void registerInputCallback(InputCallback callback) {
        mainLoop.registerInputCallback(callback);
    }

    // 3D click
    public void registerMouseCallback(Mouse3DClickCallBack callback) {
        mainLoop.registerMouse3DClickCallBack(callback);
    }

    // ========== Getters ==========

    public Camera getCamera() {
        return mainLoop.getCamera();
    }

    public Vector<Camera> getCamerasList() {
        return mainLoop.getCamerasList();
    }

    public SceneLight getSceneLight() {
        return mainLoop.getSceneLight();
    }

    public Skybox getSkybox() {
        return mainLoop.getSkybox();
    }

    // ========== Setters ==========

    public void clearCameras() {
        mainLoop.clearCameras();
    }

    /**
     * Adds a camera and its index in the list of cameras (not in live)
     *
     * @param pIndex  index
     * @param pCamera camera
     */
    public void addCamera(int pIndex, Camera pCamera) {
        mainLoop.addCamera(pIndex, pCamera);
    }

    public void setCamera(Camera pCamera) {
        mainLoop.setCamera(pCamera);
    }

    public ItemGroup createGroup(String id) {
        return mainLoop.createGroup(id);
    }

    public void removeGroup(ItemGroup pGroup) {
        mainLoop.removeGroup(pGroup);
    }

    public Vector<ItemGroup> getItemGroupArrayList() {
        return mainLoop.getItemGroupArrayList();
    }

    public void setSkybox(Skybox pSkybox) {
        mainLoop.setSkybox(pSkybox);
    }

    public void removeItem(ItemObject pItem) {
        mainLoop.removeFromScene(pItem);
    }

    /**
     * Sets the skybox by creating a new one from parameters values
     *
     * @param pWidth  the new skybox's width
     * @param pLength the new skybox's length
     * @param pHeight the new skybox's height
     * @param pR      the new skybox's red intensity
     * @param pG      the new skybox's green intensity
     * @param pB      the new skybox's blue intensity
     */
    public void setSkybox(float pWidth, float pLength, float pHeight, float pR, float pG, float pB) {
        Skybox lSkybox = new Skybox(pWidth, pLength, pHeight, new Vector3f(pR, pG, pB));
        setSkybox(lSkybox);
    }

    public void removeSkybox() {
        mainLoop.removeSkybox();
    }

    public ItemObject addModel(String id, float x, float y, float z, float pScale, Model m) {
        float[] vertices = new float[m.getVertices().size() * 3];
        float[] normals = new float[m.getNormals().size() * 3];
        float[] rgb = {75, 75, 75};

        for (int i = 0; i < m.getVertices().size(); i++) {
            Vector3f current = m.getVertices().get(i);
            vertices[i*3] = current.x;
            vertices[i*3 + 1] = current.y;
            vertices[i*3 + 2] = current.z;
        }

        for (int i = 0; i < m.getNormals().size(); i++) {
            Vector3f current = m.getNormals().get(i);
            normals[i*3] = current.x;
            normals[i*3 + 1] = current.y;
            normals[i*3 + 2] = current.z;
        }
        Mesh mesh = createMesh(vertices, normals, m.getpIndices(), rgb);
        ItemObject res = createItemObject(id, x, y, z, pScale, mesh);
        return res;

    }
}

