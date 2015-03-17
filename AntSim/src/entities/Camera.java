package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**A Camera is used for moving around the world.
 * 
 * @author Flo
 *
 */
public class Camera {

	private Vector3f position = new Vector3f(0,10,-400);
	private float pitch; //how high or low the camera is aiming
	private float yaw; //how much left or right the camera is aiming
	private float roll; //the camera's tilt - at 180� it is upside down
	
	public Camera(){};
	
	/**Moves the camera by the following key bindings:<br>
	 * <ul>
	 * 		<li>W - zoom in</li>
	 * 		<li>D - move right</li>
	 * 		<li>A - move left</li>
	 * </ul>
	 */
	/*public void move() {
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) { //zoom out
			position.z -= 0.5f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) { //zoom in
			position.z += 0.5f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) { //move right
			position.x += 0.5f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) { //move left
			position.x -= 0.5f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) { //move up
			position.y += 0.5f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) { //move down
			position.y -= 0.5f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_A)) { //rotate left
			position.x -= 1f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_D)) { //rotate right
			position.x -= 1f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_S)) { //rotate down
			pitch = (pitch + 2)%360;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_W)) { //rotate up
			pitch = (pitch - 2)%360;
		}
	}*/
	
	public void move()
	{
	                
	            float arg_yaw = Mouse.getDX() ;
	            //System.out.println(arg_yaw) ;
	            yaw += arg_yaw/10 ;
	            float arg_roll = Mouse.getDY() ;
	            pitch += -(arg_roll/10) ;
	            Mouse.setGrabbed(true);
	            
	                if (Keyboard.isKeyDown(Keyboard.KEY_W)) 
	                {
	                    float toZ = ((float)Math.sin( Math.toRadians(yaw+90))) ;
	                    float toX = ((float)Math.cos( Math.toRadians(yaw+90))) ;
	                    position.x -= toX;
	                    position.z -= toZ;
	                        
	                }
	                if (Keyboard.isKeyDown(Keyboard.KEY_S)) 
	                {
	                    float toZ = ((float)Math.sin( Math.toRadians(yaw+90))) ;
	                    float toX = ((float)Math.cos( Math.toRadians(yaw+90))) ;
	                    position.x += toX;
	                    position.z += toZ;
	                }

	                if (Keyboard.isKeyDown(Keyboard.KEY_D)) 
	                {
	                    float toZ = ((float)Math.sin( Math.toRadians(yaw))) ;
	                    float toX = ((float)Math.cos( Math.toRadians(yaw))) ;
	                    position.x += toX;
	                    position.z += toZ;
	                }

	                if (Keyboard.isKeyDown(Keyboard.KEY_A)) 
	                {
	                    float toZ = ((float)Math.sin( Math.toRadians(yaw))) ;
	                    float toX = ((float)Math.cos( Math.toRadians(yaw))) ;
	                    position.x -= toX;
	                    position.z -= toZ;
	                }
	                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) 
	                {
	                    position.y += 0.2f;
	                }
	                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) 
	                {
	                    position.y -= 0.2f;
	                }
	}

	/**
	 * @return - the Camera's position as Vector3f
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @return - the Camera's pitch (how high or low the camera is aiming)
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * @return - the Camera's yaw (how much left or right the camera is aiming)
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * @return - the Camera's tilt (in degrees)
	 */
	public float getRoll() {
		return roll;
	}
}
