package yaw.engine;



import yaw.engine.camera.Camera;
import yaw.engine.items.ItemObject;
import yaw.engine.mesh.Mesh;
import yaw.engine.shader.ShaderManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class representing a scene
 * we manage the rendering efficiency by splitting the meshes in two different structure
 * the first one (notInit) represent the mesh that must not be rendered ( we remove them from the gpu) unless we want to
 * and the second is a map where each mesh has a list of items
 */
public class SceneVertex {
    //old code from a previous attempt to manage a group of scene vertex
    private boolean itemAdded = false;
    private HashMap<Mesh, List<ItemObject>> mMeshMap;
    private HashMap<Mesh, List<ItemObject>> mMeshMapHelperSummit;
    private HashMap<Mesh, List<ItemObject>> mMeshMapHelperNormal;
    private HashMap<Mesh, List<ItemObject>> mMeshMapHelperAxesMesh;
    private ArrayList<Mesh> notInit;


    public SceneVertex() {
        mMeshMap = new HashMap<>();
        mMeshMapHelperSummit = new HashMap<>();
        mMeshMapHelperNormal = new HashMap<>();
        mMeshMapHelperAxesMesh = new HashMap<>();
        notInit = new ArrayList<>();

    }

    /**
     * Add the item in the map if the associated mesh is already a key
     * otherwise the association is created and the mesh is added to the nonInit List
     *
     * @param pItem the item
     */
    public synchronized void add(ItemObject pItem) {
        itemAdded = true;
        //retrieve the stored mesh in the item
        Mesh lMesh = pItem.getMesh();
        List<ItemObject> lItems = mMeshMap.get(lMesh);
        if (lItems == null) {
            lItems = new ArrayList<>();
            mMeshMap.put(lMesh, lItems);
            notInit.add(lMesh);
        }
        lItems.add(pItem);

    }

    /**
     * Remove the specified item from the mMeshMap
     *
     * @param pItem item to be removed
     */
    public void removeItem(ItemObject pItem) {
        List<ItemObject> lItems = mMeshMap.get(pItem.getMesh());
        lItems.remove(pItem);
    }

    /**
     * Invoke the method cleanup on all the active mesh
     */
    public void cleanUp(ShaderManager shaderManager) {
        for (Mesh lMesh : mMeshMap.keySet()) {
            lMesh.cleanUp();
        }
    }

    /**
     * Invoke the method init on all the mesh in the non initialize mesh collection
     */
    /*public void initMesh() {
        for (Mesh lMesh : notInit) {
            lMesh.initBuffer();
        }
        notInit.clear();
    }*/

    /**
     * Invoke the method render on all mesh with associated items
     * otherwise clean then remove mesh which has an empty list of items
     *
     * @param pCamera camera in wich that will render
     */

    public void render(Camera pCamera, ShaderManager shaderManager) {
        List<Mesh> meshesToRemove = new ArrayList<>();


        for (Mesh lMesh : mMeshMap.keySet()) {
            List<ItemObject> lItems = mMeshMap.get(lMesh);
            List<ItemObject> vertexHelpers = new ArrayList<>();
            List<ItemObject> normalHelpers = new ArrayList<>();
            List<ItemObject> axisHelpers = new ArrayList<>();
            if (lItems.isEmpty()) {
                meshesToRemove.add(lMesh);
            } else {
                try {
                    if(notInit.contains(lMesh)){
                        lMesh.initBuffers();
                        notInit.remove(lMesh);
                    }
                    lMesh.renderSetup(pCamera, shaderManager);
                    for (ItemObject item : lItems) {
                        lMesh.renderItem(item, shaderManager);
                        if (item.showVertexHelpers()) {
                            vertexHelpers.add(item);
                        }
                        if (item.showNormalHelpers()) {
                            normalHelpers.add(item);
                        }
                        if (item.showAxisHelpers()) {
                            axisHelpers.add(item);
                        }
                    }

                    lMesh.renderCleanup(shaderManager);

                    lMesh.renderHelperVertices(vertexHelpers, pCamera, shaderManager);
                    lMesh.renderHelperNormals(vertexHelpers, pCamera, shaderManager);
                    lMesh.renderHelperAxes(vertexHelpers, pCamera, shaderManager);

                } catch (Exception e) {
                    System.err.println("Scene vertex rendering error");
                    System.err.println(e);
                }
            }
        }
        /*Clean then remove*/
        for (Mesh lMesh : meshesToRemove) {
            lMesh.cleanUp();
            mMeshMap.remove(lMesh);
        }

    }

    public void renderHelperSummit(Camera pCamera, ShaderManager shaderManager){

        for (Mesh lMesh : mMeshMapHelperSummit.keySet()) {
            List<ItemObject> lItems = mMeshMapHelperSummit.get(lMesh);
            try {
                lMesh.renderHelperVertices(lItems, pCamera, shaderManager);
            } catch (Exception e) {
                System.out.println("Erreur scene vertex Helper Summit");
            }

        }

    }

    public void renderHelperNormal(Camera pCamera, ShaderManager shaderManager){
        for (Mesh lMesh : mMeshMapHelperNormal.keySet()) {
            List<ItemObject> lItems = mMeshMapHelperNormal.get(lMesh);
            try {
                lMesh.renderHelperNormals(lItems, pCamera,shaderManager);
            } catch (Exception e) {
                System.out.println("Erreur scene vertex Helper Normal");
            }

        }
    }

    public void renderHelperAxesMesh(Camera pCamera, ShaderManager shaderManager){
        for (Mesh lMesh : mMeshMapHelperAxesMesh.keySet()) {
            List<ItemObject> lItems = mMeshMapHelperAxesMesh.get(lMesh);
            try {
                lMesh.renderHelperAxes(lItems, pCamera,shaderManager);
            } catch (Exception e) {
                System.out.println("Erreur scene vertex Helper AxesMesh");
            }

        }
    }

    /**
     * Retrieve all the items of the scene
     *
     * @return the list of item
     */
    public ArrayList<ItemObject> getItemsList() {
        ArrayList<ItemObject> lItems = new ArrayList<>();
        mMeshMap.values().forEach(lItems::addAll);
        return lItems;
    }

    public boolean isItemAdded() {
        return itemAdded;
    }

    public HashMap<Mesh, List<ItemObject>> getMeshMap() {
        return mMeshMap;
    }

}
