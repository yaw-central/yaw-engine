package yaw;

import loader.v2.Face;
import loader.v2.FaceVertex;
import loader.v2.OBJLoader;
import loader.v2.OBJParser;
import org.joml.Vector3f;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.meshs.Mesh;
import yaw.engine.meshs.MeshBuilder;
import yaw.engine.meshs.Texture;

import java.io.IOException;
import java.util.*;

/**
 * This test is loading a prism from an OBJ file and makes it rotate (see RotatingCube).
 */
public class LoadingRotatingTest implements UpdateCallback {

    private int nbUpdates = 0;
    private double totalDeltaTime = 0.0;
    private static long deltaRefreshMillis = 1000;
    private long prevDeltaRefreshMillis = 0;
    private ItemObject prism;
    private float speed = 4;

    public LoadingRotatingTest(ItemObject prism) {
        this.prism = prism;
    }

    public ItemObject getItem() {
        return prism;
    }

    @Override
    public void update(double deltaTime) {
        nbUpdates++;
        totalDeltaTime += deltaTime;

        long currentMillis = System.currentTimeMillis();
        if (currentMillis - prevDeltaRefreshMillis > deltaRefreshMillis) {
            double avgDeltaTime = totalDeltaTime / (double) nbUpdates;
            System.out.println("Average deltaTime = " + Double.toString(avgDeltaTime) + " s (" + nbUpdates + ")");
            nbUpdates = 0;
            totalDeltaTime = 0.0;
            prevDeltaRefreshMillis = currentMillis;
        }

        //prism.rotateXYZ(0f, 3.1415925f * speed * (float) deltaTime, 0f);
        //prism.rotateZAround(1f, new Vector3f(0f, 0f, -3f));
        prism.rotateZ(3.1415925f * speed * (float) deltaTime);
        prism.rotateXYZAround(0f, 3.1415925f * speed * (float) deltaTime, 0f, new Vector3f(0f, 0f, -10f));
        //prism.rotateX(0.0f);


    }

    public static void main(String[] args) {

        // path to the OBJ file
        String filename = "src/java/ressources/objfiles/tree.obj";

        try {
            //            instantiate the objloader and the obj
            OBJLoader objLoader = new OBJLoader();
            OBJParser obj = new OBJParser(objLoader, filename);

            //            instantiate the world
            World world = new World(0, 0, 800, 600);
            world.getCamera().setPosition(0, 1, 0);
            world.getCamera().rotate(0, 10, 0);

//            create a cube from an old existing methode to compare the result
            Mesh tmpcube = MeshBuilder.generateBlock(1, 1, 1);
            ItemObject cube = world.createItemObject("cube", -5f, 0f, -5f, 1.0f, tmpcube);
            cube.getMesh().getMaterial().setTexture(new Texture("/ressources/diamond.png"));

            // -----------------------------

            // Grouping faces lists by their material
            ArrayList<ArrayList<Face>> facesByTextureList = objLoader.createFaceListsByMaterial();

            // instantiate arrays for the mesh's object
            float[] arrayG = new float[0];
            float[] arrayN = new float[0];
            float[] arrayT = new float[0];
            int[] arrayIndices = new int[0];

            // Parcours des faces pour appliquer la texture
            for (ArrayList<Face> faceList : facesByTextureList) {
                if (faceList.isEmpty()) continue;

                // Split the 4-sided faces into triangles
                ArrayList<Face> triangleList = objLoader.splitQuads(faceList);
                // Initialize the missing normal vertices from the new triangles
                objLoader.calcMissingVertexNormals(triangleList);

                //                if no triangle face in the list then go to the next list
                if (triangleList.size() <= 0) continue;

                // -----------------------------------------------
//                instantiate a map for the FaceVertexs in the triangle list and adding the into the map with an index
                Map<FaceVertex, Integer> indexMap = new HashMap<>();
                int nextVertexIndex = 0;
                ArrayList<FaceVertex> faceVertexList = new ArrayList<>();
                for (Face face : triangleList) {
                    for (FaceVertex vertex : face.getVertices()) {
                        if (!indexMap.containsKey(vertex)) {
                            indexMap.put(vertex, nextVertexIndex++);
                            faceVertexList.add(vertex);
                        }
                    }
                }
//                create lists instead of arrays because we don't know the size in advance
                List<Float> verticesG, verticesN, verticesT;
                verticesG = new ArrayList<>();
                verticesN = new ArrayList<>();
                verticesT = new ArrayList<>();

//                process each FaceVertex of the faceVertexList and add the geometric, normal and texture verticies into their respective list
                for (FaceVertex vertex : faceVertexList) {
                    verticesG.add(vertex.geometric.getX());
                    verticesG.add(vertex.geometric.getY());
                    verticesG.add(vertex.geometric.getZ());
                    if (vertex.normal == null) {
                        verticesN.add(1.0f);
                        verticesN.add(1.0f);
                        verticesN.add(1.0f);
                    } else {
                        verticesN.add(vertex.normal.getX());
                        verticesN.add(vertex.normal.getY());
                        verticesN.add(vertex.normal.getZ());
                    }
                    if (vertex.texture == null) {
                        verticesT.add((float) Math.random());
                        verticesT.add((float) Math.random());
                    } else {
                        verticesT.add(vertex.texture.u);
                        verticesT.add(vertex.texture.v);
                    }
                }

//                update mesh's arrays by adding the new list at the end of the current corresponding array
                int old_size = arrayG.length;
                arrayG = Arrays.copyOf(arrayG, old_size + verticesG.size());
                for (int i = old_size; i < arrayG.length; i++) {
                    arrayG[i] = verticesG.get(i);
                }
                old_size = arrayN.length;
                arrayN = Arrays.copyOf(arrayN, old_size + verticesN.size());
                for (int i = old_size; i < arrayN.length; i++) {
                    arrayN[i] = verticesN.get(i);
                }

                old_size = arrayT.length;
                arrayT = Arrays.copyOf(arrayT, old_size + verticesT.size());
                for (int i = old_size; i < arrayT.length; i++) {
                    arrayT[i] = verticesT.get(i);
                }

                List<Integer> indices;
                indices = new ArrayList<>();
                for (Face face : triangleList) {
                    for (FaceVertex vertex : face.getVertices()) {
                        int index = indexMap.get(vertex);
                        indices.add(index);
                    }
                }

                old_size = arrayIndices.length;
                arrayIndices = Arrays.copyOf(arrayIndices, old_size + indices.size());
                for (int i = old_size; i < arrayIndices.length; i++) {
                    arrayIndices[i] = indices.get(i);
                }

                //-----------------------------------------------


            }
            // color of the object
            float[] rgb = {0.75f, 0.75f, 0.75f};
            Mesh mesh = world.createMesh(arrayG, arrayN, arrayIndices, rgb);

            // You may have to change the object's scale or coordinates to see it. Here Mickey needs a 0.03f scale
            ItemObject prism = world.createItemObject("test", 0f, 0f, -15f, 0.5f, mesh);
            // set reotation of initial position of the object
            prism.rotateY(30f);
            prism.rotateX(30f);
            prism.getMesh().getMaterial().setTexture(new Texture("/ressources/diamond.png"));

            // create the rotation of the object
            LoadingRotatingTest loadRotatingTest = new LoadingRotatingTest(prism);

            // launch the rotation of the object
            world.registerUpdateCallback(loadRotatingTest);
            //start the world
            world.launch();
            world.waitTermination();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
