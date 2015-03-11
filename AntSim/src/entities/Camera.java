package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

/**A Camera is used for moving around the world.
 * 
 * @author Flo
 *
 */
public class Camera {

	private Vector3f position = new Vector3f(0,0,0);
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
	public void move() {
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			position.z -= 0.02f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			position.z += 0.02f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x += 0.02f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x -= 0.02f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.y += 0.02f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.y -= 0.02f;
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
