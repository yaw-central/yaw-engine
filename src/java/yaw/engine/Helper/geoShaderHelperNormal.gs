#version 330 core

layout (points) in;
layout (line_strip, max_vertices = 2) out;

in vec3 vNormal[];


void main() {

    gl_Position = gl_in[0].gl_Position;
    EmitVertex();

    gl_Position = gl_in[0].gl_Position + vec4(vNormal[0], 0.0) * 0.1;
    EmitVertex();

    EndPrimitive();
}

