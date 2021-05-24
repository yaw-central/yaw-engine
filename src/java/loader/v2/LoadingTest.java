package loader.v2;

import yaw.LoadingRotatingTest;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.meshs.Mesh;
import yaw.engine.meshs.MeshBuilder;
import yaw.engine.meshs.Texture;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class LoadingTest {
    public static void main(String[] args) {
        String filename = "src/java/ressources/objfiles/mickey.obj";

        try {
            OBJLoaderV2 objLoaderV2 = new OBJLoaderV2();
            OBJParser obj = new OBJParser(objLoaderV2, filename);
            World world = new World(0, 0, 800, 600);

            world.getCamera().setPosition(0, 1, 0);
            world.getCamera().rotate(0, 10, 0);

            // -----------------------------

            ArrayList<ArrayList<Face>> facesByTextureList = objLoaderV2.createFaceListsByMaterial();

            List<ItemObject> test = new ArrayList<>();

            float[] arrayG = new float[0];
            float[] arrayN = new float[0];
            float[] arrayT = new float[0];
            int[] arrayIndices = new int[0];

            // Parcours des faces pour appliquer la texture
            for (ArrayList<Face> faceList : facesByTextureList) {
                if (faceList.isEmpty()) continue;

                ArrayList<Face> triangleList = objLoaderV2.splitQuads(faceList);
                objLoaderV2.calcMissingVertexNormals(triangleList);
//                System.out.println("après process" + triangleList);

                if (triangleList.size() <= 0) continue;

                // -----------------------------------------------

                Map<FaceVertex, Integer> indexMap = new HashMap<>();
                int nextVertexIndex = 0;
                ArrayList<FaceVertex> faceVertexList = new ArrayList<>();
                for (Face face : triangleList) {
                    for (FaceVertex vertex : face.vertices) {
                        if (!indexMap.containsKey(vertex)) {
                            indexMap.put(vertex, nextVertexIndex++);
                            faceVertexList.add(vertex);
                        }
                    }
                }

                List<Float> verticesG, verticesN, verticesT;
                verticesG = new ArrayList<>();
                verticesN = new ArrayList<>();
                verticesT = new ArrayList<>();

                for (FaceVertex vertex : faceVertexList) {
                    verticesG.add(vertex.geometric.x);
                    verticesG.add(vertex.geometric.y);
                    verticesG.add(vertex.geometric.z);
                    if (vertex.normal == null) {
                        verticesN.add(1.0f);
                        verticesN.add(1.0f);
                        verticesN.add(1.0f);
                    } else {
                        verticesN.add(vertex.normal.x);
                        verticesN.add(vertex.normal.y);
                        verticesN.add(vertex.normal.z);
                    }
                    if (vertex.texture == null) {
                        verticesT.add((float) Math.random());
                        verticesT.add((float) Math.random());
                    } else {
                        verticesT.add(vertex.texture.u);
                        verticesT.add(vertex.texture.v);
                    }
                }

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
                    for (FaceVertex vertex : face.vertices) {
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

            float[] rgb = {75, 75, 75};
            Mesh mesh = world.createMesh(arrayG, arrayN, arrayIndices, rgb);
            ItemObject object = world.createItemObject("test", 0f, 0f, -20f, 0.03f, mesh);
//            object.rotateY(30f);
//            object.rotateX(30f);
//            object.getMesh().getMaterial().setTexture(new Texture("/ressources/diamond.png"));
            test.add(object);

            world.launch();
            world.waitTermination();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}