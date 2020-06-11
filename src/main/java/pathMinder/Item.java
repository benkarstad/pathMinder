package pathMinder;

import java.util.Collection;

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
 */
public abstract class Item {

	/**
	 * The name that identifies similar instances of an item. This does not uniquely identify this item,
	 * to do that, the player may give their item a nickname.
	 */
	private final String name;

	/**
	 * The true base value of this item, measured in copper pieces. This number may be affected by other factors,
	 * such as being broken or masterwork.
	 */
	private final int cost;

	/**
	 * The weight of this item, measured in pounds.
	 */
	private final float weight;

	private final float volume;

	/**
	 * This may be set by the user to identify specific instances of an item,
	 * such as a lucky arrow or a backpack filled with valuables.
	 */
	public String nickName;
	public String description;
	private boolean contained;

	protected Item(String name, float weight, float volume, int cost) {
		this.name = name;
		this.weight = weight;
		this.volume = volume;
		this.cost = cost;
		this.contained = false;
	}

	protected Item(Item item) {
		this(item.name, item.weight, item.volume, item.cost);
	}

	/**
	 * Returns the weight of this item and any other weight associated with it.
	 * This is not necessarily the weight of the item itself,
	 * some implementations of Item — such as Containers — include the weight of their contents as well.
	 * @see Container
	 * @return the weight of the item
	 */
	public float getWeight() { return weight; }

	/**
	 * Returns the volume of this item.
	 * @return the volume of this item.
	 */
	public float getVolume() { return volume; }

	/**
	 * @return the cost of the item
	 */
	public float getCost(){ return cost; }

	/**
	 * @return the name of the item
	 */
	public String getName() { return name; }

	public void setContained(boolean contained) { this.contained = contained; }

	/**
	 * @return if the item is contained within a container
	 */
	public boolean isContained() { return contained; }
}
