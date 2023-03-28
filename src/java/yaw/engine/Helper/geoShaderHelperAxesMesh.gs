#version 330 core

layout (lines) in;
layout (line_strip, max_vertices = 4) out;

uniform vec3 center;
out vec3 fColor;

void main()
{

    fColor = vec3(1.0,0.0,0.0);
    gl_Position = vec4(center,0.0);
    EmitVertex();
    gl_Position = (gl_in[0].gl_Position + vec4(1.0, 0.0, 0.0, 0.0));
    EmitVertex();
    EndPrimitive();

    fColor = vec3(0.0,1.0,0.0);
    gl_Position = vec4(center,0.0);
    EmitVertex();
    gl_Position = (gl_in[0].gl_Position + vec4(0.0, 1.0, 0.0, 0.0));
    EmitVertex();
    EndPrimitive();

    fColor = vec3(0.0,0.0,1.0);
    gl_Position = vec4(center,0.0);
    EmitVertex();
    gl_Position = gl_in[0].gl_Position + vec4(0.0, 0.0, 1.0, 0.0) * 2 ;
    EmitVertex();
    EndPrimitive();
}