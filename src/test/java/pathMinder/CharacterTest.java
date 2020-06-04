package pathMinder;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

public class CharacterTest {

	@Test
	public void encumbrance() {
		Character character = new Character("Steve", 12, 30);
		Encumbrance encumbrance = character.getEncumbrance();

		if (character.getInventory().size() == 0) {
			assertEquals(
					String.format("Character.getEncumbrance(): a character with am empty inventory should be at light encumbrance, not %s", encumbrance),
					Encumbrance.Light,
					encumbrance);
		}

		while(character.pick(new Barrel())) assertNotEquals(
				"Character.getEncumbrance(): a character should not be able to attain Atlassian encumbrance from picking items",
				Encumbrance.Atlassian,
				character.getEncumbrance());
	}

	@Test
	public void inventory() {
		Character character = new Character("Steve", 12, 30);
		ArrayList<Item> items = new ArrayList<>(10);

		//pick up a bunch of coins
		for(int count = 0; count < 10; count++) assertTrue("Character.pick(Item): new items were not picked up.", items.add(new GoldCoin()));
		for(Item item : items) character.pick(item);

		//check that all items are present
		assertEquals(
				"Character.pick(Item): character should have 10 items in inventory",
				10,
				character.getInventory().size());

		assertTrue("Character.pick(Item): all items added should be in inventory", character.getInventory().containsAll(items));

		for(Item item : items) assertTrue(String.format("Character.has(Item): character should have %s", item), character.has(item));

		assertTrue(String.format("Character.hasAll(Collection<Item>): character should have all of %s", items), character.hasAll(items));

		for(Item item : items) assertTrue(character.drop(item));
		assertEquals(0, character.getInventory().size());
	}
}