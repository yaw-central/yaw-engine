package yaw.engine;



import yaw.engine.camera.Camera;
import yaw.engine.items.ItemObject;
import yaw.engine.mesh.Mesh;
import yaw.engine.shader.ShaderProgram;

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
    private ArrayList<Mesh> notInit;


    public SceneVertex() {
        mMeshMap = new HashMap<>();
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

    // REFACTORING TEST

    /*public synchronized void add(ItemObject pItem) {
        itemAdded = true;
        //retrieve the stored mesh in the item
        MeshOld lMesh = pItem.getMesh();
        List<ItemObject> lItems = mMeshMap.get(lMesh);
        if (lItems == null) {
            lItems = new ArrayList<>();
            mMeshMap.put(lMesh, lItems);
            notInit.add(lMesh);
        }
        lItems.add(pItem);

    }*/

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
    public void cleanUp() {
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

    public void render(Camera pCamera) {
        List<Mesh> lRmListe = new ArrayList<>();
        for (Mesh lMesh : mMeshMap.keySet()) {
            List<ItemObject> lItems = mMeshMap.get(lMesh);
            if (lItems.isEmpty()) {
                lRmListe.add(lMesh);
            } else {
                try {
                    if(notInit.contains(lMesh)){
                        lMesh.initBuffer();
                        notInit.remove(lMesh);
                    }
                    lMesh.render(lItems, pCamera);
                } catch (Exception e) {
                    System.out.println("Erreur scene vertex");
                }
            }
        }
        /*Clean then remove*/
        for (Mesh lMesh : lRmListe) {
            lMesh.cleanUp();
            mMeshMap.remove(lMesh);
        }
    }

    // XXX: remove?
    /*Maybe
    public void update() {
        for (Mesh m : mMeshMap.keySet()) {
            for (Item i : mMeshMap.get(m))
                i.update();
        }
    }*/

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
