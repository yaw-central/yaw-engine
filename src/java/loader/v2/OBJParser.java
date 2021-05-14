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
                processUseMaterial(line);
            } else if (line.startsWith("mtllib")) {
                processMaterialLib(line);
            } else {
                System.out.println("line " + cpt + " unknown line |" + line + "|");
            }
            cpt++;
        }
        bufferedReader.close();

        System.out.println("Loaded " + cpt + " lines");
    }

    private void processVertex(String line) {
        // parse float list
        System.out.println(line);
    }

    private void processVertexTexture(String line) {
        // parse float list
        System.out.println(line);
    }

    private void processVertexNormal(String line) {
        // parse float list
        System.out.println(line);
    }

    private void processFace(String line) {
        line = line.substring("f".length()).trim();
        // parsz vertice list
        System.out.println(line);
    }

    private void processGroupName(String line) {
        // parse white space list
        System.out.println(line);
    }

    private void processObjectName(String line) {
        objloader.addObjectName(line.substring("o".length()).trim());
    }

    private void processMaterialLib(String line){
        // parse white space list
        System.out.println(line);
    }

    private void processUseMaterial(String line) {
        objloader.setCurrentMaterial(line.substring("usemtl".length()).trim());
    }

    private void processMapLib(String line) {
        // parse white space list
        System.out.println(line);
    }

    private void processUseMap(String line) {
        objloader.setCurrentMap(line.substring("usemap".length()).trim());
    }

    //TODO parse Material File


}
