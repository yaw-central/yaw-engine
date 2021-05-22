package loader.v2;

import java.io.*;

public class OBJParser {
    OBJLoaderV2Interface objloader;
    File objFile = null;

    public OBJParser(OBJLoaderV2Interface objloader, String filename) throws IOException {
        this.objloader = objloader;
        objloader.setObjFileName(filename);
        parseObjFile(filename);

    }


    private void parseObjFile(String objFilename) throws IOException {
        int cpt = 0;
        FileReader fileReader;
        BufferedReader bufferedReader;

        objFile = new File(objFilename);
        fileReader = new FileReader(objFile);
        bufferedReader = new BufferedReader(fileReader);

        String line;
        int currentInd = 0;

        while (true) {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }

            line = line.trim();

            if (line.length() == 0) {
                continue;
            }

            if (line.startsWith("#")) {
                continue;
            } else if (line.startsWith("vt")) {
                processVertexTexture(line);
            } else if (line.startsWith("vn")) {
                processVertexNormal(line);
            } else if (line.startsWith("v")) {
                processVertex(line);
            } else if (line.startsWith("f")) {
                processFace(line);
            } else if (line.startsWith("g")) {
                processGroupName(line);
            } else if (line.startsWith("o")) {
                processObjectName(line);
            } else if (line.startsWith("maplib")) {
                processMapLib(line);
            } else if (line.startsWith("usemap")) {
                processUseMap(line);
            } else if (line.startsWith("usemtl")) {
                //processUseMaterial(line);
            } else if (line.startsWith("mtllib")) {
                //processMaterialLib(line);
            } else if (line.startsWith("s")) {
                processSmoothingGroup(line);
            } else if (line.startsWith("p")) {
                processPoint(line);
            } else if (line.startsWith("l")) {
                processLine(line);
            } else {
                System.out.println("line " + cpt + " unknown line |" + line + "|");
            }
            cpt++;
        }
        bufferedReader.close();

