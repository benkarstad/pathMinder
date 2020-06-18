package pathMinder.core;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class Character {

	private String name;

	private int baseStrength;
	@SuppressWarnings("unused")
	private int baseSpeed;

	private Inventory inventory;

	public Character(String name) { this(name, 10, 30); }
	public Character(String name, int baseStrength, int baseSpeed) {
		this.name = name;
		this.baseStrength = baseStrength;
		this.baseSpeed = baseSpeed;
		inventory = new Inventory(this);
	}


	public String getName() { return name; }
	public Encumbrance getEncumbrance() { return Encumbrance.getEncumbrance(inventory.getWeight(), getStrength()); }
	public int getStrength() { return baseStrength; }

	public boolean pick(Item item) { return inventory.add(item); }
	public boolean drop(Item item) { return inventory.remove(item); }
	public Set<Item> getInventory() { return new LinkedHashSet<>(inventory); }
	public boolean has(Item item) { return inventory.contains(item); }
	public boolean hasAll(Collection<Item> items) { return inventory.containsAll(items); }
}