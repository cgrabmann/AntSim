package engineTester;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

/**MainLoop holds the main game loop containing the main game logic and handles the initialization and destruction of the game.
 * <br>
 * The main game loop updates all objects and does the rendering for every frame.
 * 
 * @author Flo
 *
 */
public class MainLoop {
	
	/* The OpenGL code so far has been written based on the a youtube tutorial series. 
	 * 
	 * Also, there is a plugin for writing GLSL shaders in Eclipse: http://sourceforge.net/projects/webglsl/
	 * -> Extract .zip file to Eclipse installation directory
	 * 
	 * In case of questions, refer to the following videos:
	 * 
	 * 1.) Setting up Display: http://www.youtube.com/watch?v=VS8wlS9hF8E
	 * 2.) VAOs and VBOs: http://www.youtube.com/watch?v=WMiggUPst-Q
	 * 3.) Using index buffers: http://www.youtube.com/watch?v=z2yFlvkBbmk
	 * 4.) Introduction to Shaders: http://www.youtube.com/watch?v=AyNZG_mqGVE
	 * 5.) Coloring using Shaders: http://www.youtube.com/watch?v=4w7lNF8dnYw (shaders.vertexShader, shaders.fragmentShader)
	 * 6.) Texturing: http://www.youtube.com/watch?v=SPt-aogu72A
	 * 7.) Matrices & Uniform Variables: http://www.youtube.com/watch?v=oc8Yl4ZruCA -> Uniform Variables are used to send variables from Java code to the shaders
	 * 8.) Model, View & Projection Matrices: http://www.youtube.com/watch?v=50Y9u7K0PZo
	 * */
	
	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);
		
		float[] vertices = {			
				-0.5f,0.5f,-0.5f,	
				-0.5f,-0.5f,-0.5f,	
				0.5f,-0.5f,-0.5f,	
				0.5f,0.5f,-0.5f,		
				
				-0.5f,0.5f,0.5f,	
				-0.5f,-0.5f,0.5f,	
				0.5f,-0.5f,0.5f,	
				0.5f,0.5f,0.5f,
				
				0.5f,0.5f,-0.5f,	
				0.5f,-0.5f,-0.5f,	
				0.5f,-0.5f,0.5f,	
				0.5f,0.5f,0.5f,
				
				-0.5f,0.5f,-0.5f,	
				-0.5f,-0.5f,-0.5f,	
				-0.5f,-0.5f,0.5f,	
				-0.5f,0.5f,0.5f,
				
				-0.5f,0.5f,0.5f,
				-0.5f,0.5f,-0.5f,
				0.5f,0.5f,-0.5f,
				0.5f,0.5f,0.5f,
				
				-0.5f,-0.5f,0.5f,
				-0.5f,-0.5f,-0.5f,
				0.5f,-0.5f,-0.5f,
				0.5f,-0.5f,0.5f
				
		};
		
		float[] textureCoords = {
				
				0,0,
				0,1,
				1,1,
				1,0,			
				0,0,
				0,1,
				1,1,
				1,0,			
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0

				
		};
		
		int[] indices = {
				0,1,3,	
				3,1,2,	
				4,5,7,
				7,5,6,
				8,9,11,
				11,9,10,
				12,13,15,
				15,13,14,	
				16,17,19,
				19,17,18,
				20,21,23,
				23,21,22

		};
		
		/* Using index buffers will help to use less data in total by not specifying positions shared by 
		 * different vertexes multiple times and instead using indices defining which vertexes use which positions.
		 */
		
		RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
		ModelTexture texture = new ModelTexture(loader.loadTexture("test_texture")); //load a texture from a png file
		TexturedModel staticModel = new TexturedModel(model, texture); //stick the texture on a RawModel
		Entity entity = new Entity(staticModel, new Vector3f(0,0,-5),0,0,0,1); 
		Camera camera = new Camera();
		
		//main game loop
		while(!Display.isCloseRequested()) {
			
			//game logic
			//entity.increaseRotation(1, 1, 0);
			camera.move(); //every single frame check for key inputs which move the camera
		
			renderer.prepare(); //basically clears screen from previous frame
			shader.start();
			shader.loadViewMatrix(camera); //all objects in the world need to be moved every single frame to simulate the camera
			renderer.render(entity, shader);
			shader.stop();
			DisplayManager.updateDisplay();
		}
		
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
