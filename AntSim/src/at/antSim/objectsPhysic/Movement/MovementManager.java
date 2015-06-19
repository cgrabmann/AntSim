package at.antSim.objectsPhysic.Movement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import at.antSim.objectsKI.Entity;
import at.antSim.objectsPhysic.DynamicPhysicsObject;

/**Handles movement of Entities - either to specific locations or randomly.
 * 
 * @author Flo
 *
 */
public class MovementManager {

	private static MovementManager INSTANCE = null;
	
	HashMap<DynamicPhysicsObject, Stack<MovementMode>> entries = new HashMap<>();
	
	static {
		INSTANCE = new MovementManager();
	}
	
	protected MovementManager() {};
	
	/**Adds a {@link MovementMode} on top of a {@link DynamicPhysicsObject}'s movement stack.
	 * 
	 * @param physicsObject
	 * @param mode
	 */
	public void addMovementEntry(DynamicPhysicsObject physicsObject, MovementMode mode) {
		if (entries.containsKey(physicsObject)) {
			entries.get(physicsObject).add(mode);
		} else {
			entries.put(physicsObject, new Stack<MovementMode>());
			entries.get(physicsObject).add(mode);
		}
	}
	
	/**Removes the last {@link MovementMode} entry from a {@link DynamicPhysicsObject}'s movement stack.
	 * 
	 * @param physicsObject
	 */
	public void removeLastMovementEntry(DynamicPhysicsObject physicsObject) {
		if (entries.containsKey(physicsObject)) {
			entries.get(physicsObject).pop();
			if (entries.get(physicsObject).size() == 0) {
				entries.remove(physicsObject);
			}
		}
	}
	
	/**Removes a {@link DynamicPhysicsObject} from the Collection of handled {@link DynamicPhysicsObject}s, thereby removing all its {@link MovementMode} entries.
	 * 
	 * @param physicsObject
	 */
	public void removeAllMovementEntries(DynamicPhysicsObject physicsObject) {
		entries.remove(physicsObject);
	}
	
	/**Clears all {@link DynamicPhysicsObject}s from the Collection of handled {@link DynamicPhysicsObject}s.
	 * 
	 */
	public void clearAllEntries() {
		entries.clear();
	}
	
	public void moveAllEntries() {
		for (Stack<MovementMode> stack : entries.values()) {
			stack.lastElement().move();
		}
	}
	
	public static MovementManager getInstance() {
		return INSTANCE;
	}
}