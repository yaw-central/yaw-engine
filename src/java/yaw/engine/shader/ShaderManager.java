package yaw.engine.shader;

import yaw.engine.helper.*;
import yaw.engine.light.LightModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages all the shaders (shader programs)
 * used to render a frame in the gameloop.
 *
 * This applies to the following "times" :
 *
 *  - initialization time : before entering the gameloop or when
 *  a "reinitialization" must be performed, e.g. if the (maximum) number of lights change,
 *  which require a reconstruction of the shader programs. This concerns e.g. the compilation
 *  of the shader programs, the creation of uniforms, etc.
 *  The corresponding methods are prefixed with  `init...`
 *
 *  - (frame) setup time `setup` : before the rendering, when e.g. all the global (shared) uniforms must be set.
 *  The method prefix is `setup...`
 *
 *  - rendering time :  mostly setting the mesh-specific uniforms and issuing the draw commands.
 *  The method prefix is `render...`, and it is the responsability of the renderer.
 */
public class ShaderManager {

    private Map<String, ShaderProgram> shaderMap;

    private ArrayList<ShaderProgram> shaderlist;

    public ShaderManager() {
        shaderMap = new HashMap<>();
        shaderlist = new ArrayList<>();
    }

    public void register(String key, ShaderProgram shaderProgram) {
        if (shaderMap.containsKey(key)) {
            throw new Error("ShaderProgram '" + key + "' already registered");
        }
        shaderMap.put(key, shaderProgram);
    }

    public void unregister(String key) {
        if(!shaderMap.containsKey(key)) {
            throw new Error("No such shader program: '" + key + "'");
        }
        shaderMap.remove(key);
    }

    public ShaderProgram fetch(String key) {
        ShaderProgram prog = shaderMap.get(key);
        if (prog == null) {
            throw new Error("No such shader program: '" + key + "'");
        }
        return prog;
    }

    public void addShader(ShaderProgram mShaderProgram){
        shaderlist.add(mShaderProgram);
    }
    
    public ShaderProgram getShaderProgram(int id){
        return shaderlist.get(id);
    }

    public ShaderProgramADS getShaderProgramAds() { return (ShaderProgramADS) shaderlist.get(0);}
    public HelperVerticesShaders getShaderProgramHelperSummit() { return (HelperVerticesShaders) shaderlist.get(1);}
    public HelperNormalsShaders getShaderProgramHelperNormals() { return (HelperNormalsShaders) shaderlist.get(2);}
    public HelperAxesShaders getShaderProgramHelperAxesMesh() { return (HelperAxesShaders) shaderlist.get(3);}

    /**
     * The Shader Program is deallocated
     */
    public void cleanUp(){
        for(ShaderProgram sp : shaderlist){
            sp.cleanup();
        }
    }
}
