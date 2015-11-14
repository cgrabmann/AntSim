package at.antSim.objectsKI;

import at.antSim.Globals;
import at.antSim.MainApplication;
import at.antSim.graphics.entities.GraphicsEntity;
import at.antSim.graphics.graphicsUtils.TransparentTriangle;
import at.antSim.graphics.models.TexturedModel;
import at.antSim.graphics.terrains.Terrain;
import at.antSim.objectsPhysic.DynamicPhysicsObject;
import at.antSim.objectsPhysic.GhostPhysicsObject;
import at.antSim.objectsPhysic.PhysicsManager;
import at.antSim.objectsPhysic.StaticPhysicsObject;
import at.antSim.objectsPhysic.TerrainPhysicsObject;
import at.antSim.objectsPhysic.Movement.BasicMovement;
import at.antSim.objectsPhysic.Movement.MovementManager;
import at.antSim.objectsPhysic.basics.PhysicsObject;
import at.antSim.objectsPhysic.basics.PositionablePhysicsObject;
import at.antSim.objectsPhysic.basics.ReadOnlyPhysicsObject;
import at.antSim.utils.Maths;
import at.antSim.utils.Pair;
import at.antSim.utils.TransparentTriangleComparator;
import at.antSim.utils.TransparentsSorterWorker;

import java.util.*;
import java.util.Map.Entry;

import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Matrix4f;

/**
 * Created on 18.05.2015.
 *
 * @author Flo, Clemens
 */
/**
 * @author Flo
 *
 */
public abstract class Entity {

	static final Map<PhysicsObject, ObjectType> physicsObjectTypeMap = new HashMap<PhysicsObject, ObjectType>();
	static final Map<TexturedModel, List<Entity>> renderingMap = new HashMap<TexturedModel, List<Entity>>();
	static final LinkedList<Entity> transparents = new LinkedList<>();
	static ArrayList<Pair<Entity, TransparentTriangle>> transparentTriangles = new ArrayList<Pair<Entity, TransparentTriangle>>();
	static final List<Entry<Entity, Integer>> entityVertices = new ArrayList<Entry<Entity, Integer>>();
	static final Map<PhysicsObject, Entity> parentingEntities = new HashMap<PhysicsObject, Entity>(); //allows us to get eg. the Food Entity in a react() method when an ant hit the Food Entitie's physicsObject
	static final List<Entity> entities = new LinkedList<>(); //used to delete all entities
	static final List<Entity> dynamicEntities = new LinkedList<>();
	static final List<Ant> ants = new LinkedList<Ant>();
	static final List<Entity> pheromones = new LinkedList<>();
	static final List<PositionLocator> deleteableLocators = new LinkedList<PositionLocator>();
	static Hive hive;
	
	static TransparentsSorterWorker triangleSorter = new TransparentsSorterWorker();
	
	static boolean deleteAllowed = true; //set to false when quitting a game so that events do not delete entities that are already being deleted

