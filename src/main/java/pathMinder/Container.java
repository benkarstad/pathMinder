package pathMinder;

import java.util.*;

/**
 * An Item that may contain other Items.
 * The contents of a Container may be limited by weight, volume, count or any combination thereof.
 * Items and containers are organized in a strict tree structure,
 * i.e. each item may be in the contents of no more than one container.
 * <p>
 * This constraint is to be maintained without external interference;
 * when a container has a new Item inserted,
 * it is that container's responsibility to ensure that the item is removed from its previous container, of any.
 * <p>
 * All methods inherited from the Set interface treat Containers as a tree;
 * all elements within a Container and its children are treated as elements of that container for the purpose of these methods.
 * Other methods exist for manipulating only the direct child elements of a Container.
 * <p>
 * TODO: add maxVolume and maxCount;
 */
public class Container extends Item implements Set<Item> {

	public static float getWeight(Collection<? extends Item> items) {
		float weight = 0.0f;
		for(Item item : items) { weight += item.getWeight(); }
		return weight;
	}
	
	public static float getWeight(Container items) { return items.getWeight(); }

	private final HashSet<Item> contents;
	private final float maxWeight; //the maximum weight a container can hold

	/**
	 * Used to detect concurrent modifications.
	 * Incremented each time this container or any descendant containers have their backing Set modified.
	 */
	private int modCount = 0;

	/**
	 * Used to dected concurrent modifications.
	 * It checks the total modification count of all its children.
	*/
	private int getChildrenModCount() {
		int childrenModCount = 0;

		for(Item i : contents) {
			if(i instanceof Container) {
				childrenModCount += ((Container) i).modCount;
			}
		}

		return childrenModCount;
	}

	protected Container(Collection<Item> contents, String name, float weight, float maxWeight, int cost) throws TooManyItemsException {
		super(name, weight, cost);
		if(contents != null && Container.getWeight(contents) > maxWeight) throw new TooManyItemsException("Contents exceed container's maximum weight");
		this.maxWeight = maxWeight;
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
		for(Item item : contents) {
			totalWeight += item.getWeight();
		}
		return  totalWeight;
	}

	/**
	 * Calculates the container's contents.
	 * This method will recursively traverse the entire contents of itself.
	 * @return the weight of all of its contents.
	 */
	public float getContentsWeight() { 
		float totalWeight = 0;
		for(Item item : contents) {
			totalWeight += item.getWeight();
		}
		return totalWeight;
	}

	/**
	 * Checks if adding the specified item would violate any constraints.
	 * Formally, given an element e where fits(e)==true, it can be assumed that add(e) will not throw any exceptions.
	 * @param item the item in question
	 * @return false if attempting to add the specified element to this container would result in an exception
	 */
	public boolean fits(Item item) {
		if(item == null) throw new NullPointerException(); //null item
		if(getContentsWeight() + item.getWeight() > maxWeight || //weight restriction
			item instanceof Container && (item == this || ((Container) item).contains(this))) //self-containing restriction
			return false;

		return true;
	}

	/**
	 * Adds an item if doing so would not violate any constraints.
	 *
	 * If the weight of the container would not exceed its maxWeight, newItem is added.
	 * @param newItem the item being added
	 * @return true if item was added, and false if the the item was already in this Container;
	 * any other case will throw an Exception and the Container will not be modified.
	 * @throws TooManyItemsException if adding the item would violate the weight, volume or count constraints of the Container
	 * @throws IllegalArgumentException if this==newItem or newItem.contains(this) == true
	 * @see Set
	 */
	@Override
	public boolean add(Item newItem) throws TooManyItemsException, IllegalArgumentException {
		if(newItem == null) throw new NullPointerException(); //null item

		//Item is already in a container
		if(newItem.isContained())
			return false;

		//failure case
		if(this.contains(newItem)) return false;

		//Check if the item can fit inside the container.
		if(!fits(newItem))
			throw new TooManyItemsException("Unable to add item to container.");

		//success case
		newItem.setContained(true);
		wasModified();
		contents.add(newItem);
		return true;
	}


	@Override
	public boolean addAll(Collection<? extends Item> items){ throw new UnsupportedOperationException(); }

	/**
	 * Returns true if the specified item is within this item or any of its contents.
	 * More formally, returns true if and only if o is contained within the sub-tree that has this as the root node.
	 * @param o the object being searched for
	 * @return true if the specified item is within this item or any of its contents
	 */
	@Override
	public boolean contains(Object o) {
		if(contents.contains(o)) return true; //check this for o
		for(Item inner : contents) if(inner instanceof Container && ((Container) inner).contains(o)) return true; //check this's contents for o

		return false; //no dice
	}

