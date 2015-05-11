package at.antSim.guiWrapper;

import java.util.LinkedList;
import java.util.List;

/**A GuiState represents a Gui's appearance at a certain state<br>
 * <br>
 * {@link GuiState} holds a list of Gui elements and each element can be positioned relatively to the previous element,
 * so the order of insertion is significant -> elements should be added in the order they should be rendered.<br>
 * If two Gui elements have the same position, the later added one will be drawn above the earlier added one.
 * 
 * @author Flo
 *
 */
public class GuiState {
	
	List<GuiContainer> elements = new LinkedList<GuiContainer>();

	/**Adds a container for positioning and scaling a group of gui elements.
	 * 
	 */
	public void addContainer(GuiContainer container) {
		elements.add(container);
	};
	
	/**Returns a list of all elements in this gui.
	 * 
	 * @return - list of {@link GuiElement}s
	 */
	public List<GuiContainer> getElements() {
		return elements;
	}
	
}