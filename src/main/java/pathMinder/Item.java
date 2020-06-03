package pathMinder;

import java.util.Comparator;

/**
 * A physical item. Items have a number of attributes; some, like their weight, can be easily determined by empirical means.
 * Others, like their gold value and true identity may be more difficult to discern;
 * A certain player may not know what an item is, or may have incorrect/inaccurate information.
 * <p>
 * Calling no-argument get methods only reveals immediately apparent information, like weight.
 * Other, possibly hidden, aspects may require providing the identity of the observing character to glean more.
 * <p>
 * Identifying items is an integral part of the narrative.
 * This feature allows the DM to hold finer control over what the party knows,
 * while still providing the full features of pathMinder's inventory system.
 * <p>
 * TODO: Implement intel based parameters;
 * TODO: [LOW] Link items back to their Container
 */
public abstract class Item {
	/**
	 * The name that identifies similar instances of an item. This does not uniquely identify this item,
	 * to do that, the player may give their item a nickname.
	 */
	private final String name;
	/**
	 * The true base value of this item, measured in gold pieces. This number may be affected by other factors,
	 * such as being broken or masterwork.
	 */
	private final float cost;
	/**
	 * The weight of this item, measured in pounds.
	 */
	private final float weight;
	/**
	 * This may be set by the user to identify specific instances of an item,
	 * such as a lucky arrow or a backpack filled with valuables.
	 */
	public String nickName;
	public String description;

	protected Item(String name, float weight, float cost) {
		this.name = name;
		this.weight = weight;
		this.cost = cost;
	}

	/**
	 * Returns the weight of this item and any other weight associated with it.
	 * This is not necessarily the weight of the item itself,
	 * some implementations of Item — such as Containers — include the weight of their contents as well.
	 * @see Container
	 * @return the weight of the item
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * @return the cost of the item
	 */
	public float getCost(){return cost;}

	/**
	 * @return the name of the item
	 */
	public String getName() {return name;}
}
