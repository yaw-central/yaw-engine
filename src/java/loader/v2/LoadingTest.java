package loader.v2;

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
        String filename = "src/java/ressources/tree2.obj";

        try {
            OBJLoaderV2 objLoaderV2 = new OBJLoaderV2();
            OBJParser obj = new OBJParser(objLoaderV2, filename);
            World world = new World(0, 0, 800, 600);

            world.getCamera().setPosition(0, 1, 0);
            world.getCamera().rotate(0, 10, 0);

            Mesh tmpcube = MeshBuilder.generateBlock(1, 1, 1);
            ItemObject cube = world.createItemObject("cube", -5f, 0f, -5f, 1.0f, tmpcube);

            cube.getMesh().getMaterial().setTexture(new Texture("/ressources/diamond.png"));

            //-----------------------------

            ArrayList<ArrayList<Face>> facesByTextureList = createFaceListsByMaterial(objLoaderV2);

            int cptparts = 0;
            List<ItemObject> test = new ArrayList<>();

            for (ArrayList<Face> faceList : facesByTextureList) {
                if (faceList.isEmpty()) {
                    continue;
                }

                ArrayList<Face> triangleList = splitQuads(faceList);
                calcMissingVertexNormals(triangleList);
                System.out.println("après process" + triangleList);

                if (triangleList.size() <= 0) {
                    continue;
                }

                //-----------------------------------------------

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
                Collections.reverse(verticesG);
                Collections.reverse(verticesN);
                Collections.reverse(verticesT);

                float[] arrayG = new float[verticesG.size()];
                for (int i = 0; i < verticesG.size(); i++) {
                    arrayG[i] = verticesG.get(i);
                }
                float[] arrayN = new float[verticesN.size()];
                for (int i = 0; i < verticesG.size(); i++) {
                    arrayN[i] = verticesN.get(i);
                }
                float[] arrayT = new float[verticesT.size()];
                for (int i = 0; i < verticesT.size(); i++) {
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
                Collections.reverse(indices);

                int[] arrayIndices = new int[indices.size()];
                for (int i = 0; i < indices.size(); i++) {
                    arrayIndices[i] = indices.get(i);
                }

                //-----------------------------------------------
                float[] rgb = {75, 75, 75};

                System.out.println("arrayG" + Arrays.toString(arrayG));

                Mesh mesh = world.createMesh(arrayG, arrayN, arrayIndices, rgb);
                test.add(world.createItemObject("test" + cptparts++, 0f, 0f, -15f, 0.250f, mesh));
            }
            System.out.println("liste de  items" + test);
            world.launch();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<ArrayList<Face>> createFaceListsByMaterial(OBJLoaderV2 objLoaderV2) {
        ArrayList<ArrayList<Face>> facesByTextureList = new ArrayList<>();
        Material currentMaterial = null;
        ArrayList<Face> currentFaceList = new ArrayList<>();

        for (Face face : objLoaderV2.faces) {
            if (face.material != currentMaterial) {
                if (!currentFaceList.isEmpty()) {
                    facesByTextureList.add(currentFaceList);
                }
                currentMaterial = face.material;
                currentFaceList = new ArrayList<>();
            }
            currentFaceList.add(face);
        }
        if (!currentFaceList.isEmpty()) {
            facesByTextureList.add(currentFaceList);
        }
        return facesByTextureList;
    }

    private static ArrayList<Face> splitQuads(ArrayList<Face> faceList) {
        ArrayList<Face> triangleList = new ArrayList<>();
        for (Face face : faceList) {
            System.out.println(face.vertices.size());
            if (face.vertices.size()/3 == 3) {
                triangleList.add(face);
            } else if (face.vertices.size()/4 == 4) {
                FaceVertex v1 = face.vertices.get(0);
                FaceVertex v2 = face.vertices.get(1);
                FaceVertex v3 = face.vertices.get(2);
                FaceVertex v4 = face.vertices.get(3);
                Face f1 = new Face();
                f1.map = face.map;
                f1.material = face.material;
                f1.addVertex(v1);
                f1.addVertex(v2);
                f1.addVertex(v3);
                triangleList.add(f1);
                Face f2 = new Face();
                f2.map = face.map;
                f2.material = face.material;
                f2.addVertex(v1);
                f2.addVertex(v3);
                f2.addVertex(v4);
                triangleList.add(f2);
            }
        }
        System.out.println("liste depuis splitQuads" + triangleList);
        return triangleList;
    }

    private static void calcMissingVertexNormals(ArrayList<Face> triangleList) {
        for (Face face : triangleList) {
            face.processTriangleNormal();
            for (int i = 0; i < face.vertices.size(); i++) {
                FaceVertex fv = face.vertices.get(i);
                if (face.vertices.get(0).normal == null) {
                    FaceVertex newFv = new FaceVertex();
                    newFv.geometric = fv.geometric;
                    newFv.texture = fv.texture;
                    newFv.normal = face.faceNormal;
                    face.vertices.set(i, newFv);
                }
            }
        }
        System.out.println("calcmissing" + triangleList);
    }


}
