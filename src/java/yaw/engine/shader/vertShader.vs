#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 normal;

out vec3 vNorm;
out vec3 vPos;
out vec2 outTexCoord;
out vec4 vDirectionalShadowSpace;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 directionalShadowMatrix;

void main()
{
	vec4 mvPos = modelMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * mvPos;
    vNorm = normalize(transpose(inverse(mat3(modelMatrix))) * normal);
    vPos = mvPos.xyz;
    vDirectionalShadowSpace = directionalShadowMatrix * mvPos;
    outTexCoord=texCoord;
}