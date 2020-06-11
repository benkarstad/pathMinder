package pathMinder;

import java.util.Set;

/**
 * A special ItemGroup linked to a Character. Each character has one, and only one, inventory.
 * A character's inventory represents the entirety of what a character has on their person in a given moment.
 * Anything in a character's inventory moves with them and affects their movement through Encumbrance.
 * Unlike most containers, an Inventory's capacity isn't fixed and is dictated by the players strength;
 * for example, if a character is already overencumbered and their strength is reduced,
 * they will collapse, unable to move without shedding some equipment.
 *
 * @see Character
 * @see Encumbrance
 */
class Inventory extends ItemGroup {

	private final Character holder;

	Inventory(Character character) {
		super(String.format("%s's inventory", character.getName()), Float.MAX_VALUE, Float.MAX_VALUE, Integer.MAX_VALUE);
		holder = character;
	}

	@Override
	public boolean add(Item newItem) {
		if(getWeight() + newItem.getWeight() <= getMaxWeight()) return super.add(newItem);

		return false;
	}

	float getMaxWeight() { return Encumbrance.Heavy.getCapacity(holder.getStrength()); }
}