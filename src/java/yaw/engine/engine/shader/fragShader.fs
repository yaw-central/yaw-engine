#version 330

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;


in vec2 outTexCoord;

in vec4 summit;
in vec3 vNorm;
in vec3 vPos;
in vec4 vDirectionalShadowSpace;

out vec4 fragColor;

struct PointLight
{
    vec3 color;
    // Light position is assumed to be in view coordinates
    vec3 position;
    float intensity;

    //Attenuation
    float att_constant;
    float att_linear;
    float att_exponent;
};

struct SpotLight
{
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

struct DirectionalLight
{
    vec3 color;
    vec3 direction;
    float intensity;
};

struct Material
{
    vec3 color;
    int hasTexture;
    float reflectance;
};

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;
uniform sampler2D shadowMapSampler;
uniform float bias;
uniform vec3 camera_pos;
uniform bool helperSummit;

float calcShadow(vec4 lightSpace, vec3 to_light_dir, vec3 normal)
{
    vec3 projCoords = lightSpace.xyz / lightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;
    float currentDepth = projCoords.z;

    if(currentDepth > 1.0) currentDepth = 0.0;

    float cosTheta = clamp(dot(normal, to_light_dir), 0, 1);
    float rbias = bias*tan(acos(cosTheta));
    rbias = clamp(rbias, 0,0.01);

    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMapSampler, 0);
    for(int x = -1; x <= 1; ++x) {
        for(int y = -1; y <= 1; ++y) {
            float pcfDepth = texture(shadowMapSampler, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth-rbias > pcfDepth ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;

    return shadow;
}

vec4 calcLightcolor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffusecolor = vec4(0, 0, 0, 0);
    vec4 speccolor = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffusecolor = vec4(light_color, 1.0) * light_intensity * diffuseFactor;

    // Specular Light
    vec3 camera_direction = normalize(camera_pos - position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir , normal));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    speccolor = light_intensity  * specularFactor * material.reflectance * vec4(light_color, 1.0);

    return (diffusecolor + speccolor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_color = calcLightcolor(light.color, light.intensity, position, to_light_dir, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att_constant + light.att_linear * distance +
        light.att_exponent * distance * distance;
    if(attenuationInv==0){
        attenuationInv = 1;
    }
    return light_color / attenuationInv;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.pl.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec3 from_light_dir  = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.conedir));

    vec4 color = vec4(0, 0, 0, 0);

    if ( spot_alfa > light.cutoff )
    {
        color = calcPointLight(light.pl, position, normal);
        color *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoff));
    }
    return color;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    float shadow = calcShadow(vDirectionalShadowSpace, normalize(-light.direction), normal);
    return (1.0 - shadow) * calcLightcolor(light.color, light.intensity, position, normalize(-light.direction), normal);
}

vec4 calcBasecolor(Material pMaterial, vec2 text_coord)
{
    vec4 basecolor;
    if ( pMaterial.hasTexture == 1 )
    {
        basecolor = texture(texture_sampler, text_coord);
    }
    else
    {
        basecolor = vec4(pMaterial.color, 1);
    }
    return basecolor;
}

void main()
{

    vec3 normal = vNorm;
    //vec3 normal = normalize(cross(dFdx(vPos), dFdy(vPos)));

    vec4 basecolor = calcBasecolor(material, outTexCoord);

    vec4 totalLight = vec4(ambientLight, 1.0);
    totalLight += calcDirectionalLight(directionalLight, vPos, normal);

    for (int i=0; i<MAX_POINT_LIGHTS; i++)
    {
        if ( pointLights[i].intensity > 0 )
        {
            totalLight += calcPointLight(pointLights[i], vPos, normal);
        }
    }



    for (int i=0; i<MAX_SPOT_LIGHTS; i++)
    {
        if ( spotLights[i].pl.intensity > 0 )
        {
            totalLight += calcSpotLight(spotLights[i], vPos, normal);
        }
    }

    if (helperSummit)
        fragColor = vec4(1.0, 0.0, 0.0, 1.0);
    else
        fragColor = vec4((basecolor * totalLight).xyz,1);

}
