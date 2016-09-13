#version 130

layout(location = 0) in vec3 in_position;

out vec3 pass_textureCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * vec4(in_position, 1.0);
	pass_textureCoords = in_position;
	pass_textureCoords.x = -pass_textureCoords.x;
	pass_textureCoords.y = -pass_textureCoords.y;
}