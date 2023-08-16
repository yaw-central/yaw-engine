package yaw.engine.shader;

import yaw.engine.light.LightModel;
import yaw.engine.mesh.Material;

public class ShaderProgramADS extends ShaderProgram {
    private final String glVersion;
    private final boolean glCoreProfile;

    public ShaderProgramADS(String glVersion, boolean glCoreProfile) {
        this.glVersion = glVersion;
        this.glCoreProfile = glCoreProfile;
    }

    public ShaderProgramADS() {
        this("330", true);
    }

    public static ShaderCode computeLight(ShaderCode code) {
        code.function("Compute diffuse and specular components of lights.",
                "vec4", "computeLight", new String[][]{{"vec3", "light_color"},
                        {"float", "light_intensity"},
                        {"vec3", "position"},
                        {"vec3", "to_light-dir"},
                        {"vec3", "normal"}});

        code.l("vec4 diffusecolor = vec4(0, 0, 0, 0)")
                .l("vec4 speccolor = vec4(0, 0, 0, 0)");

        code.l().cmt("Diffuse color")
                .l("float diffuseFactor = max(dot(normal, to_light_dir), 0.0)")
                .l("diffusecolor = vec4(light_color, 1.0) * light_intensity * diffuseFactor * material.diffuse");

        code.l().cmt("Specular color")
                .l("vec3 camera_direction = normalize(camera_pos - position)")
                .l("vec3 from_light_dir = -to_light_dir")
                .l("vec3 reflected_light = normalize(reflect(from_light_dir , normal))")
                .l("float specularFactor = max( dot(camera_direction, reflected_light), 0.0)")
                .l("specularFactor = pow(specularFactor, material.shineness)")
                .l("speccolor = light_intensity  * specularFactor * material.specular * vec4(light_color, 1.0)");

        code.l().l("return (diffusecolor + speccolor)");

        return code.endFunction();
    }

    public static ShaderCode computeShadow(ShaderCode code) {
        code.function("Computation of shadowmap (only for directional lights, for now ...)",
                "float", "computeShadow", new String[][] {{"vec4", "lightSpace"}, {"vec3", "to_light_dir"}, {"vec3", "normal"}});

        code.l().l("vec3 projCoords = lightSpace.xyz / lightSpace.w")
                .l("projCoords = projCoords * 0.5 + 0.5")
                .l("float currentDepth = projCoords.z");

        code.l().l("if(currentDepth > 1.0) currentDepth = 0.0");

        code.l().l("float cosTheta = clamp(dot(normal, to_light_dir), 0, 1)")
                .l("float rbias = shadowBias*tan(acos(cosTheta))")
                .l("rbias = clamp(rbias, 0,0.01)");

        code.l().l("float shadow = 0.0")
                .l("vec2 texelSize = 1.0 / textureSize(shadowMapSampler, 0")
                .beginFor("int x = -1", "x <= 1", "++x")
                   .beginFor("int y = -1", "y <= 1", "++y")
                      .l("float pcfDepth = texture(shadowMapSampler, projCoords.xy + vec2(x, y) * texelSize).r")
                      .l("shadow += currentDepth-rbias > pcfDepth ? 1.0 : 0.0")
                   .endFor()
                .endFor()
                .l("shadow /= 9.0");

        code.l().l("return shadow");

        return code.endFunction();
    }

    public static ShaderCode computeDirectionalLight(ShaderCode code, boolean withShadows) {
        code.function("vec4", "computeDirectionalLight",
                new String[][]{{"DirectionalLight", "light"},
                        {"vec3", "position"},
                        {"vec3", "normal"}});

        if (withShadows) {
            code.l("float shadow = computeShadow(vDirectionalShadowSpace, normalize(-light.direction), normal)")
                    .l("return (1.0 - shadow) * computeLight(light.color, light.intensity, position, normalize(-light.direction), normal)");
        } else {
            code.l("return computeLight(light.color, light.intensity, position, normalize(-light.direction), normal)");
        }
        return code.endFunction();
    }

    public static ShaderCode computePointLight(ShaderCode code) {
        code.function("vec4", "computePointLight", new String[][]{{"PointLight", "light"}
                , {"vec3", "position"}
                , {"vec3", "normal"}});

        code.l().l("vec3 light_direction = light.position - position")
                .l("vec3 to_light_dir  = normalize(light_direction)")
                .l("vec4 light_color = computeLight(light.color, light.intensity, position, to_light_dir, normal)");

        code.l().cmt("Attenuation")
                .l("float distance = length(light_direction)")
                .l("float attenuationInv = light.att_constant + light.att_linear * distance + light.att_quadratic * (distance * distance)");

        code.l().l("return light_color / attenuationInv");

        return code.endFunction();
    }

