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
            } else if (line.startsWith("maplib")) { // maplib
                processMapLib(line);
            } else if (line.startsWith("usemap")) { //usemap
                processUseMap(line);
            } else if (line.startsWith("usemtl")) { // use mtl
                processUseMaterial(line);
            } else if (line.startsWith("mtllib")) { //mtllib
                // Not working yet
//                processMaterialLib(line);
            } else if (line.startsWith("s")) { // smoothing
                processSmoothingGroup(line);
            } else if (line.startsWith("p")) { // point
                processPoint(line);
            } else if (line.startsWith("l")) { // line
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
        float[] res = StringParser.parseStringToFloatArray(3, line, "v".length()); // parse with an utility class
        objloader.addGeometricVertex(res[0], res[1], res[2]); // add the parsing result to the objloader
    }

    private void processVertexTexture(String line) {
        // Parse float list
        float[] res = StringParser.parseStringToFloatArray(2, line, "vt".length()); // parse with an utility class
        objloader.addTextureVertex(res[0], res[1]); // add the parsing result to the objloader
    }

    private void processVertexNormal(String line) {
        // Parse float list
        float[] res = StringParser.parseStringToFloatArray(3, line, "vn".length()); // parse with an utility class after removing the keyword
        objloader.addNormalVertex(res[0], res[1], res[2]); // add the parsing result to the objloader
    }

    private void processFace(String line) {
        line = line.substring("f".length()).trim();
        int[] res = StringParser.parseMultipleVertices(line, 3); // parse with an utility class after removing the keyword
        objloader.addFace(res); // add the parsing result to the objloader
    }

    private void processGroupName(String line) {
        String[] res = StringParser.parseWhiteSpaces(line.substring("g".length()).trim()); // parse with an utility class after removing the keyword
        objloader.setCurrentGroupNames(res); // add the parsing result to the objloader
    }

    private void processObjectName(String line) {
        objloader.setObjectName(line.substring("o".length()).trim()); // add the object name to the objloader after removing the keyword
    }

    private void processMaterialLib(String line) {
        // Parse white space list
        line = line.substring("g".length()).trim(); // removing the keyword
        String[] tmp = line.split(" ");  // split on spaces
        if (tmp != null) {
            for (int i = 0; i < tmp.length; i++) {
                tmp[i] = tmp[i].trim(); // trim all elements of the string array
            }
            for (int i = 0; i < tmp.length; i++) {
                try {
                    parseMtlFile(tmp[i]); // parse the string array
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
        line = line.substring("g".length()).trim(); // removing the keyword
        String[] tmp = line.split(" "); // split on spaces
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = tmp[i].trim(); // trim all elements of the string array
        }
        objloader.addMapLib(tmp); // add the parsing result to the objloader
    }

    private void processUseMap(String line) {
        objloader.setCurrentMap(line.substring("usemap".length()).trim()); // add the parsing result to the objloader after removing the keyword
    }

    private void processSmoothingGroup(String line) {
        line = line.substring("s".length()).trim(); // removing the keyword
        int groupNumber = 0; // default value equals 0
        if (!line.equalsIgnoreCase("off")) { // ignore if argument off in line
            groupNumber = Integer.parseInt(line); // update groupnumber with the number in the line
        }
        objloader.setCurrentSmoothingGroup(groupNumber); // add the parsing result to the objloader
    }

    private void processPoint(String line) {
        line = line.substring("p".length()).trim(); // removing the keyword
        // Parse Vertice NTuples
        int[] res = StringParser.parseMultipleVertices(line, 1);
        objloader.addPoints(res); // add the parsing result to the objloader
    }

    private void processLine(String line) {
        line = line.substring("l".length()).trim(); // removing the keyword
        // Parse Vertice NTuples
        int[] values = StringParser.parseMultipleVertices(line, 2); // parse with an utility class after removing the keyword
        objloader.addLine(values); // add the parsing result to the objloader
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
        line = line.substring("newmtl".length()).trim(); // removing the keyword
        objloader.newMaterial(line); // add the parsing result to the objloader
    }

    private void processReflectivity(String fieldName, String line) {
        int type = 0;
        if (fieldName.equals("Kd")) { // get reflectivity's type in function of the keyword
            type = 1;
        } else if (fieldName.equals("Ks")) {
            type = 2;
        } else if (fieldName.equals("Tf")) {
            type = 3;
        }

        String[] tokens = StringParser.parseWhiteSpaces(line.substring(fieldName.length())); // parse with an utility class
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
            objloader.setXYZ(type, x, y, z); // set the coords from the line's infos
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
            objloader.setRGB(type, r, g, b); // set the rgb from the line's infos
        }
    }

    private void processIllum(String line) {
        line = line.substring("illum".length()).trim(); // parse the keyword
        int illumModel = Integer.parseInt(line);
        if ((illumModel < 0) || (illumModel > 10)) {
            return;
        }
        objloader.setIllum(illumModel); // add the result to the objloader
    }

    private void processD(String line) {
        line = line.substring("d".length()).trim(); // parse the keyword
        boolean halo = false;
        if (line.startsWith("-halo")) { // if argument halo in line then halo is true and parse the float value in the line
            halo = true;
            line = line.substring("-halo".length()).trim();
        }
        objloader.setD(halo, Float.parseFloat(line)); // add the result to the objloader
    }

    private void processNs(String line) {
        line = line.substring("Ns".length()).trim();// parse the keyword
        objloader.setNs(Float.parseFloat(line));// add the result to the objloader
    }

    private void processSharpness(String line) {
        line = line.substring("sharpness".length()).trim();// parse the keyword
        objloader.setSharpness(Float.parseFloat(line));// add the result to the objloader
    }

    private void processNi(String line) {
        line = line.substring("Ni".length()).trim();// parse the keyword
        objloader.setNi(Float.parseFloat(line));// add the result to the objloader
    }

    private void processMapDecalDispBump(String fieldname, String line) {
        int type = 0;
        switch (fieldname) { //get type in function of the keyword
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

        objloader.setMapDecalDispBump(type, line.substring(fieldname.length()).trim()); // add the type to the objloader
    }

    private void processRefl(String line) {
        String filename = null;

        int type = -1;
        line = line.substring("refl".length()).trim(); //parse the line
        if (line.startsWith("-type")) {
            line = line.substring("-type".length()).trim();
            if (line.startsWith("sphere")) { // if line start with sphere then update the type and the filename
                type = 0;
                filename = line.substring("sphere".length()).trim();
            } else if (line.startsWith("cube_top")) {// if line start with cube_top then update the type and the filename
                type = 0;
                filename = line.substring("cube_top".length()).trim();
            } else if (line.startsWith("cube_bottom")) {// if line start with cube_bottom then update the type and the filename
                type = 2;
                filename = line.substring("cube_bottom".length()).trim();
            } else if (line.startsWith("cube_front")) {// if line start with cube_front then update the type and the filename
                type = 3;
                filename = line.substring("cube_front".length()).trim();
            } else if (line.startsWith("cube_back")) {// if line start with cube_back then update the type and the filename
                type = 4;
                filename = line.substring("cube_back".length()).trim();
            } else if (line.startsWith("cube_left")) {// if line start with cube_left then update the type and the filename
                type = 5;
                filename = line.substring("cube_left".length()).trim();
            } else if (line.startsWith("cube_right")) {// if line start with cube_right then update the type and the filename
                type = 6;
                filename = line.substring("cube_right".length()).trim();
            } else { // else : unknown material
                System.out.println("unknown material refl -type, line = " + line);
                return;
            }
        } else {
            filename = line;
        }

        objloader.setRefl(type, filename); // add the type to the objloader
    }

}
