#version 330 core

layout (lines) in;
layout (line_strip, max_vertices = 4) out;

void main()
{
    gl_Position = gl_in[0].gl_Position;
    EmitVertex();
    gl_Position = (gl_in[0].gl_Position + vec4(1.0, 0.0, 0.0, 0.0));
    EmitVertex();
    EndPrimitive();

    gl_Position = gl_in[0].gl_Position;
    EmitVertex();
    gl_Position = gl_in[0].gl_Position + vec4(0.0, 1.0, 0.0, 0.0);
    EmitVertex();
    EndPrimitive();

    gl_Position = gl_in[0].gl_Position;
    EmitVertex();
    gl_Position = gl_in[0].gl_Position + vec4(0.0, 0.0, 1.0, 0.0) ;
    EmitVertex();
    EndPrimitive();
}