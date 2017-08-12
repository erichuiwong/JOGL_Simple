#version 430

//Takes color value from vertical shader
in vec4 varyingColor;

//Output color
out vec4 color;
void main(void)
{
	color = varyingColor;
}