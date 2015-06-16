package at.antSim.objectsKI;

import at.antSim.MainApplication;
import at.antSim.graphics.entities.GraphicsEntity;
import at.antSim.graphics.models.TexturedModel;
import at.antSim.graphics.terrains.Terrain;
import at.antSim.objectsPhysic.DynamicPhysicsObject;
import at.antSim.objectsPhysic.GhostPhysicsObject;
import at.antSim.objectsPhysic.PhysicsManager;
import at.antSim.objectsPhysic.StaticPhysicsObject;
import at.antSim.objectsPhysic.TerrainPhysicsObject;
import at.antSim.objectsPhysic.basics.PhysicsObject;
import at.antSim.objectsPhysic.basics.PositionablePhysicsObject;
import at.antSim.objectsPhysic.basics.ReadOnlyPhysicsObject;

import java.util.*;

import javax.vecmath.Vector3f;

/**
 * Created on 18.05.2015.
 *
 * @author Flo, Clemens
 */
public abstract class Entity {

	static final Map<PhysicsObject, ObjectType> physicsObjectTypeMap = new HashMap<PhysicsObject, ObjectType>();
	static final Map<TexturedModel, List<Entity>> renderingMap = new HashMap<TexturedModel, List<Entity>>();
	static final Map<PhysicsObject, Entity> parentingEntities = new HashMap<PhysicsObject, Entity>(); //allows us to get eg. the Food Entity in a react() method when an ant hit the Food Entitie's physicsObject
	static final List<Entity> entities = new LinkedList<>(); //used to delete all entities
	static final List<Entity> dynamicEntities = new LinkedList<>();
	static final List<Ant> ants = new LinkedList<Ant>();

	final GraphicsEntity graphicsEntity;
	final PhysicsObject physicsObject;
	final ObjectType objectType;

	public Entity(GraphicsEntity graphicsEntity, PhysicsObject physicsObject, ObjectType type) {
		this.graphicsEntity = graphicsEntity;
		this.physicsObject = physicsObject;
		this.objectType = type;
		
		entities.add(this);
		parentingEntities.put(physicsObject, this);
		
		//add Entity to physics and rendering hashmaps
		physicsObjectTypeMap.put(physicsObject, type);
		if (graphicsEntity != null) {
			addRenderingEntity();
		}
	}

	public abstract void react(StaticPhysicsObject staticPhysicsObject);

	public abstract void react(DynamicPhysicsObject dynamicPhysicsObject);

	public abstract void react(GhostPhysicsObject ghostPhysicsObject);
	
	public abstract void react(TerrainPhysicsObject terrainPhysicsObject);


	/**
	 * Adds an Entity to the renderingMap.
	 */
	private void addRenderingEntity() {
		TexturedModel entityModel = graphicsEntity.getModel();
		List<Entity> batch = renderingMap.get(entityModel);
		if (batch != null) {
			batch.add(this);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(this);
			renderingMap.put(entityModel, newBatch);
		}
	}

	/**
	 * Deletes this Entity, removing it from the physicsObjectTypeMap and the renderingMap.
	 */
	public void delete() {
		deleteSpecific();
		PhysicsManager.getInstance().unregisterPhysicsObject(physicsObject);
		entities.remove(this);
		parentingEntities.remove(physicsObject);
		physicsObjectTypeMap.remove(this);
		if (graphicsEntity != null) { //null for Pheromones
			renderingMap.get(graphicsEntity.getModel()).remove(this);
		}
	}
	
	/**Allows to execute deletion routines specific to a subclass of Entity.
	 * 
	 */
	protected  abstract void deleteSpecific();

	/**
	 * @return - an unmodifiable version of renderingMap for preventing changes on renderingMap while allowing it to be retrieved for rendering
	 */
	public static Map<TexturedModel, List<Entity>> getUnmodifiableRenderingMap() {
		return Collections.unmodifiableMap(renderingMap);
	}
	
	public static Entity getParentEntity(PhysicsObject physicsObject) {
		return parentingEntities.get(physicsObject);
	}

	public GraphicsEntity getGraphicsEntity() {
		return graphicsEntity;
	}

	public PhysicsObject getPhysicsObject() {
		return physicsObject;
	}
	
	public ObjectType getObjectType() {
		return objectType;
	}
	
	/**Deletes all Entities from physicsObjects and GraphicsEntities maps when game session is quit (return to main menu).
	 * 
	 */
	public static void deleteAllEntities() {
		for (Entity entity : entities) {
			entity.delete();
		}
		physicsObjectTypeMap.clear();
		renderingMap.clear();
		parentingEntities.clear();
		entities.clear();
		dynamicEntities.clear();
		ants.clear();
	}
	
	/**Strangely, sometimes dynamic objects seem to fall below the world. 
	 * This method resets them a little above terrain height, preserving their original forces.
	 * 
	 */
	public static void resetUndergroundEntities() {
		for (Entity entity : dynamicEntities) {
			DynamicPhysicsObject phyObj = (DynamicPhysicsObject) entity.getPhysicsObject();
			float terrainHeight = MainApplication.getInstance().getTerrain().getHeightOfTerrain(phyObj.getPosition().x, phyObj.getPosition().z);
			float modelHeight = entity.getGraphicsEntity().getModel().getRawModel().getyLength() / 2 * entity.getGraphicsEntity().getScale();
			if ((phyObj.getPosition().y - modelHeight) < terrainHeight) {
				float desiredHeight = terrainHeight + modelHeight  + 1f;
				Vector3f linVelocity = new javax.vecmath.Vector3f();
				Vector3f angVelocity = new javax.vecmath.Vector3f();
				phyObj.getCollisionBody().getLinearVelocity(linVelocity);
				phyObj.getCollisionBody().getAngularVelocity(angVelocity);
				PhysicsManager.getInstance().unregisterPhysicsObject(phyObj);
				phyObj.getCollisionBody().clearForces();
				phyObj.setPosition(new Vector3f(phyObj.getPosition().x, desiredHeight, phyObj.getPosition().z));
				PhysicsManager.getInstance().registerPhysicsObject(phyObj);
				phyObj.getCollisionBody().setLinearVelocity(linVelocity);
				phyObj.getCollisionBody().setAngularVelocity(angVelocity);
			}
		}
	}
}