        System.out.println("Loaded " + cpt + " lines");
    }

    private void processVertex(String line) {
        float[] res = StringParser.parseStringToFloatArray(line);
        objloader.addGeometricVertex(res[0], res[1], res[2]);
    }

    private void processVertexTexture(String line) {
        // Parse float list
        float[] res = StringParser.parseStringToFloatArray(line);
        objloader.addTextureVertex(res[0], res[1]);
    }

    private void processVertexNormal(String line) {
        // Parse float list
        float[] res = StringParser.parseStringToFloatArray(line);
        objloader.addNormalVertex(res[0], res[1], res[2]);
    }

    private void processFace(String line) {
        line = line.substring("f".length()).trim();
        int[] res = StringParser.parseMultipleVertices(line, 3);
        objloader.addFace(res);
    }

    private void processGroupName(String line) {
        String[] res = StringParser.parseWhiteSpaces(line.substring("g".length()).trim());
        objloader.setCurrentGroupNames(res);
    }

    private void processObjectName(String line) {
        objloader.addObjectName(line.substring("o".length()).trim());
    }

    private void processMaterialLib(String line) {
        // Parse white space list
//        System.out.println(line);
        line = line.substring("g".length()).trim();
        String[] tmp = line.split(" ");
        if (tmp != null) {
            for (int i = 0; i < tmp.length; i++) {
                tmp[i] = tmp[i].trim();
            }
            for (int i = 0; i < tmp.length; i++) {
                try {
                    parseMtlFile(tmp[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processUseMaterial(String line) {
        objloader.setCurrentMaterial(line.substring("usemtl".length()).trim());
    }

    private void processMapLib(String line) {
        // Parse white space list
//        System.out.println(line);
        line = line.substring("g".length()).trim();
        String[] tmp = line.split(" ");
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = tmp[i].trim();
        }
        objloader.addMapLib(tmp);
    }

    private void processUseMap(String line) {
        objloader.setCurrentMap(line.substring("usemap".length()).trim());
    }

    private void processSmoothingGroup(String line) {
        line = line.substring("s".length()).trim();
        int groupNumber = 0;
        if (!line.equalsIgnoreCase("off")) {
            groupNumber = Integer.parseInt(line);
        }
        objloader.setCurrentSmoothingGroup(groupNumber);
    }

    private void processPoint(String line) {
        line = line.substring("p".length()).trim();
        // Parse Vertice NTuples
        int[] res = StringParser.parseMultipleVertices(line, 1);
        objloader.addPoints(res);
    }

    private void processLine(String line) {
        line = line.substring("l".length()).trim();
        // Parse Vertice NTuples
        int[] values = StringParser.parseMultipleVertices(line, 2);
        objloader.addLine(values);
    }


    // TODO: parse Material File
    /**
     * Parses the given MTL file
     * @param mtlFilename
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void parseMtlFile(String mtlFilename) throws FileNotFoundException, IOException {
        int cpt = 0;
        FileReader fileReader;
        BufferedReader bufferedReader;

        File mtlFile = new File(objFile.getParent(), mtlFilename);
        fileReader = new FileReader(mtlFile);
        bufferedReader = new BufferedReader(fileReader);

        String line;

        while (true) {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();

            if (line.length() == 0) {
                continue;
            }

            if (line.startsWith("#")) // comment
            {
                continue;
            } else if (line.startsWith("newmtl")) {
                processNewmtl(line);
            } else if (line.startsWith("Ka")) {
                processReflectivityTransmissivity("Ka", line);
            } else if (line.startsWith("Kd")) {
                processReflectivityTransmissivity("Kd", line);
            } else if (line.startsWith("Ks")) {
                processReflectivityTransmissivity("Ks", line);
            } else if (line.startsWith("Tf")) {
                processReflectivityTransmissivity("Tf", line);
            } else if (line.startsWith("illum")) {
                processIllum(line);
            } else if (line.startsWith("d")) {
                processD(line);
            } else if (line.startsWith("Ns")) {
                processNs(line);
            } else if (line.startsWith("sharpness")) {
                processSharpness(line);
            } else if (line.startsWith("Ni")) {
                processNi(line);
            } else if (line.startsWith("map_Ka")) {
                processMapDecalDispBump("map_Ka", line);
            } else if (line.startsWith("map_Kd")) {
                processMapDecalDispBump("map_Kd", line);
            } else if (line.startsWith("map_Ks")) {
                processMapDecalDispBump("map_Ks", line);
            } else if (line.startsWith("map_Ns")) {
                processMapDecalDispBump("map_Ns", line);
            } else if (line.startsWith("map_d")) {
                processMapDecalDispBump("map_d", line);
            } else if (line.startsWith("disp")) {
                processMapDecalDispBump("disp", line);
            } else if (line.startsWith("decal")) {
                processMapDecalDispBump("decal", line);
            } else if (line.startsWith("bump")) {
                processMapDecalDispBump("bump", line);
            } else if (line.startsWith("refl")) {
                processRefl(line);
            } else {
                System.out.println("line " + cpt + " unknown line |" + line + "|");

            }
            cpt++;
        }
        bufferedReader.close();

        System.out.println("Loaded " + cpt + " lines");
    }

    private void processNewmtl(String line) {
        line = line.substring("newmtl".length()).trim();
        objloader.newMaterial(line);
    }

    private void processReflectivityTransmissivity(String fieldName, String line) {
        //TODO
    }

    private void processIllum(String line) {
        //TODO
    }

    private void processD(String line) {
        //TODO
    }

    private void processNs(String line) {
        //TODO
    }

    private void processSharpness(String line) {
        //TODO
    }

    private void processNi(String line) {
        //TODO
    }

    private void processMapDecalDispBump(String fieldname, String line) {
        //TODO
    }

    private void processRefl(String line) {
        //TODO
    }

}
