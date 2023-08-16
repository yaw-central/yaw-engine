package yaw.engine.shader;

import yaw.engine.helper.*;

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

    private Map<String, ShaderProgram> namedShadersMap;

    private Map<ShaderProperties, ShaderProgram> meshShadersMap;

    public ShaderManager() {
        namedShadersMap = new HashMap<>();
        meshShadersMap = new HashMap<>();
    }

    public void register(String key, ShaderProgram shaderProgram) {
        if (namedShadersMap.containsKey(key)) {
            throw new Error("ShaderProgram '" + key + "' already registered");
        }
        namedShadersMap.put(key, shaderProgram);
    }

    public ShaderProgram fetch(String key) {
        ShaderProgram prog = namedShadersMap.get(key);
        if (prog == null) {
            throw new Error("No such shader program: '" + key + "'");
        }
        return prog;
    }

    public void register(ShaderProperties props, ShaderProgram shaderProgram) {
        if (namedShadersMap.containsKey(props)) {
            throw new Error("ShaderProgram (mesh-specific) already registered");
        }
        meshShadersMap.put(props, shaderProgram);
    }

    public ShaderProgram fetch(ShaderProperties props) {
        return meshShadersMap.get(props);
    }


    /**
     * The Shader Program is deallocated
     */
    public void cleanUp(){
        for(ShaderProgram sp : namedShadersMap.values()) {
            sp.cleanup();
        }

        for(ShaderProgram sp : meshShadersMap.values()) {
            sp.cleanup();
        }
    }
}