    public static ShaderCode computeSpotLight(ShaderCode code) {
        code.function("vec4", "computeSpotLight", new String[][]{{"SpotLight", "light"}
                , {"vec3", "position"}
                , {"vec3", "normal"}});

        code.l().l("vec3 light_direction = light.pl.position - position")
                .l("vec3 to_light_dir  = normalize(light_direction)")
                .l("vec3 from_light_dir  = -to_light_dir")
                .l("float spot_alfa = dot(from_light_dir, normalize(light.conedir))")
                .l("vec4 color = vec4(0, 0, 0, 0)");

        code.beginIf("spot_alfa > light.cutoff")
                .l("color = calcPointLight(light.pl, position, normal)")
                .l("color *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoff))")
                .endIf();

        code.l("return color");

        return code.endFunction();
    }



    public ShaderCode vertexShader(boolean withShadows) {
        ShaderCode code = new ShaderCode(glVersion, glCoreProfile)
                .l()
                .cmt("Input buffer components")
                .l("layout(location = 0) in vec3 position")
                .l("layout(location = 1) in vec2 texCoord")
                .l("layout(location = 2) in vec3 normal")
                .l()
                .cmt("Output values")
                .l("out vec3 vPos")
                .l("out vec2 outTexCoord")
                .l("out vec3 vNorm");

        if (withShadows) {
            code.l().cmt("Shadow properties")
                    .l("out vec4 vDirectionalShadowSpace");
        }

        code.l().cmt("Camera-level uniforms")
                .l("uniform mat4 worldMatrix")
                .l()
                .cmt("Model-level uniforms")
                .l("uniform mat4 modelMatrix")
                .l("uniform mat3 normalMatrix");

        if (withShadows) {
            code.l().cmt("Shadow uniforms")
                    .l("uniform mat4 directionalShadowMatrix");
        }

        code.l().beginMain()
                .cmt("World vertex position")
                .l("vec4 mvPos = modelMatrix * vec4(position, 1.0)")
                .cmt("Projected position")
                .l("gl_Position = worldMatrix * mvPos")
                .cmt("Output copy of position")
                .l("vPos = mvPos.xyz;")
                .cmt("Computation of normal vector")
                .l("vNorm = normalize(mat3(normalMatrix) * normal)")
                .cmt("Texture coordinates")
                .l("outTexCoord = texCoord");

        if (withShadows) {
            code.l().cmt("Shadow output")
                    .l("vDirectionalShadowSpace = directionalShadowMatrix * mvPos");
        }

        code.endMain();

        return code;
    }

