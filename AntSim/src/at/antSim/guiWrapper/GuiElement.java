package at.antSim.guiWrapper;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import at.antSim.Globals;
import at.antSim.eventSystem.EventListener;
import at.antSim.eventSystem.EventPriority;
import at.antSim.eventSystem.events.MouseButtonPressedEvent;
import at.antSim.eventSystem.events.MouseButtonReleasedEvent;
import at.antSim.graphics.models.RawModel;

/**Represents an abstract class for Element's which shall be drawn in the GUI.
 * 
 * @author Flo
 *
 */
public abstract class GuiElement {

	private RawModel model; //holds geometry data
	private Vector2f position; //need to store center position of the gui quad since gui Texture has no underlying ModelData storing positional data
	private Vector2f scale;
	private String id;
	private int textureId;
	private GuiContainer parent;
	
	//positional values in pixel
	private Point topLeft;
	private Point middle;
	private int width;
	private int height;
	
	private HorReference horRef;
	private HorPositions horPos;
	private int horOffset;
	private VerReference verRef;
	private VerPositions verPos;
	private int verOffset;
	private int textureWidth;
	private int textureHeight;
	
	//transparency
	private float transparency; //0: will be non-transparent, opaque; 1: will be fully transparent
	
	//blend color and factor
	private Vector3f blendColor;
	private float blendFactor;

