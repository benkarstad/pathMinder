package pathMinder;

import java.rmi.activation.ActivationGroup_Stub;
import java.util.*;

/**
 * An Item that may contain other Items.
 * The contents of a Container may be limited by weight, volume, count or any combination thereof.
 * TODO: add maxVolume and maxCount;
 */
public class Container extends Item implements  Set<Item>{

	public static float getWeight(Collection<? extends Item> items) {
		float weight = (float) 0.0;
		for(Item item : items) { weight += item.getWeight(); }
		return weight;
	}
	public static float getWeight(Container items) { return items.getWeight(); }

	private final LinkedHashSet<Item> contents;
	private final float maxWeight; //the maximum weight a container can hold

	protected Container(Collection<Item> contents, String name, float weight, float maxWeight, float cost) throws TooManyItemsException {
		super(name, weight, cost);
		if(contents != null && Container.getWeight(contents) > maxWeight) throw new TooManyItemsException("Contents exceed container's maximum weight");
		this.maxWeight = maxWeight+weight;
		this.contents = (contents == null) ? new LinkedHashSet<>(15) : new LinkedHashSet<>(contents);
	}

	/**
	 * Calculates the weight of this and its contents.
	 * This method will recursively traverse the entire contents of itself.
	 * @return the weight of this item and all of its contents.
	 */
	@Override
	public float getWeight() {
		float totalWeight = super.getWeight();
		for(Item item : this) {
			totalWeight+=item.getWeight();
		}
		return  totalWeight;
	}

	/**
	 * Adds an item if there's enough available space.
	 * If the weight of the container would not exceed its maxWeight, newItem is added.
	 * @param newItem the item being added
	 * @return true if item was added
	 * @see Set
	 */
	@Override
	public boolean add(Item newItem) {
		if(newItem == null) throw new NullPointerException();
		if(getWeight() + newItem.getWeight() <= maxWeight) return contents.add(newItem);
		return false;
	}

	/**
	 * Adds all the provided items, unless doing so would violate any of the constraints of the container,
	 * in which case, nothing is added;
	 * for example, if  the total weight would exceed the maximum,
	 * or an item is not permitted to be added to the container.
	 * <p>
	 * Attempting to add duplicates does not violate any constraints.
	 * @param items the items being added to this
	 * @return true if this was modified as a result of this call
	 */
	@Override
	public boolean addAll(Collection<? extends Item> items) {
		if(getWeight() + getWeight(items) > maxWeight) return false; //weight constraint
		return contents.addAll(items);
	}

	/**
	 * Empties this container into a pile. Removes all Items from this and returns them in an ItemGroup.
	 * @return the ItemGroup containing all this's contents;
	 */
	public ItemGroup dump() {
		ItemGroup pile = new ItemGroup(contents, String.format("Pile, contents of: %s", getName()), Integer.MAX_VALUE);
		contents.clear();
		return pile;
	}


	//=================== Collection<Item> passed through methods ===================//

	@Override
	public boolean contains(Object item) { return contents.contains(item); }

	@Override
	public int size() { return contents.size(); }

	@Override
	public boolean isEmpty() { return contents.isEmpty(); }

	@Override
	public boolean remove(Object item) { return contents.remove(item); }

	@Override
	public boolean removeAll(Collection items) { return contents.removeAll(items); }

	@Override
	public boolean containsAll(Collection<?> items) { return contents.containsAll(items); }

	@Override
	public Iterator<Item> iterator() { return contents.iterator(); }

	@Override
	public Object[] toArray() {
		return contents.toArray();
	}

	@Override
	public <Item> Item[] toArray(Item[] a) {
		return contents.toArray(a);
	}

	@Override
	public void clear() { throw new UnsupportedOperationException("to remove all Items, call this.dump"); }

	@Override
	public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }

}