	/**
	 * Returns the number of elements in this Container.
	 * In other words, returns the total number of Items that have this Container as an ancestor in the inventory tree.
	 * <p>
	 * Note: Container.size() does not count itself.
	 * @return the total number of Items within this Container
	 */
	@Override
	public int size() {
		int size = contents.size();

		for(Item item : contents) if(item instanceof Container) size += ((Container) item).size();

		return size;
	}


	@Override
	public boolean isEmpty() { return contents.isEmpty(); }

	/**
	 * Removes the specified element entirely from this inventory-tree structure,
	 * making it and its contents into a new tree.
	 * The item will be removed if it is contained anywhere within the sub-tree having this container as a root (not just a direct child).
	 * @param o the item being removed
	 * @throws NullPointerException if the specified element is null
	 * @return true if the item was removed
	 */
	@Override
	public boolean remove(Object o) {
		if(o == null) throw new NullPointerException(); //null case
		if(!(o instanceof Item)) return false; //Containers only hold Items

		if(contents.remove(o)) { //base case
			((Item) o).setContained(false);
			wasModified();
			return true;
		}

		for(Item inner : contents) { //recursive case
			if(inner instanceof Container && ((Container)inner).contains(o)) {
				((Container) inner).remove(o);
				return true;
			}
		}

		return false;
	}

	/**
	 * Unsupported operation, call static Item.remove(Collection) instead.
	 * @see Item
	 */
	@Override
	public boolean removeAll(Collection items) { throw new UnsupportedOperationException(); }

	/**
	 * Instead of removing from the current container and adding to the new container,
	 * this function does the necessary checks before hand and then modifies data directly
	 * to improve performace.
	 * 
	 * @param item the item to be moved
	 * @param newContainer the new container to move the item to
	 * @return true if the item was successfully moved
	 */
	public boolean move(Item item, Container newContainer)
	{
		if(item == null) throw new NullPointerException(); //null item
		if(!contains(item)) return false; //Item not located in this container
		if(!newContainer.fits(item)) return false; //Item will not fit in the new container

		//If the requested is contained within a contained item
		if(!contents.remove(item)) {
			for(Item i : contents) {
				if(i instanceof Container && ((Container) i).contains(i)) { //If the item contains the requested item
					((Container) i).contents.remove(item); 					//Remove it from its contents, and stop checking further items
					break;
				}
			}
		}

		newContainer.contents.add(item);
		wasModified();
		newContainer.wasModified();
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> items) {
		if(items == null) throw new NullPointerException();
		if(items.size() == 0) return true; //the empty set is a subset of all sets

		for(Object o : items) if(!this.contains(o)) return false; //if any item is not contained, return false

		return true; //otherwise return true
	}

	/**
	 * Returns an iterator over the entire sub-tree rooted at this item.
	 * Container Iterators are fail-fast, and any concurrent modifications will cause the iterator to throw a ConcurrentModificationException.
	 * @return an iterator that will traverse the entire sub-tree rooted at this item.
	 * @see ContainerIterator
	 */
	@Override
	public ContainerIterator iterator() { return new ContainerIterator(this); }

	/**
	 * Returns an array containing every element e such that this.contains(e).
	 * @return an array containing every element e such that this.contains(e)
	 */
	@Override
	public Object[] toArray() {
		int size = size();
		Item[] newArray = new Item[size];

		ContainerIterator it = this.iterator();
		for(int index = 0; index < size; index++) newArray[index] = it.next();

		return newArray;
	}

	//TODO: Implement public <T> T[] toArray(T[] a)
	@Override
	public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException(); }

	@Override
	public void clear(){ throw new UnsupportedOperationException(); }

	@Override
	public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }

	/**
	 * Every time this container's backing Set is modified,
	 * its modCount and the modCount of all it's ancestor Containers are incremented.
	 * This allows any iterator's to trivially detect any concurrent modification to it or it's children.
	 */
	private void wasModified() {
		modCount++;
	}

	private class ContainerIterator implements Iterator<Item> {

		private final Iterator<Item> contents;
		private int modCount;
		private Container container;
		private ContainerIterator currentChild = null;
		ContainerIterator(Container container) {
			this.container = container;
			contents = container.contents.iterator();
			modCount = container.modCount + container.getChildrenModCount();
		}

		@Override
		public boolean hasNext() {
			if(modCount != container.modCount + container.getChildrenModCount()) throw new ConcurrentModificationException();
			return contents.hasNext() || currentChild != null && currentChild.hasNext();
		}

		@Override
		public Item next() {
			if(modCount != container.modCount + container.getChildrenModCount()) throw new ConcurrentModificationException();
			if(currentChild != null && currentChild.hasNext()) return currentChild.next(); //return next in current child, if exists

			//currentChild is Empty
			if(!contents.hasNext()) throw new NoSuchElementException(); //So is content, throw exception

			Item nextItem = contents.next();

			if(nextItem instanceof Container) currentChild = ((Container) nextItem).iterator();

			return nextItem;
		}
	}
}
