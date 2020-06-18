package pathMinder;


import org.junit.jupiter.api.Test;
import pathMinder.core.Container;
import pathMinder.core.Item;
import pathMinder.core.TooManyItemsException;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ContainerTest {

	@Test
	public void testAdd() {

		Bag bag1 = new Bag(),
			bag2 = new Bag();

		for(int i = 0; i < 10; i++) {
			Ball ball = new Ball( 1, 0.5f, 1);
			//success case
			assertTrue(bag1.add(ball), "bag1 was already full");

			//failure cases
			assertFalse(bag2.add(ball), "ball was already in bag1");
			assertFalse(bag1.add(ball), "bag1 already contains ball");
		}

		//Exception cases
		assertThrows(TooManyItemsException.class, () -> bag1.add(new Ball()));
		assertThrows(TooManyItemsException.class, ()-> { bag1.add(new Ball(0, 6f, 1)); });
	}

	@Test
	public void testRemove() {
		Bag bag = new Bag();
		Bag innerBag = new Bag();
		Ball ball = new Ball();
		innerBag.add(ball);
		Bag innerBag2 = new Bag();
		assertFalse(innerBag2.add(ball), "Ball is in another container.");

		assertFalse(bag.remove(ball), "ball was not yet within bag");
		bag.add(innerBag);
		assertThrows(NullPointerException.class, ()->bag.remove(null), "remove(Item) should not accept null arguments");
		assertTrue(bag.remove(ball), "ball should've been removed");
		assertTrue(bag.remove(innerBag), "innerBag should've been removed");
		//assertNull(ball.getContainer(), "removed ball should no longer have a parent"); ----Removal of items referencing their containers nullifies this test.----
		assertEquals(0, innerBag.size(), "innerBag should be empty");
		assertTrue(innerBag2.add(ball), "Ball should not be in another container.");
		
		assertTrue(innerBag2.move(ball, innerBag), "Ball should be moved to new container.");
		assertTrue(innerBag.contains(ball), "innerBag should contain ball.");
		assertFalse(innerBag2.contains(ball), "innerBag2 should no longer contain ball.");
	}

	@Test
	public void testGetWeight() {
		//test empty weight
		for(int i = 0; i < 100; i += 25) {
			assertEquals(i, new Ball(i, 1, 1).getWeight(), String.format("Weight should be %d", i));
		}

		//test with items one layer deep
		{
			Bag bag = new Bag();
			float weight = bag.getWeight();
			for(int i = 1; i <= 10; i++) {
				Item ball = new Ball();
				weight += ball.getWeight();
				bag.add(ball);
				assertEquals(weight, bag.getWeight());
				assertEquals(weight, Container.getWeight(bag));
			}
		}

		//test with multiple layers of nested Containers
		{
			Bag bag = new Bag();
			float weight = bag.getWeight();
			assertEquals(weight, bag.getWeight());
			for(int i=0; i < 3; i++) {
				Bag innerBag = new Bag();
				innerBag.add(new Ball());
				innerBag.add(new Ball());
				weight += innerBag.getWeight();
				bag.add(innerBag);
				assertEquals(weight, bag.getWeight());
				assertEquals(weight, Container.getWeight(bag));
			}
		}

	}

	@Test
	public void testGetVolume() {
		//test empty volume
		for(int i = 0; i < 100; i += 25) {
			assertEquals(i, new Ball(i, 1, 1).getWeight(), String.format("Weight should be %d", i));
		}
		assertEquals(10f, new Box().getVolume()); //rigid container
		assertEquals(0f, new Bag().getVolume()); //non-rigid container

		//test with items one layer deep
		{
			Bag bag = new Bag();
			float volume = bag.getVolume();
			for(int i = 1; i <= 10; i++) {
				Item ball = new Ball();
				volume += ball.getVolume();
				bag.add(ball);
				assertEquals(volume, bag.getVolume());
				assertEquals(volume, Container.getVolume(bag));
			}
		}

		//test with multiple layers of nested Containers
		{
			Bag bag = new Bag();
			float volume = bag.getVolume();
			assertEquals(volume, bag.getVolume());
			for(int i=0; i < 3; i++) {
				Bag innerBag = new Bag();
				innerBag.add(new Ball());
				innerBag.add(new Ball());
				volume += innerBag.getVolume();
				bag.add(innerBag);
				assertEquals(volume, bag.getVolume());
				assertEquals(volume, Container.getVolume(bag));
			}
		}

	}

	@Test
	public void testSize()  {
		//test empty container
		assertEquals(0, new Bag().size(), "empty bag should have size of zero");

		//test with items one layer deep
		{
			Bag bag = new Bag();
			int size = 0;
			for(int i = 1; i <= 10; i++) {
				Item ball = new Ball();
				size++;
				bag.add(ball);
				assertEquals(size, bag.size());
			}
		}

		//test with multiple layers of nested Containers
		{
			Bag bag = new Bag();
			int size = 0;
			assertEquals(size, bag.size());
			for(int i=0; i < 3; i++) {
				Bag innerBag = new Bag();
				innerBag.add(new Ball());
				innerBag.add(new Ball());
				size += 1+innerBag.size(); // one extra for the bag itself
				bag.add(innerBag);
				assertEquals(size, bag.size());
			}
		}
	}

	@Test
	public void testFits() {
		//TooManyItems
		{
			Bag bag = new Bag();
			for(int i = 0; i < 10; i++) {
				Ball ball = new Ball();
				assertTrue(bag.fits(ball), "the ball should fit");
				bag.add(ball);
			}
			assertFalse(bag.fits(new Ball()), "the ball should not fit");
		}

		//insert self
		{
			Bag bag = new Bag();
			assertFalse(bag.fits(bag));
		}

		//insert parent
		{
			Bag
					bag = new Bag(),
					innerBag = new Bag();

			assertTrue(bag.add(innerBag));
			assertFalse(innerBag.fits(bag));
		}
	}

	@Test
	public void testContains() {

		//self containing
		{
			Bag bag = new Bag();
			assertFalse(bag.contains(bag));
		}

		//inserted and removed items
		{
			Bag bag = new Bag();
			Ball[] balls = new Ball[10];

			for(int i = 0; i < 10; i++) {
				Ball ball = new Ball();
				ball = new Ball();
				assertFalse(bag.contains(ball), "bag should not yet contain this item");
				bag.add(ball);
				balls[i] = ball;
				assertTrue(bag.contains(ball), "bag should contain this item");
			}

			for(Ball ball : balls) {
				assertTrue(bag.contains(ball), "bag should contain this item");
				bag.remove(ball);
				assertFalse(bag.contains(ball), "bag should not contain this item anymore");
			}
		}

		//test with multiple layers of nested Containers
		{
			Bag bag = new Bag();
			LinkedHashSet<Item> innerItems= new LinkedHashSet<>();

			for(int i = 0; i < 3; i++) {
				Bag innerBag = new Bag();
				Ball ball1 = new Ball();
				Ball ball2 = new Ball();
				innerBag.add(ball1);
				innerBag.add(ball2);
				innerItems.add(innerBag);
				innerItems.add(ball1);
				innerItems.add(ball2);
				bag.add(innerBag);
				for(Item item : innerItems) assertTrue(bag.contains(item), "bag should contain this item");
			}

			//after removal
			for(Item item : innerItems) {
				bag.remove(item);
				assertFalse(bag.contains(item));
			}
		}
	}

	@Test
	public void testContainsAll() {

		//throws on a null collection
		assertThrows(NullPointerException.class, ()-> new Bag().containsAll(null));

		//test with multiple layers of nested Containers
		{
			Bag bag = new Bag();
			LinkedHashSet<Item> innerItems= new LinkedHashSet<>();

			for(int i = 0; i < 3; i++) {
				Bag innerBag = new Bag();
				Ball ball1 = new Ball();
				Ball ball2 = new Ball();
				innerBag.add(ball1);
				innerBag.add(ball2);
				innerItems.add(ball1);
				innerItems.add(ball2);
				innerItems.add(innerBag);
				bag.add(innerBag);
				assertTrue(bag.containsAll(innerItems), "bag should contain these items");
			}

			//after removal
			for(Iterator<Item> iterator = innerItems.iterator(); iterator.hasNext();) {
				assertTrue(bag.containsAll(innerItems)); //bag contains all items
				assertTrue(bag.remove(iterator.next())); //remove one item from bag
				assertFalse(bag.containsAll(innerItems)); //bag no longer contains all items
				iterator.remove(); //disregard removed item
			}
		}
	}

	@Test
	public void testIterator() {
		Bag bag = new Bag();
		LinkedHashSet<Item> innerItems= new LinkedHashSet<>();

		for(int i = 0; i < 3; i++) {
			Bag innerBag = new Bag();
			Ball ball1 = new Ball();
			Ball ball2 = new Ball();
			innerBag.add(ball1);
			innerBag.add(ball2);
			innerItems.add(ball1);
			innerItems.add(ball2);
			innerItems.add(innerBag);
			bag.add(innerBag);
		}

		for(Item item : bag) innerItems.remove(item);
		assertEquals(0, innerItems.size());

		//concurrent modifications

		Iterator<Item> iterator = bag.iterator();
		for(int i = 0; i < 5; i++) iterator.next();
		bag.remove(iterator.next());
		assertThrows(ConcurrentModificationException.class, iterator::next);
		
		//nested concurrent modifications
		Bag outer = new Bag();
		Bag inner = new Bag();
		inner.add(new Ball());
		inner.add(new Ball());
		outer.add(inner);
		
		//Modifying inner should throw an error for outer's iterator
		Iterator<Item> outerIterator = outer.iterator();
		inner.add(new Ball());
		assertThrows(ConcurrentModificationException.class, outerIterator::next);
		
		//Modifying outer should not throw an error for inner's iterator
		Iterator<Item> innerIterator = inner.iterator();
		outer.add(new Ball());
		innerIterator.next();
	}

	private static class Box extends Container {
		public Box() {super("Box", 1, 0f, 1000, 10, 10, 10, true); }
	}
	private static class Bag extends Container {
		public Bag() {super("Bag", 1, 0f, 1000, 10, 10, 10, false); }
	}
	private static class Ball extends Item {
		public Ball() {this(1, 1, 1);}
		public Ball(float weight, float volume, int cost) { super("Ball", weight, volume, cost); }
	}
}