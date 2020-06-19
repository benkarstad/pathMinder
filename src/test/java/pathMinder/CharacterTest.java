package pathMinder;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import pathMinder.core.*;
import pathMinder.core.Character;

public class CharacterTest {

	@Test
	public void testEncumbrance() {
		Character character = new Character("Steve", 12, 30);
		Encumbrance encumbrance = character.getEncumbrance();

		if (character.getInventory().size() == 0) {
			assertEquals(
					Encumbrance.Light,
					encumbrance,
					String.format("Character.getEncumbrance(): a character with am empty inventory should be at light encumbrance, not %s", encumbrance));
		}

		while(character.pick(new Barrel())) assertNotEquals(
				Encumbrance.Atlassian,
				character.getEncumbrance(),
				"Character.getEncumbrance(): a character should not be able to attain Atlassian encumbrance from picking items");
	}

	@Test
	public void testInventory() {
		Character character = new Character("Steve", 12, 30);
		ArrayList<Item> items = new ArrayList<>(10);

		//pick up a bunch of coins
		for(int count = 0; count < 10; count++) assertTrue(items.add(new GoldCoin()), "Character.pick(Item): new items were not picked up.");
		for(Item item : items) character.pick(item);

		//check that all items are present
		assertEquals(
				10,
				character.getInventory().size(),
				"Character.pick(Item): character should have 10 items in inventory");

		assertTrue(character.getInventory().containsAll(items), "Character.pick(Item): all items added should be in inventory");

		for(Item item : items) assertTrue(character.has(item), String.format("Character.has(Item): character should have %s", item));

		assertTrue(character.hasAll(items), String.format("Character.hasAll(Collection<Item>): character should have all of %s", items));

		for(Item item : items) assertTrue(character.drop(item));
		assertEquals(0, character.getInventory().size());
	}
}