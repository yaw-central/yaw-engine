package yaw;

import loader.v2.Face;
import loader.v2.FaceVertex;
import loader.v2.OBJLoaderV2;
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
        String filename = "src/java/ressources/objfiles/tree.obj";

        try {
            OBJLoaderV2 objLoaderV2 = new OBJLoaderV2();
            OBJParser obj = new OBJParser(objLoaderV2, filename);
            World world = new World(0, 0, 800, 600);

            world.getCamera().setPosition(0, 1, 0);
            world.getCamera().rotate(0, 10, 0);

            Mesh tmpcube = MeshBuilder.generateBlock(1, 1, 1);
            ItemObject cube = world.createItemObject("cube", -5f, 0f, -5f, 1.0f, tmpcube);

            cube.getMesh().getMaterial().setTexture(new Texture("/ressources/diamond.png"));

            // -----------------------------

            ArrayList<ArrayList<Face>> facesByTextureList = objLoaderV2.createFaceListsByMaterial();

            int cptparts = 0;

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

//                System.out.println("arrayG" + Arrays.toString(arrayG));

            }
            float[] rgb = {75, 75, 75};
            Mesh mesh = world.createMesh(arrayG, arrayN, arrayIndices, rgb);
//                ItemObject current = world.createItemObject("test" + cptparts++, 0f, 0f, -15f, 0.03f, mesh);
            ItemObject prism = world.createItemObject("test" + cptparts++, 0f, 0f, -15f, 0.5f, mesh);
            prism.rotateY(30f);
            prism.rotateX(30f);
            prism.getMesh().getMaterial().setTexture(new Texture("/ressources/diamond.png"));


//            Mesh prismMesh;
//            ItemObject prism = world.createItemObject("prism", 0f, 0f, -2f, 1.0f, prismMesh);
            //prism.getMesh().getMaterial().setTexture(new Texture("/ressources/diamond.png"));
//            prism.translate(2f, 0f, -5f);

            LoadingRotatingTest loadRotatingTest = new LoadingRotatingTest(prism);

            world.registerUpdateCallback(loadRotatingTest);
//            System.out.println("liste de  items" + test);
            world.launch();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
