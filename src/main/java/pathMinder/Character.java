package pathMinder;

public class Character {

	private String name;

	private int baseStrength = 15;

	private Inventory inventory;

	public Character(String name) {
		this.name = name;

		inventory = new Inventory(this);
	}


	public String getName() { return name; }
	public Encumbrance getEncumbrance() { return Encumbrance.getEncumbrance(inventory.getWeight(), getStrength()); }
	public int getStrength() { return baseStrength; }
}