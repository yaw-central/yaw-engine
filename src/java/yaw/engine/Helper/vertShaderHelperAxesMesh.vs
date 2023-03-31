#version 330 core

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform vec3 center;



void main() {
        gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(center, 1.0);
}