package yaw.engine.resources;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MtlLoader {
    public enum LoadMode {
        LOAD_UNDEFINED
        , LOAD_FROM_FILE
        , LOAD_FROM_RESOURCE
    }
    private LoadMode loadMode = LoadMode.LOAD_UNDEFINED;
    private String loadPath = null;

    private String currentMaterial = null;

    public MtlLoader() {}

    public Map<String, MtlMaterial> parseFromFile(String filename) throws IOException {
        loadMode = LoadMode.LOAD_FROM_FILE;
        loadPath = filename;
        return parseFromBufferedReader(new BufferedReader(new FileReader(filename)));
    }

    public Map<String, MtlMaterial> parseFromResource(String name) throws IOException {
        InputStream istream = ObjLoader.class.getResourceAsStream(name);
        if (istream == null) {
            throw new ObjLoader.ParseError("Cannot find resource: "+name);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
        loadMode = LoadMode.LOAD_FROM_RESOURCE;
        loadPath = name;
        return parseFromBufferedReader(reader);
    }

    public Map<String, MtlMaterial> parseFromBufferedReader(BufferedReader reader) throws IOException {
        String[] lines = Utils.readLines(reader);
        return parseFromLines(lines);
    }

    public Map<String, MtlMaterial> parseFromLines(String[] lines) {
        Map<String, MtlMaterial> materials = new HashMap<>();
        for(int linepos=1; linepos<=lines.length; linepos++) {
            parseLine(materials, linepos, lines[linepos-1]);
        }
        return materials;
    }

    public void parseLine(Map<String, MtlMaterial> materials, int linepos, String line) {
        if (line.isEmpty()) {
            return;
        }
        if (line.charAt(0) == '#') {
            return;
        }

        String[] parts = line.split("\\s+");
        if (parts.length == 0) {
            return;
        }

        if (parts[0].equals("newmtl")) {
            // new material
            if (parts.length < 2) {
                throw new ObjLoader.ParseError("Missing material name", linepos);
            }
            String matName = parts[1];
            if (materials.containsKey(matName)) {
                throw new ObjLoader.ParseError("Dupplicate material name: " + matName, linepos);
            }
            MtlMaterial material = new MtlMaterial(matName);
            materials.put(matName, material);
            currentMaterial = matName;
            return;
        } else if (parts[0].equals("Ns")) {
            float shininess = parseMaterialValue("shininess", parts, linepos);
            materials.get(currentMaterial).shininess = shininess;
            return;
        } else if (parts[0].equals("Ka")) {
            float ambient = parseMaterialValue("ambient", parts, linepos);
            materials.get(currentMaterial).ambient = ambient;
            return;
        } else if (parts[0].equals("Kd")) {
            float diffuse = parseMaterialValue("diffuse", parts, linepos);
            materials.get(currentMaterial).diffuse = diffuse;
            return;
        } else if (parts[0].equals("Ks")) {
            float specular = parseMaterialValue("specular", parts, linepos);
            materials.get(currentMaterial).specular = specular;
            return;
        } else if (parts[0].equals("Ke")) {
            float emissive = parseMaterialValue("emissive", parts, linepos);
            materials.get(currentMaterial).emissive = emissive;
            return;
        } else if (parts[0].equals("d")) {
            float opacity = parseMaterialValue("opacity", parts, linepos);
            materials.get(currentMaterial).opacity = opacity;
            return;
        } else {
            // unsupported entry : emit warning ?
            return;
        }
    }

    public float parseMaterialValue(String desc, String[] parts, int linepos) {
        if (parts.length < 2) {
            throw new ObjLoader.ParseError("Missing " + desc + " value", linepos);
        }
        if (currentMaterial == null) {
            throw new ObjLoader.ParseError("Material name is not specified", linepos);
        }
        float value = 0;
        try {
            value = Float.parseFloat(parts[1]);
        } catch (NumberFormatException e) {
            throw new ObjLoader.ParseError("Cannot parse "+ desc + " value", linepos, e);
        }
        return value;
    }
}
