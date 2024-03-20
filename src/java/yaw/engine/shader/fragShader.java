
// This file is generated from `fragShader.fs` shader program
// Please do not edit directly
package yaw.engine.shader;

public class fragShader {
    public final static String SHADER_STRING = "#version 330\n\n" +
            "const int MAX_POINT_LIGHTS = 5;\n" +
            "const int MAX_SPOT_LIGHTS = 5;\n\n\n" +
            "in vec2 outTexCoord;\n\n" +
            "in vec3 vNorm;\n" +
            "in vec3 vPos;\n" +
            "in vec4 vDirectionalShadowSpace;\n\n" +
            "out vec4 fragColor;\n\n" +
            "struct PointLight\n{\n" +
            "    vec3 color;\n" +
            "    // Light position is assumed to be in view coordinates\n" +
            "    vec3 position;\n" +
            "    float intensity;\n\n" +
            "    //Attenuation\n" +
            "    float att_constant;\n" +
            "    float att_linear;\n" +
            "    float att_exponent;\n" +
            "};\n\n" +
            "struct SpotLight\n{\n" +
            "    PointLight pl;\n" +
            "    vec3 conedir;\n" +
            "    float cutoff;\n" +
            "};\n\n" +
            "struct DirectionalLight\n{\n" +
            "    vec3 color;\n" +
            "    vec3 direction;\n" +
            "    float intensity;\n" +
            "};\n\n" +
            "struct Material\n{\n" +
            "    vec3 color;\n" +
            "    int hasTexture;\n" +
            "    float reflectance;\n" +
            "};\n\n" +
            "uniform sampler2D texture_sampler;\n" +
            "uniform vec3 ambientLight;\n" +
            "uniform float specularPower;\n" +
            "uniform Material material;\n" +
            "uniform PointLight pointLights[MAX_POINT_LIGHTS];\n" +
            "uniform SpotLight spotLights[MAX_SPOT_LIGHTS];\n" +
            "uniform DirectionalLight directionalLight;\n" +
            "uniform sampler2D shadowMapSampler;\n" +
            "uniform float bias;\n" +
            "uniform vec3 camera_pos;\n\n" +
            "float calcShadow(vec4 lightSpace, vec3 to_light_dir, vec3 normal)\n{\n" +
            "    vec3 projCoords = lightSpace.xyz / lightSpace.w;\n" +
            "    projCoords = projCoords * 0.5 + 0.5;\n" +
            "    float currentDepth = projCoords.z;\n\n" +
            "    if(currentDepth > 1.0) currentDepth = 0.0;\n\n" +
            "    float cosTheta = clamp(dot(normal, to_light_dir), 0, 1);\n" +
            "    float rbias = bias*tan(acos(cosTheta));\n" +
            "    rbias = clamp(rbias, 0,0.01);\n\n" +
            "    float shadow = 0.0;\n" +
            "    vec2 texelSize = 1.0 / textureSize(shadowMapSampler, 0);\n" +
            "    for(int x = -1; x <= 1; ++x) {\n" +
            "        for(int y = -1; y <= 1; ++y) {\n" +
            "            float pcfDepth = texture(shadowMapSampler, projCoords.xy + vec2(x, y) * texelSize).r;\n" +
            "            shadow += currentDepth-rbias > pcfDepth ? 1.0 : 0.0;\n" +
            "        }\n" +
            "    }\n" +
            "    shadow /= 9.0;\n\n" +
            "    return shadow;\n}\n\n" +
            "vec4 calcLightcolor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)\n{\n" +
            "    vec4 diffusecolor = vec4(0, 0, 0, 0);\n    vec4 speccolor = vec4(0, 0, 0, 0);\n\n" +
            "    // Diffuse Light\n" +
            "    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);\n" +
            "    diffusecolor = vec4(light_color, 1.0) * light_intensity * diffuseFactor;\n\n" +
            "    // Specular Light\n" +
            "    vec3 camera_direction = normalize(camera_pos - position);\n" +
            "    vec3 from_light_dir = -to_light_dir;\n" +
            "    vec3 reflected_light = normalize(reflect(from_light_dir , normal));\n" +
            "    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);\n" +
            "    specularFactor = pow(specularFactor, specularPower);\n" +
            "    speccolor = light_intensity  * specularFactor * material.reflectance * vec4(light_color, 1.0);\n\n" +
            "    return (diffusecolor + speccolor);\n}\n\n" +
            "vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)\n{\n" +
            "    vec3 light_direction = light.position - position;\n" +
            "    vec3 to_light_dir  = normalize(light_direction);\n" +
            "    vec4 light_color = calcLightcolor(light.color, light.intensity, position, to_light_dir, normal);\n\n" +
            "    // Apply Attenuation\n    float distance = length(light_direction);\n" +
            "    float attenuationInv = light.att_constant + light.att_linear * distance +\n" +
            "        light.att_exponent * distance * distance;\n" +
            "    if(attenuationInv==0){\n" +
            "        attenuationInv = 1;\n" +
            "    }\n" +
            "    return light_color / attenuationInv;\n}\n\n" +
            "vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal)\n{\n" +
            "    vec3 light_direction = light.pl.position - position;\n" +
            "    vec3 to_light_dir  = normalize(light_direction);\n" +
            "    vec3 from_light_dir  = -to_light_dir;\n" +
            "    float spot_alfa = dot(from_light_dir, normalize(light.conedir));\n\n" +
            "    vec4 color = vec4(0, 0, 0, 0);\n\n" +
            "    if ( spot_alfa > light.cutoff )\n" +
            "    {\n" +
            "        color = calcPointLight(light.pl, position, normal);\n" +
            "        color *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoff));\n" +
            "    }\n" +
            "    return color;\n" +
            "}\n\n" +
            "vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)\n{\n" +
            "    float shadow = calcShadow(vDirectionalShadowSpace, normalize(-light.direction), normal);\n" +
            "    return (1.0 - shadow) * calcLightcolor(light.color, light.intensity, position, normalize(-light.direction), normal);\n" +
            "}\n\n" +
            "vec4 calcBasecolor(Material pMaterial, vec2 text_coord)\n{\n" +
            "    vec4 basecolor;\n" +
            "    if ( pMaterial.hasTexture == 1 )\n" +
            "    {\n" +
            "        basecolor = texture(texture_sampler, text_coord);\n" +
            "    }\n" +
            "    else\n" +
            "    {\n" +
            "        basecolor = vec4(pMaterial.color, 1);\n" +
            "    }\n" +
            "    return basecolor;\n" +
            "}\n\n" +
            "void main()\n" +
            "{\n\n" +
            "    vec3 normal = vNorm;\n" +
            "    //vec3 normal = normalize(cross(dFdx(vPos), dFdy(vPos)));\n\n" +
            "    vec4 basecolor = calcBasecolor(material, outTexCoord);\n\n" +
            "    vec4 totalLight = vec4(ambientLight, 1.0);\n" +
            "    totalLight += calcDirectionalLight(directionalLight, vPos, normal);\n\n" +
            "    for (int i=0; i<MAX_POINT_LIGHTS; i++)\n" +
            "    {\n" +
            "        if ( pointLights[i].intensity > 0 )\n" +
            "        {\n" +
            "            totalLight += calcPointLight(pointLights[i], vPos, normal);\n" +
            "        }\n" +
            "    }\n\n\n\n" +
            "    for (int i=0; i<MAX_SPOT_LIGHTS; i++)\n" +
            "    {\n" +
            "        if ( spotLights[i].pl.intensity > 0 )\n" +
            "        {\n" +
            "            totalLight += calcSpotLight(spotLights[i], vPos, normal);\n" +
            "        }\n" +
            "    }\n" +
            "    fragColor = vec4((basecolor * totalLight).xyz,1);\n\n" +
            "}\n";
}
