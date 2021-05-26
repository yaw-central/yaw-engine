package yaw;

import loader.Face;
import loader.FaceVertex;
import loader.OBJLoader;
import loader.OBJParser;
import org.joml.Vector3f;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.meshs.Mesh;

import java.io.IOException;
import java.util.*;

/**
 * This test is loading a teapot from an OBJ file and makes it revolve (see TestRevolveAround).
 */
public class LoadingRevolveTest implements UpdateCallback {

    private int nbUpdates = 0;
    private double totalDeltaTime = 0.0;
    private static long deltaRefreshMillis = 1000;
    private long prevDeltaRefreshMillis = 0;
    private ItemObject teapot;
    private float speed = 10;

    public LoadingRevolveTest(ItemObject teapot) {
        this.teapot = teapot;
    }

    public ItemObject getItem() {
        return teapot;
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

        teapot.rotateXYZAround(0f, 1f, 0f, new Vector3f(0f, 0f, 0f));
        teapot.rotateXYZ(0, 1, 0);


    }

    public static void main(String[] args) {

        // Path to the OBJ file
        String filename = "src/java/ressources/objfiles/teapot.obj";

        try {
            // Instantiate the objloader and the obj
            OBJLoader objLoader = new OBJLoader();
            OBJParser obj = new OBJParser(objLoader, filename);
            // Instantiate the world
            World world = new World(0, 0, 800, 600);
            world.getCamera().setPosition(0, 0, 6);


            // -----------------------------
            // Grouping faces lists by their material
            ArrayList<ArrayList<Face>> facesByTextureList = objLoader.createFaceListsByMaterial();

            // Instantiate arrays for the mesh's object
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

                // If no triangle face in the list then go to the next list
                if (triangleList.size() <= 0) continue;

                // -----------------------------------------------
                // Instantiate a map for the FaceVertexs in the triangle list and adding the into the map with an index
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

                // Create lists instead of arrays because we don't know the size in advance
                List<Float> verticesG, verticesN, verticesT;
                verticesG = new ArrayList<>();
                verticesN = new ArrayList<>();
                verticesT = new ArrayList<>();

                // Process each FaceVertex of the faceVertexList and add the geometric, normal and texture verticies into their respective list
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

                // Update mesh's arrays by adding the new list at the end of the current corresponding array
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

//                System.out.println("arrayG" + Arrays.toString(arrayG));
            }

            // Color of the object
            float[] rgb = {0.75f, 0.75f, 0.75f};
            Mesh mesh = world.createMesh(arrayG, arrayN, arrayIndices, rgb);
            // You may have to change the object's scale or coordinates to see it. Here Mickey needs a 0.03f scale
            ItemObject object = world.createItemObject("test", 0f, 0f, -2f, 0.5f, mesh);
            // Set reotation of initial position of the object
            object.rotateY(30f);
            object.rotateX(30f);

            // Create the revolve of the object
            LoadingRevolveTest rObject = new LoadingRevolveTest(object);

            // Launch the revolve of the object
            world.registerUpdateCallback(rObject);
            // Start the world
            world.launch();
            world.waitTermination();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