    public ShaderCode fragmentShader(boolean hasDirectionalLight, int maxPointLights, int maxSpotLights, boolean hasTexture, boolean withShadows) {
        ShaderCode code = new ShaderCode(glVersion, glCoreProfile)
                .cmt("Fragment shader for A(mbient) D(iffuse) S(pecular) rendering")
                .l();

        code.cmt("Max lights constants (forward rendering)");
        if (maxPointLights == 0) {
            code.cmt("No point light");
        } else {
            code.l("const int MAX_POINT_LIGHTS = " + maxPointLights);
        }

        if (maxSpotLights == 0) {
            code.cmt("No spot light");
        } else {
            code.l("const int MAX_SPOT_LIGHTS = " + maxPointLights);
        }

        code.l().cmt("Input values")
                .l("in vec2 inTexCoord")
                .l("in vec3 inNorm")
                .l("in vec3 inPos");

        if (withShadows) {
            code.l("in vec4 vDirectionalShadowSpace");
        }

        code.l()
                .cmt("Output values")
                .l("out fragColor");

        code.l().cmt("Structures").l();

        code.beginStruct("DirectionalLight")
                .item("vec3", "color")
                .item("float", "intensity")
                .item("vec3", "direction")
                .endStruct().l();

        code.beginStruct("PointLight")
                .item("vec3", "color")
                .item("float", "intensity")
                .item("vec3", "position", "Position assumed in view coordinates")
                .cmt("Attenuations")
                .item("float", "att_constant")
                .item("float", "att_linear")
                .item("float", "att_quadratic")
                .endStruct().l();

        code.beginStruct("SpotLight")
                .item("PointLight", "pl")
                .item("vec3", "conedir")
                .item("float", "cutoff")
                .endStruct().l();

        code.beginStruct("Material");

        if (!hasTexture) {
            code.item("vec3", "color", "Non-textured material");
        }

        code.item("vec3", "ambient", "the ambient color (multiplied by ambient light)")
                .item("vec3", "emissive", "Emissive color (added to overally color)")
                .item("float", "emissiveAmount", "The percentage (0 .. 1.0) of emissive color")
                .item("vec3", "diffuse", "the diffuse color (if no diffuse map")
                .item("vec3", "specular", "the specular color (if no specular map)")
                .item("float", "shineness", "for reflectance computation")
                .endStruct().l();

        code.cmt("Fragment shader uniforms")
                .l("uniform vec3 camera_pos")
                .l("uniform sampler2D texture_sampler")
                .l("uniform Material material")
                .l().cmt("Lights")
                .l("uniform vec3 ambientLight");

        if (hasDirectionalLight) {
            code.l("uniform DirectionalLight directionalLight");
        }

        if (maxPointLights > 0) {
            code.l("uniform PointLight pointLights[MAX_POINT_LIGHTS]");
        }

        if (maxSpotLights > 0) {
            code.l("uniform SpotLight spotLights[MAX_SPOT_LIGHTS]");
        }

        if (withShadows) {
            code.l().cmt("Shadow map uniforms")
                    .l("uniform sampler2D shadowMapSampler")
                    .l("uniform float shadowBias");
        }

        code.l().cmt("Auxiliary functions").l();

        code = computeLight(code);

        if (withShadows) {
            code.l();
            code = computeShadow(code);
        }

        if (hasDirectionalLight) {
            code.l();
            code = computeDirectionalLight(code, withShadows);
        }

        if (maxPointLights > 0) {
            code.l();
            code = computePointLight(code);
        }

        if (maxSpotLights > 0) {
            code.l();
            code = computeSpotLight(code);
        }

        code.l().beginMain()
                .l("vec3 normal = vNorm").l();

        if (hasTexture) {
            code.l("vec4 basecolor = texture(texture_sampler, text_coord)");
        } else {
            code.l("vec4 basecolor = vec4(material.color, 1)");
        }

        code.l().l("vec4 totalLight = vec4(ambientLight * material.ambient, 1.0)");

        if (hasDirectionalLight) {
            code.l().l("totalLight += computeDirectionalLight(directionalLight, vPos, normal)");
        }

        if (maxPointLights > 0) {
            code.beginFor("int i = 0", "i < MAX_POINT_LIGHTS", "i++")
                    .beginIf("pointLights[i].intensity > 0")
                    .l("totalLight += computePointLight(pointLights[i], vPos, normal)")
                    .endIf();
            code.endFor();
        }

        if (maxSpotLights > 0) {
            code.beginFor("int i = 0", "i < MAX_SPOT_LIGHTS", "i++")
                    .beginIf("spotLights[i].intensity > 0")
                    .l("totalLight += computeSpotLight(spotLights[i], vPos, normal)")
                    .endIf();
            code.endFor();
        }

        code.l().l("vec4 finalColor = basecolor * totalLight")
                .l("finalColor += material.emissiveAmount * material.emissive")
                .l("fragColor = vec4((finalColor).xyz,1)");

        return code.endMain();
    }

    /**
     * Create uniform for each attribute of the material
     *
     * @param uniformName uniform name
     */
    public void createMaterialUniform(String uniformName) {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    /**
     * Modifies the value of a uniform material with the specified material
     *
     * @param uniformName the uniform name
     * @param material    the material
     */
    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".color", material.getColor());
        setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    public void init() {
        /* Initialization of the shader program. */
        // System.out.println(vertexShader(true).toString());
        createVertexShader(vertexShader(true));
        createFragmentShader(fragShader.SHADER_STRING);

        /* Binds the code and checks that everything has been done correctly. */
        link();

        createUniform("worldMatrix");
        createUniform("modelMatrix");
        createUniform("normalMatrix");

        /* Initialization of the shadow map matrix uniform. */
        createUniform("directionalShadowMatrix");

        /* Create uniform for material. */
        createMaterialUniform("material");
        createUniform("texture_sampler");
        /* Initialization of the light's uniform. */
        createUniform("camera_pos");
        createUniform("specularPower");
        createUniform("ambientLight");
        createPointLightListUniform("pointLights", LightModel.MAX_POINTLIGHT);
        createSpotLightUniformList("spotLights", LightModel.MAX_SPOTLIGHT);
        createDirectionalLightUniform("directionalLight");
        createUniform("shadowMapSampler");
        createUniform("bias");
    }
}