	GraphicsEntity graphicsEntity;
	PhysicsObject physicsObject;
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
			if (graphicsEntity.getModel().usesTransparency()) {
				addTransparent(this);
				
				// do not insert elements at correct position but sort whole list at the end -> should be faster for big amounts of data				
				for (TransparentTriangle triangle : graphicsEntity.getModel().getRawModel().getTransparentVertices())
				{
					//!!! IMPORTANT: MAKE COPIES AND NOT REFERENCES OF RAW MODEL PRESET'S TRIANGLES
					TransparentTriangle copiedTriangle = new TransparentTriangle(triangle);
					transparentTriangles.add(new Pair<Entity, TransparentTriangle>(this, copiedTriangle));
				}
								
				sortAllTransparentTriangles();
			}
		}
	}

	/**Adds entity sorted by z-value in world position.
	 * @param entity
	 */
	private void addTransparent(Entity entity) {		
		
		transparents.add(entity);
	}

	public abstract void react(StaticPhysicsObject staticPhysicsObject);

	public abstract void react(DynamicPhysicsObject dynamicPhysicsObject);

	public abstract void react(GhostPhysicsObject ghostPhysicsObject);
	
	public abstract void react(TerrainPhysicsObject terrainPhysicsObject);


	/**
	 * Adds an Entity to the renderingMap.
	 */
	void addRenderingEntity() {
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
	 * 
	 * @param removeFromEntities - set to false if iterating through Entity.entities list to avoid concurrentModification exception
	 */
	public void delete() {
		deleteSpecific();
		PhysicsManager.getInstance().unregisterPhysicsObject(physicsObject);
		if (deleteAllowed) {
			entities.remove(this);
			
			// remove from transparent triangles
			for (Iterator<Pair<Entity, TransparentTriangle>> iterator = transparentTriangles.iterator(); iterator.hasNext();) {
				
				Pair<Entity, TransparentTriangle> triangle = iterator.next();
			    
			    if (triangle.getKey() == this)
			    {
			    	iterator.remove();
			    }
			}
			
			parentingEntities.remove(physicsObject);
			physicsObjectTypeMap.remove(this);
			if (graphicsEntity != null) { //null for Pheromones
				if (renderingMap.containsKey(graphicsEntity.getModel())) {
					renderingMap.get(graphicsEntity.getModel()).remove(this);
				}
				transparents.remove(this);
			}
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
	
	/**
	 * @return - an unmodifiable version of renderingMap for preventing changes on renderingMap while allowing it to be retrieved for rendering
	 */
	public static LinkedList<Entity> getTransparentsList() {
		return transparents;
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
	
	public static void setDeleteAllowed(boolean allowed) {
		deleteAllowed = allowed;
	}
	
	/**Deletes all Entities from physicsObjects and GraphicsEntities maps when game session is quit (return to main menu).
	 * 
	 */
	public static void deleteAllEntities() {
				
		setDeleteAllowed(false);
		for (Entity entity : entities) {
			entity.delete();
		}
		for (PositionLocator loc : deleteableLocators) {
			loc.delete();
		}
		physicsObjectTypeMap.clear();
		renderingMap.clear();
		parentingEntities.clear();
		entities.clear();
		dynamicEntities.clear();
		ants.clear();
		pheromones.clear();
		deleteableLocators.clear();
		transparents.clear();
		transparentTriangles.clear();
		setDeleteAllowed(true);
	}
	
	public static void testDynamicsHaveMovementModes() {
		for (Entity e : dynamicEntities) {
			if (PhysicsManager.getInstance().getPhysicsObject(e.physicsObject.getCollisionBody()) == null) {
				PhysicsManager.getInstance().unregisterPhysicsObject(e.physicsObject);
				PhysicsManager.getInstance().registerPhysicsObject(e.physicsObject);
//				System.out.println("entity " + e + " has been reset");
			}
			else if (MovementManager.getInstance().getTopMovementMode(e.physicsObject) == null) {
//				System.out.println("dynamic entity lost basic movement");
				Vector3f dir = new Vector3f(-1f + 2*(float) Math.random(), 0, -1f + 2*(float) Math.random());
				MovementManager.getInstance().addMovementEntry((DynamicPhysicsObject) e.physicsObject, new BasicMovement((DynamicPhysicsObject) e.physicsObject, dir, Globals.ANT_SPEED));
			} else {
				if (e.objectType == ObjectType.ANT) {
					Ant a = (Ant) e;
					Vector3f currentPos = ((ReadOnlyPhysicsObject) e.getPhysicsObject()).getPosition();
					Vector3f diffPosition = new Vector3f(currentPos.x - a.previousResetPosition.x, 0, currentPos.z - a.previousResetPosition.z);
					a.previousResetPosition = currentPos;
					if (diffPosition.length() < 1f) {
						a.previousResetCtr++;
						if (a.previousResetCtr >= a.previousResetThreshold) {
							a.previousResetCtr = 0;
							Vector3f dir = new Vector3f(-1f + 2*(float) Math.random(), 0, -1f + 2*(float) Math.random());
							MovementManager.getInstance().removeAllMovementEntries(a.physicsObject, false);
							((BasicMovement) MovementManager.getInstance().getBaseMovementMode(a.physicsObject)).setDirection(dir);
							try {
								PhysicsManager.getInstance().unregisterPhysicsObject(a.physicsObject);
								PhysicsManager.getInstance().registerPhysicsObject(a.physicsObject);
//								System.out.println("ant " + a  + " has been reset");
							} catch (Exception ex) {
								System.out.println(e + " failed to be readded to physicsWorld");
							}
						}
					}
				} else if (e.objectType == ObjectType.ENEMY) {
					Enemy en = (Enemy) e;
					Vector3f currentPos = ((ReadOnlyPhysicsObject) e.getPhysicsObject()).getPosition();
					Vector3f diffPosition = new Vector3f(currentPos.x - en.previousResetPosition.x, 0, currentPos.z - en.previousResetPosition.z);
					en.previousResetPosition = currentPos;
					if (diffPosition.length() < 1f) {
						en.previousResetCtr++;
						if (en.previousResetCtr >= en.previousResetThreshold) {
							en.previousResetCtr = 0;
							Vector3f dir = new Vector3f(-1f + 2*(float) Math.random(), 0, -1f + 2*(float) Math.random());
							MovementManager.getInstance().removeAllMovementEntries(en.physicsObject, false);
							((BasicMovement) MovementManager.getInstance().getBaseMovementMode(en.physicsObject)).setDirection(dir);
							try {
								PhysicsManager.getInstance().unregisterPhysicsObject(en.physicsObject);
								PhysicsManager.getInstance().registerPhysicsObject(en.physicsObject);
//								System.out.println("enemy " + e + " has been reset");
							} catch (Exception ex) {
								System.out.println(e + " failed to be readded to physicsWorld");
							}
						}
					}
				}
			}
		}
	}
	
	public static void resetHive() {
		//reset hive 
		entities.add(hive);
		hive.addRenderingEntity();
		physicsObjectTypeMap.put(hive.physicsObject, hive.objectType);
		parentingEntities.put(hive.physicsObject, hive);
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

	// sorts all vertices by the distance between their barrycentre and the camera, from the back to the front
	// positions, normals and texture coords as well as all other data stays the same as in original entities,
	// 
	
	/**Sorts all vertices by the distance between their barrycentre and the camera, from the back to the front.
	 * Positions, normals and texture coords can stay as they are since they are referenced by vertex indices.
	 * However, the array of vertex indices needs to be sorted from back to front and in the process an Entity
	 * might be "split" into multiple pieces if it intersects with another Entity.
	 * 
	 * Finally, these parts of the original Entities will be rendered in the correct sorted order from back to front.
	 * 
	 */
	private static void sortAllTransparentTriangles() 
	{
		if (!Globals.sortingDisabled && MainApplication.getWorldLoaded())
		{
			MainApplication.getExecutor().execute(triangleSorter);
			//Collections.sort(transparentTriangles, triangleComp);	
		}
	}
	
	public static List<Pair<Entity, TransparentTriangle>> getTransparentTriangles()
	{
		return transparentTriangles;
	}
	
	public static void setTransparentTriangles(ArrayList<Pair<Entity, TransparentTriangle>> triangles)
	{
		transparentTriangles = triangles;
	}
	
	/**Updates transparent triangle sorting if camera position has changed.
	 * @param vector3f
	 */
	public static void updateTransparentTriangles(org.lwjgl.util.vector.Vector3f cameraPos)
	{		
		for (Pair<Entity, TransparentTriangle> triangle : transparentTriangles)
		{
			triangle.getValue().calcCameraDist(cameraPos);
		}
				
		sortAllTransparentTriangles();
	}
	
	/**Updates a transparent entitie's triangles' barrycentre if the triangle's parenting Entity's world transform has changed.
	 * @param po
	 */
	public static void setTriangleTransforms(ReadOnlyPhysicsObject po)
	{
		Entity entity = parentingEntities.get(po);
		
		// entity will be null for GhostPhysicsObjects (collision detection triggers)
		if (entity != null && entity.getGraphicsEntity() != null && entity.getGraphicsEntity().getModel().usesTransparency())
		{
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(Maths.vec3fToSlickUtil(po.getPosition()), 
					po.getRotationDegrees().x, po.getRotationDegrees().y, po.getRotationDegrees().z, 
					entity.getGraphicsEntity().getScale()); //transformation matrix to be applied in the shader program
						
			for (Pair<Entity, TransparentTriangle> triangle : transparentTriangles)
			{
				if (triangle.getKey() == entity)
				{
					triangle.getValue().updateTransform(transformationMatrix);
				}
			}
												
			sortAllTransparentTriangles();
		}
	}
}
