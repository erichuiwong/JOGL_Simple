#version 430

//Variables trasnferred from main class
uniform float inc;
uniform float incY;
uniform float colorFlag;
uniform float zoom;

//Sends color for fragment shader to display
out vec4 varyingColor;

//inc and incY moves the shape
void main(void)
{ 	if (zoom == 1.0f) {
		//Determines which size to use
		if (gl_VertexID == 0) gl_Position = vec4( 0.25+inc,-0.25+incY, 0.0, 1.0);
	  	else if (gl_VertexID == 1) gl_Position = vec4(-0.25+inc,-0.25+incY, 0.0, 1.0);
	  	else gl_Position = vec4(0+inc, 0.25+incY, 0.0, 1.0);
	} else if (zoom == 2.0f) { 
	  	if (gl_VertexID == 0) gl_Position = vec4( 0.30+inc,-0.30+incY, 0.0, 1.0);
	  	else if (gl_VertexID == 1) gl_Position = vec4(-0.30+inc,-0.30+incY, 0.0, 1.0);
	  	else gl_Position = vec4(0+inc, 0.30+incY, 0.0, 1.0);
	} else if (zoom == 3.0f) { 
		if (gl_VertexID == 0) gl_Position = vec4( 0.35+inc,-0.35+incY, 0.0, 1.0);
	  	else if (gl_VertexID == 1) gl_Position = vec4(-0.35+inc,-0.35+incY, 0.0, 1.0);
	  	else gl_Position = vec4(0+inc, 0.35+incY, 0.0, 1.0);
	} else if (zoom == 4.0f) {
		if (gl_VertexID == 0) gl_Position = vec4( 0.40+inc,-0.40+incY, 0.0, 1.0);
	  	else if (gl_VertexID == 1) gl_Position = vec4(-0.40+inc,-0.40+incY, 0.0, 1.0);
	  	else gl_Position = vec4(0+inc, 0.40+incY, 0.0, 1.0);
	} else {
		if (gl_VertexID == 0) gl_Position = vec4( 0.45+inc,-0.45+incY, 0.0, 1.0);
	  	else if (gl_VertexID == 1) gl_Position = vec4(-0.45+inc,-0.45+incY, 0.0, 1.0);
	  	else gl_Position = vec4(0+inc, 0.45+incY, 0.0, 1.0);
	}
	//Determines solid color or gradient
    if (colorFlag == 1.0f) varyingColor = (gl_Position * 0.5) + vec4(0.5, 0.5, 0.5, 0.5);
    else varyingColor = vec4(0, 1, 0, 1);
}