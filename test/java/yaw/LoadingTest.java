package yaw;

import loader.v2.Face;
import loader.v2.FaceVertex;
import loader.v2.OBJLoader;
import loader.v2.OBJParser;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.meshs.Mesh;

import java.io.IOException;
import java.util.*;

/**
 * This test is the basic test for loading an OBJ file and display it.
 * If you want to display another object, pay attention to the fact that all scales exist and you could need
 * to hand-change the object position and/or scale.
 * TODO : make the textures work ! The structure is already prepared to apply them.
 */
public class LoadingTest {
    public static void main(String[] args) {

        // path to the OBJ file
        String filename = "src/java/ressources/objfiles/mickey.obj";

        try {
//            instantiate the objloader and the obj
            OBJLoader objLoader = new OBJLoader();
            OBJParser obj = new OBJParser(objLoader, filename);

//            instantiate the world
            World world = new World(0, 0, 800, 600);
            world.getCamera().setPosition(0, 0, 6);

            // -----------------------------

            // Grouping faces lists by their material
            ArrayList<ArrayList<Face>> facesByTextureList = objLoader.createFaceListsByMaterial();

            // instantiate arrays for the mesh's object
            float[] arrayG = new float[0];
            float[] arrayN = new float[0];
            float[] arrayT = new float[0];
            int[] arrayIndices = new int[0];

            // Iterating on all faces groups to apply textures
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

            //create a random color for the object
            float[] rgb = {(float) Math.random(), (float) Math.random(), (float) Math.random()};
            Mesh mesh = world.createMesh(arrayG, arrayN, arrayIndices, rgb);
            // You may have to change the object's scale or coordinates to see it. Here Mickey needs a 0.03f scale
            ItemObject object = world.createItemObject("test", 0f, 0f, -10f, 0.03f, mesh);

            //start the world
            world.launch();
            world.waitTermination();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
