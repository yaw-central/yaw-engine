package loader.v2;

import java.io.*;

/**
 * The OBJParser can parse OBJ files
 */
public class OBJParser {

    // ========== Attributes ==========


    /**
     * Its OBJ loader
     */
    private OBJLoader objloader;
    /**
     * The OBJ File
     */
    private File objFile = null;


    // ========== Constructors =========

    /**
     * Basic constructor. Launches the parsing on the given file
     *
     * @param objloader The associated OBJ loader
     * @param filename  The OBJ file name
     * @throws IOException
     */
    public OBJParser(OBJLoader objloader, String filename) throws IOException {
        this.objloader = objloader;
        objloader.setObjFileName(filename);
        parseObjFile(filename);
    }


    // ========== Methods ==========


    /**
     * Parses the given file
     */
    private void parseObjFile(String objFilename) throws IOException {
        int cpt = 0;
        objFile = new File(objFilename);
        FileReader fileReader = new FileReader(objFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        int currentInd = 0;

        // Reading all file's lines
        while (true) {
            line = bufferedReader.readLine();
            if (line == null) break;            // End of the file

            // Removing first spaces
            line = line.trim();

            if (line.length() == 0) continue;   // Empty line

            // Matching line's keyword
            if (line.startsWith("#")) {
                continue;
            } else if (line.startsWith("vt")) { // Texture Vertex
                processVertexTexture(line);
            } else if (line.startsWith("vn")) { // Normal Vertex
                processVertexNormal(line);
            } else if (line.startsWith("v")) {  // Vertex
                processVertex(line);
            } else if (line.startsWith("f")) {  // Face
                processFace(line);
            } else if (line.startsWith("g")) {  // Group
                processGroupName(line);
            } else if (line.startsWith("o")) {  // Object
                processObjectName(line);
            } else if (line.startsWith("maplib")) {
                processMapLib(line);
            } else if (line.startsWith("usemap")) {
                processUseMap(line);
            } else if (line.startsWith("usemtl")) {
                processUseMaterial(line);
            } else if (line.startsWith("mtllib")) {
                // not working yet
//                processMaterialLib(line);
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
        float[] res = StringParser.parseStringToFloatArray(3, line, "v".length());
        objloader.addGeometricVertex(res[0], res[1], res[2]);
    }

    private void processVertexTexture(String line) {
        // Parse float list
        float[] res = StringParser.parseStringToFloatArray(2, line, "vt".length());
        objloader.addTextureVertex(res[0], res[1]);
    }

    private void processVertexNormal(String line) {
        // Parse float list
        float[] res = StringParser.parseStringToFloatArray(3, line, "vn".length());
        objloader.addNormalVertex(res[0], res[1], res[2]);
    }

    private void processFace(String line) {
        line = line.substring("f".length()).trim();
//        System.out.println(line);
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
     *
     * @param mtlFilename
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void parseMtlFile(String mtlFilename) throws FileNotFoundException, IOException {
        int cpt = 0;
        File mtlFile = new File(objFile.getParent(), "src/java/ressources/" + mtlFilename);
        FileReader fileReader = new FileReader(mtlFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;

        // Reading all file's lines
        while (true) {
            line = bufferedReader.readLine();
            if (line == null) break;

            // Removing first spaces
            line = line.trim();

            if (line.length() == 0) continue;

            // Matching line keyword
            if (line.startsWith("#")) {
                continue;   // comment line
            } else if (line.startsWith("newmtl")) {
                processNewMtl(line);
            } else if (line.startsWith("Ka")) {
                processReflectivity("Ka", line);
            } else if (line.startsWith("Kd")) {
                processReflectivity("Kd", line);
            } else if (line.startsWith("Ks")) {
                processReflectivity("Ks", line);
            } else if (line.startsWith("Tf")) {
                processReflectivity("Tf", line);
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

    private void processNewMtl(String line) {
        line = line.substring("newmtl".length()).trim();
        objloader.newMaterial(line);
    }

    private void processReflectivity(String fieldName, String line) {
        int type = 0;
        if (fieldName.equals("Kd")) {
            type = 1;
        } else if (fieldName.equals("Ks")) {
            type = 2;
        } else if (fieldName.equals("Tf")) {
            type = 3;
        }

        String[] tokens = StringParser.parseWhiteSpaces(line.substring(fieldName.length()));
        if (tokens == null || tokens.length <= 0 || tokens[0].equals("spectral")) {
            return;
        } else if (tokens[0].equals("xyz")) {
            // Ka xyz x_num y_num z_num

            if (tokens.length < 2) {
                return;
            }
            float x = Float.parseFloat(tokens[1]);
            float y = x;
            float z = x;
            if (tokens.length > 2) {
                y = Float.parseFloat(tokens[2]);
            }
            if (tokens.length > 3) {
                z = Float.parseFloat(tokens[3]);
            }
            objloader.setXYZ(type, x, y, z);
        } else {
            float r = Float.parseFloat(tokens[0]);
            float g = r;
            float b = r;
            if (tokens.length > 1) {
                g = Float.parseFloat(tokens[1]);
            }
            if (tokens.length > 2) {
                b = Float.parseFloat(tokens[2]);
            }
            objloader.setRGB(type, r, g, b);
        }
    }

    private void processIllum(String line) {
        line = line.substring("illum".length()).trim();
        int illumModel = Integer.parseInt(line);
        if ((illumModel < 0) || (illumModel > 10)) {
            return;
        }
        objloader.setIllum(illumModel);
    }

    private void processD(String line) {
        line = line.substring("d".length()).trim();
        boolean halo = false;
        if (line.startsWith("-halo")) {
            halo = true;
            line = line.substring("-halo".length()).trim();
        }
        objloader.setD(halo, Float.parseFloat(line));
    }

    private void processNs(String line) {
        line = line.substring("Ns".length()).trim();
        objloader.setNs(Float.parseFloat(line));
    }

    private void processSharpness(String line) {
        line = line.substring("sharpness".length()).trim();
        objloader.setSharpness(Float.parseFloat(line));
    }

    private void processNi(String line) {
        line = line.substring("Ni".length()).trim();
        objloader.setNi(Float.parseFloat(line));
    }

    private void processMapDecalDispBump(String fieldname, String line) {
        int type = 0;
        switch (fieldname) {
            case "map_Kd":
                type = 1;
                break;
            case "map_Ks":
                type = 2;
                break;
            case "map_Ns":
                type = 3;
                break;
            case "map_d":
                type = 4;
                break;
            case "decal":
                type = 5;
                break;
            case "disp":
                type = 6;
                break;
            case "bump":
                type = 7;
                break;
        }

        objloader.setMapDecalDispBump(type, line.substring(fieldname.length()).trim());
    }

    private void processRefl(String line) {
        String filename = null;

        int type = -1;
        line = line.substring("refl".length()).trim();
        if (line.startsWith("-type")) {
            line = line.substring("-type".length()).trim();
            if (line.startsWith("sphere")) {
                type = 0;
                filename = line.substring("sphere".length()).trim();
            } else if (line.startsWith("cube_top")) {
                type = 0;
                filename = line.substring("cube_top".length()).trim();
            } else if (line.startsWith("cube_bottom")) {
                type = 2;
                filename = line.substring("cube_bottom".length()).trim();
            } else if (line.startsWith("cube_front")) {
                type = 3;
                filename = line.substring("cube_front".length()).trim();
            } else if (line.startsWith("cube_back")) {
                type = 4;
                filename = line.substring("cube_back".length()).trim();
            } else if (line.startsWith("cube_left")) {
                type = 5;
                filename = line.substring("cube_left".length()).trim();
            } else if (line.startsWith("cube_right")) {
                type = 6;
                filename = line.substring("cube_right".length()).trim();
            } else {
                System.out.println("unknown material refl -type, line = " + line);
                return;
            }
        } else {
            filename = line;
        }

        objloader.setRefl(type, filename);
    }

}
