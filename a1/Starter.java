package a1;

import java.nio.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.opengl.util.*;
import graphicslib3D.GLSLUtils.*;
import graphicslib3D.*;

public class Starter extends JFrame implements GLEventListener, MouseWheelListener
{	private GLCanvas myCanvas;
	private int rendering_program;
	private int vao[] = new int[1];
	private GLSLUtils util = new GLSLUtils();

	private float x = 0.0f;
	private float inc = 0.01f; 
	private float incY = 0.01f; 
	private float y = 0.0f;
	private float zoom = 1.0f;
	
	private JButton upDownButton = new JButton("Up/Down or Left/Right");
	private JButton circleButton = new JButton("Circle");
	private JButton toggleColor = new JButton("Toggle Color");
	private JButton exitButton = new JButton("Exit");

	private Boolean direction = true;
	private Boolean circleFlag = true;
	private float colorFlag = 1.0f;

	private double angle = 45;

	public Starter()
	{	setTitle("Assignment 1");
		setSize(800, 600);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		myCanvas.addMouseWheelListener(this);
		JPanel buttons = new JPanel(new GridLayout(0, 4));	//Panel for buttons
		buttons.add(upDownButton);
		buttons.add(circleButton);
		buttons.add(toggleColor);
		buttons.add(exitButton);
		setLayout(new BorderLayout());
		add(myCanvas, BorderLayout.CENTER);
		add(buttons, BorderLayout.SOUTH);
		
		//Adding ActionListeners to the Buttons
		upDownButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				direction = !direction;
			}
		});
		circleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				circleFlag = !circleFlag;
			}
		});
		toggleColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (colorFlag == 1.0f) colorFlag = 0.0f;
				else colorFlag = 1.0f;
			}
		});
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		setVisible(true);
		FPSAnimator animator = new FPSAnimator(myCanvas, 30);
		animator.start();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if (notches < 0) {
			//Mouse wheel moved up or ENLARGE 
			if (zoom > 5.0f) {
				zoom = 5.0f;
			} else {
				zoom += 1.0f;
			}
		} else {
			//Mouse wheel moved down or SHRINK	
			if (zoom <= 1.0f) {
				zoom = 1.0f;
			} else {
				zoom-= 1.0f;
			}
		}
	}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glUseProgram(rendering_program);
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
		if (circleFlag) {
			//Determines moving linearly or circular
			if(direction) {
				//Determines if moving left/right or up/down
				x += inc;
				if (x > 1.0f) inc = -0.01f;
				if (x < -1.0f) inc = 0.01f;
			} else {
				y += incY;
				if (y > 1.0f) incY = -0.01f;
				if (y < -1.0f) incY = 0.01f;
			}
		} else {
			//Uses sin/cos to create circular movement
			angle += Math.toRadians(10);
			x += (float)Math.cos(angle)*0.05f;
			y += (float)Math.sin(angle)*0.05f;
		}
		//Transferring data to the vertical shader
		int offset_loc = gl.glGetUniformLocation(rendering_program, "inc");
		gl.glProgramUniform1f(rendering_program, offset_loc, x);
		int offset_locY = gl.glGetUniformLocation(rendering_program, "incY");
		gl.glProgramUniform1f(rendering_program, offset_locY, y);
		int color_flag = gl.glGetUniformLocation(rendering_program, "colorFlag");
		gl.glProgramUniform1f(rendering_program, color_flag, colorFlag);
		int offset_zoom = gl.glGetUniformLocation(rendering_program, "zoom");
		gl.glProgramUniform1f(rendering_program, offset_zoom, zoom);

		gl.glDrawArrays(GL_TRIANGLES,0,3);
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		//Print out versions of Java, OpenGL, and JOGL
		System.out.println("Java -version " + System.getProperty("java.version"));
		System.out.println("OpenGL -version " + gl.glGetString(GL_VERSION));
		System.out.println("JOGL -version " + Package.getPackage("com.jogamp.opengl").getImplementationVersion());
	}

	private int createShaderProgram()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] vertCompiled = new int[1];
		int[] fragCompiled = new int[1];
		int[] linked = new int[1];

		String vshaderSource[] = util.readShaderSource("a1/vert.shader");
		String fshaderSource[] = util.readShaderSource("a1/frag.shader");
		int lengths[];

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);	
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glCompileShader(vShader);
		
		//check for shader errors
		checkOpenGLError();
		gl.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
		if (vertCompiled[0] == 1) {
			System.out.println("Vertex Compilation Success");
		} else {
			System.out.println("Vertext Compilation Failed");
			printShaderLog(vShader);
		}	
		
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(fShader);

		//check for fragment errors
		checkOpenGLError();
		gl.glGetShaderiv(fShader, GL_COMPILE_STATUS, fragCompiled, 0);
		if (fragCompiled[0] == 1) {
			System.out.println("Fragment Compilation Success");
		} else {
			System.out.println("Fragment Compilation Failed");
			printShaderLog(fShader);
		}
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		
		gl.glLinkProgram(vfprogram);
		//check for linking errors
		checkOpenGLError();
		gl.glGetProgramiv(vfprogram, GL_LINK_STATUS, linked, 0);
		if (linked[0] == 1) {
			System.out.println("Linking Succeeded");
		} else {
			System.out.println("Linking Failed");
			printProgramLog(vfprogram);
		}
		
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		return vfprogram;
	}

	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}
	
	private void printShaderLog(int shader)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;

		// determine the length of the shader compilation log
		gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{	log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
			System.out.println("Shader Info Log: ");
			for (int i = 0; i < log.length; i++)
			{	System.out.print((char) log[i]);
			}
		}
	}

	void printProgramLog(int prog)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;

		// determine length of the program compilation log
		gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{	log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
			System.out.println("Program Info Log: ");
			for (int i = 0; i < log.length; i++)
			{	System.out.print((char) log[i]);
			}
		}
	}

	boolean checkOpenGLError()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while (glErr != GL_NO_ERROR)
		{	System.err.println("glError: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}
}