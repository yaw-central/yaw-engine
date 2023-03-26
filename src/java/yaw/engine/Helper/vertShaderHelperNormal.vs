#version 330 core

layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

out vec3 vNormal;


void main() {
    vec4 mvPos = modelMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * mvPos;
    vNormal = normal;
}