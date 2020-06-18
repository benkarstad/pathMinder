package pathMinder.core;

/**
 * A conceptual group of items. This represents a group of items that,
 * while not within a physical container, are grouped together.
 * Two examples of this are a person's inventory and a pile on the ground.
 */
public class ItemGroup extends Container {
	public ItemGroup(String name, float maxWeight, float maxVolume, int maxCount) {
		super(name, 0, 0, 0,
				maxWeight, maxVolume, maxCount, false);
	}
}
