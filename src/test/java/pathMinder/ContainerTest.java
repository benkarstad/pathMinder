package pathMinder;


import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ContainerTest {

	@Test
	public void testAdd() {
		Box box = new Box();
		for(int i = 0; i < 10; i++) {
			assertTrue(box.add(new Ball()), "box was already full");
		}

		assertThrows(TooManyItemsException.class, () -> box.add(new Ball()));
	}

	@Test
	public void testRemove() {
		Box box = new Box();
		Box innerBox = new Box();
		Ball ball = new Ball();
		innerBox.add(ball);
		Box innerBox2 = new Box();
		assertFalse(innerBox2.add(ball), "Ball is in another container.");

		assertFalse(box.remove(ball), "ball was not yet within box");
		box.add(innerBox);
		assertThrows(NullPointerException.class, ()->box.remove(null), "remove(Item) should not accept null arguments");
		assertTrue(box.remove(ball), "ball should've been removed");
		assertTrue(box.remove(innerBox), "innerBox should've been removed");
		//assertNull(ball.getContainer(), "removed ball should no longer have a parent"); ----Removal of items referencing their containers nullifies this test.----
		assertEquals(0, innerBox.size(), "innerBox should be empty");
		assertTrue(innerBox2.add(ball), "Ball should not be in another container.");
	}

	@Test
	public void testGetWeight() {
		//test empty weight
		for(int i = 0; i < 100; i += 25) {
			assertEquals(i, new Container(null, "TestContainer", i, 10, 1).getWeight());
		}

		//test with items one layer deep
		{
			Box box = new Box();
			float weight = box.getWeight();
			for(int i = 1; i <= 10; i++) {
				Item ball = new Ball();
				weight += ball.getWeight();
				box.add(ball);
				assertEquals(weight, box.getWeight());
				assertEquals(weight, Container.getWeight(box));
			}
		}

		//test with multiple layers of nested Containers
		{
			Container box = new Box();
			float weight = box.getWeight();
			assertEquals(weight, box.getWeight());
			for(int i=0; i < 3; i++) {
				Box innerBox = new Box();
				innerBox.add(new Ball());
				innerBox.add(new Ball());
				weight += innerBox.getWeight();
				box.add(innerBox);
				assertEquals(weight, box.getWeight());
				assertEquals(weight, Container.getWeight(box));
			}
		}

	}

	@Test
	public void testSize()  {
		//test empty container
		assertEquals(0, new Box().size(), "empty box should have size of zero");

		//test with items one layer deep
		{
			Box box = new Box();
			int size = 0;
			for(int i = 1; i <= 10; i++) {
				Item ball = new Ball();
				size++;
				box.add(ball);
				assertEquals(size, box.size());
			}
		}

		//test with multiple layers of nested Containers
		{
			Container box = new Box();
			int size = 0;
			assertEquals(size, box.size());
			for(int i=0; i < 3; i++) {
				Box innerBox = new Box();
				innerBox.add(new Ball());
				innerBox.add(new Ball());
				size += 1+innerBox.size(); // one extra for the box itself
				box.add(innerBox);
				assertEquals(size, box.size());
			}
		}
	}

	@Test
	public void testFits() {
		//TooManyItems
		{
			Box box = new Box();
			for(int i = 0; i < 10; i++) {
				Ball ball = new Ball();
				assertTrue(box.fits(ball), "the ball should fit");
				box.add(ball);
			}
			assertFalse(box.fits(new Ball()), "the ball should not fit");
		}

		//insert self
		{
			Box box = new Box();
			assertFalse(box.fits(box));
		}

		//insert parent
		{
			Box
					box = new Box(),
					innerBox = new Box();

			assertTrue(box.add(innerBox));
			assertFalse(innerBox.fits(box));
		}
	}

	@Test
	public void testContains() {

		//self containing
		{
			Box box = new Box();
			assertFalse(box.contains(box));
		}

		//inserted and removed items
		{
			Box box = new Box();
			Ball[] balls = new Ball[10];

			for(int i = 0; i < 10; i++) {
				Ball ball = new Ball();
				ball = new Ball();
				assertFalse(box.contains(ball), "box should not yet contain this item");
				box.add(ball);
				balls[i] = ball;
				assertTrue(box.contains(ball), "box should contain this item");
			}

			for(Ball ball : balls) {
				assertTrue(box.contains(ball), "box should contain this item");
				box.remove(ball);
				assertFalse(box.contains(ball), "box should not contain this item anymore");
			}
		}

		//test with multiple layers of nested Containers
		{
			Container box = new Box();
			LinkedHashSet<Item> innerItems= new LinkedHashSet<>();

			for(int i = 0; i < 3; i++) {
				Box innerBox = new Box();
				Ball ball1 = new Ball();
				Ball ball2 = new Ball();
				innerBox.add(ball1);
				innerBox.add(ball2);
				innerItems.add(innerBox);
				innerItems.add(ball1);
				innerItems.add(ball2);
				box.add(innerBox);
				for(Item item : innerItems) assertTrue(box.contains(item), "box should contain this item");
			}

			//after removal
			for(Item item : innerItems) {
				box.remove(item);
				assertFalse(box.contains(item));
			}
		}
	}

	@Test
	public void testContainsAll() {

		//throws on a null collection
		assertThrows(NullPointerException.class, ()-> new Box().containsAll(null));

		//contains an empty Collection
		{
			Container box = new Box();

			for(int i = 0; i < 3; i++) {
				Box innerBox = new Box();
				innerBox.add(new Ball());
				innerBox.add(new Ball());
				box.add(innerBox);
			}
		}

		//test with multiple layers of nested Containers
		{
			Container box = new Box();
			LinkedHashSet<Item> innerItems= new LinkedHashSet<>();

			for(int i = 0; i < 3; i++) {
				Box innerBox = new Box();
				Ball ball1 = new Ball();
				Ball ball2 = new Ball();
				innerBox.add(ball1);
				innerBox.add(ball2);
				innerItems.add(ball1);
				innerItems.add(ball2);
				innerItems.add(innerBox);
				box.add(innerBox);
				assertTrue(box.containsAll(innerItems), "box should contain these items");
			}

			//after removal
			for(Iterator<Item> iterator = innerItems.iterator(); iterator.hasNext();) {
				assertTrue(box.containsAll(innerItems)); //box contains all items
				assertTrue(box.remove(iterator.next())); //remove one item from box
				assertFalse(box.containsAll(innerItems)); //box no longer contains all items
				iterator.remove(); //disregard removed item
			}
		}
	}

	@Test
	public void testIterator() {
		Container box = new Box();
		LinkedHashSet<Item> innerItems= new LinkedHashSet<>();

		for(int i = 0; i < 3; i++) {
			Box innerBox = new Box();
			Ball ball1 = new Ball();
			Ball ball2 = new Ball();
			innerBox.add(ball1);
			innerBox.add(ball2);
			innerItems.add(ball1);
			innerItems.add(ball2);
			innerItems.add(innerBox);
			box.add(innerBox);
		}

		for(Item item : box) innerItems.remove(item);
		assertEquals(0, innerItems.size());

		//concurrent modifications

		Iterator<Item> iterator = box.iterator();
		for(int i = 0; i < 5; i++) iterator.next();
		box.remove(iterator.next());
		assertThrows(ConcurrentModificationException.class, iterator::next);
	}

	private static class Box extends Container {
		public Box() {super(null, "Box", 1, 10, 1);}

		/**
		 * @return a Box instance full of Balls
		 */
		public static Box getFullInstance() {
			Box box = new Box();
			for(Ball newBall = new Ball(); box.fits(newBall); newBall = new Ball()) box.add(newBall);
			return box;
		}
	}
	private static class Ball extends Item {public Ball() {super("Ball", 1, 1);}}
}