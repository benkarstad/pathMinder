package pathMinder;

import java.util.Set;

/**
 * A conceptual group of items. This represents a group of items that,
 * while not within a physical container, are grouped together.
 * Two examples of this are a person's inventory and a pile on the ground.
 */
public class ItemGroup extends Container {
	public ItemGroup(String name, float maxWeight) { this(null, name, maxWeight); }
	public ItemGroup(Set<Item> contents, String name, float maxWeight) { super(contents, name, 0, maxWeight, 0); }
}