	/**Constructs a new {@link GuiElement}.
	 * 
	 * @param id - the {@link GuiElement}'s id as String
	 * @param parent - the {@link GuiElement}'s parenting {@link GuiContainer}
	 * @param model - the {@link GuiElement}'s geometric model
	 * @param textureId - id of the {@link GuiElement}'s texture as assigned by OpenGL
	 * @param textureWidth - width of the {@link GuiElement}'s texture
	 * @param textureHeight - height of the {@link GuiElement}'s texture
	 * @param desiredWidth - desired width of the Gui element in pixels
	 * @param desiredHeight - desired height of the Gui element in pixels
	 * @param horRef - {@link HorReference} of the {@link GuiElement}
	 * @param horPos - {@link HorPosition} of the {@link GuiElement}
	 * @param horOffset - horizontal offset in pixels
	 * @param verRef - {@link VerReference} of the {@link GuiElement}
	 * @param verPos - {@link VerPosition} of the {@link GuiElement}
	 * @param verOffset - vertical offset in pixels
	 * @param transparency - 0: opaque, 1: fully transparent
	 * @param blendColor - color to blend with the {@link GuiElement}'s texture
	 * @param blendFactor - 0: draw 100% original texture, 1: fully blend texture with blendColor
	 */
	public GuiElement(String id, GuiContainer parent, RawModel model, int textureId, int textureWidth, int textureHeight, int desiredWidth, int desiredHeight, 
			HorReference horRef, HorPositions horPos, int horOffset, VerReference verRef, VerPositions verPos, int verOffset, float transparency, Vector3f blendColor, float blendFactor) {
		
		this.width = desiredWidth;
		this.height = desiredHeight;
		this.model = model;
		this.textureId = textureId;
		this.id = id;
		this.parent = parent;
		this.horRef = horRef;
		this.horPos = horPos;
		this.horOffset = horOffset;
		this.verRef = verRef;
		this.verPos = verPos;
		this.verOffset = verOffset;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.transparency = transparency;
		this.blendColor = blendColor;
		this.blendFactor = blendFactor;
		
		this.scale = new Vector2f(((float) textureWidth/Globals.displayWidth) * ((float) desiredWidth/textureWidth), ((float) textureHeight/Globals.displayHeight) * ((float) desiredHeight/textureHeight));
		//this.scale = new Vector2f((float) desiredWidth/textureWidth*desiredWidth/Globals.displayWidth, (float) desiredHeight/textureHeight*desiredHeight/Globals.displayHeight/9*16);
		
		System.out.println("desired width: " + desiredWidth + ", texture width: " + textureWidth);
		System.out.println("desired height: " + desiredHeight + ", texture height: " + textureHeight);
		System.out.println("scale x: " + scale.x);
		System.out.println("scale y: " + scale.y);
				
		calculatePos();
		
		if (parent != null) {
			parent.getChildren().add(this);
		}
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public Vector2f getScale() {
		return scale;
	}
	
	public RawModel getRawModel() {
		return model;
	}
	
	public String getId() {
		return id;
	}
	
	/**
	 * @return - a number < 0 if no texture shall be drawn for this gui element, or >= 0 if a texture shall be drawn
	 */
	public int getTextureId() {
		return textureId;
	}
	
	public Point getTopLeft() {
		return topLeft;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	/**Calculates the position of this Gui Element in OpenGl -1 to 1 normalized device coordinates.
	 * 
	 */
	public void calculatePos() {
		
		int top = 0;
		int left = 0;
		
		int refWidth;
		int refHeight;
		int refTop;
		int refLeft;
		
		GuiElement horRefElem;
		GuiElement verRefElem;
		
		if (parent != null) {
			if (horRef == HorReference.PARENT) {
				horRefElem = parent;
			}
			else {
				if (parent.getChildren().size() > 0) {
					horRefElem = parent.getChildren().get(parent.getChildren().size() - 1);
				} else {
					horRefElem = parent;
				}
			}
			if (verRef == VerReference.PARENT) {
				verRefElem = parent;
			}
			else {
				if (parent.getChildren().size() > 0) {
					verRefElem = parent.getChildren().get(parent.getChildren().size() - 1);
				} else {
					verRefElem = parent;
				}
			}
			refWidth = horRefElem.getWidth();
			refHeight = verRefElem.getHeight();
			refTop = verRefElem.getTopLeft().getY();
			refLeft = horRefElem.getTopLeft().getX();
		} else {
			refWidth = Globals.displayWidth;
			refHeight = Globals.displayHeight;
			refTop = 0;
			refLeft = 0;
		}
		
		switch (horPos) {
			case LEFT:
				left = (int) (refLeft + horOffset); 
				break;
			case CENTER:
				left = (int) (refLeft + refWidth/2 - width/2);
				break;
			case RIGHT:
				left = (int) (refLeft + refWidth - horOffset - width);
				break;
			case LEFT_OF:
				left = (int) (refLeft - width - horOffset);
				break;
			case RIGHT_OF:
				left = (int) (refLeft + refWidth + horOffset);
				break;
		}
		
		switch (verPos) {
			case TOP:
				top = (int) (refTop + verOffset); 
				break;
			case MIDDLE:
				top = (int) (refTop + refHeight/2 - height/2);
				break;
			case BOTTOM:
				top = (int) (refTop + refHeight - verOffset - height);
				break;
			case ABOVE:
				top = (int) (refTop - verOffset - height);
				break;
			case BELOW:
				top = (int) (refTop + refHeight + verOffset);
				break;
		}
				
		this.topLeft = new Point(left, top);
		this.middle = new Point(left + width/2, top + height/2);
		this.position = new Vector2f((float) middle.getX()/Globals.displayWidth * 2 - 1, ((float) middle.getY()/Globals.displayHeight * 2 - 1) * -1f);
		
		System.out.println("topLeft: " + topLeft.getX() + ", " + topLeft.getY());
		System.out.println("middle: " + middle.getX() + ", " + middle.getY());
		System.out.println("position: " + position.x + ", " + position.y);
	}

	public float getTransparency() {
		return transparency;
	}

	public void setTransparency(float transparency) {
		this.transparency = transparency;
	}

	public Vector3f getBlendColor() {
		return blendColor;
	}

	public float getBlendFactor() {
		return blendFactor;
	}

	@EventListener(priority = EventPriority.HIGH)
	public void onMousePress(MouseButtonPressedEvent event){
		System.out.println("mouse pressed");
	}
	
	@EventListener
	public void onMouseReleased(MouseButtonReleasedEvent event){
		System.out.println("mouse released");
	}
}