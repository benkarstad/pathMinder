package pathMinder;

import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.*;

public class ContainerTest {
	@Test
	public void getWeight() {
		Box box = new Box();
		float weight;

		//check empty weight
		weight = box.getWeight();
		assertEquals(String.format("Container.getWeight(): box should weigh 1, not %f", weight), 1.0, weight, 0.0);

		//check weight with some stuff
		for(int i = 0; i < 2; i++) box.add(new Ball());
		weight = box.getWeight();
		assertEquals(String.format("Container.getWeight(): box should weigh 3, not %f", weight), 3.0, weight, 0.0);

		assertEquals(
				String.format("static Container.getWeight(Container): empty box should weigh 1.0, not %f", Container.getWeight(new Box())),
				1,
				Container.getWeight(new Box()),
				0.0);
		assertEquals(
				String.format("static Container.getWeight(Container): full box should weigh 11.0, not %f", Container.getWeight(Box.getFullInstance())),
				11.0,
				Container.getWeight(Box.getFullInstance()),
				0.0);
	}

	@Test
	public void add() {
		Box box = new Box();
		for(int i = 0; i < 10; i++) {
			assertTrue("Container.add(): box was already full", box.add(new Ball()));
		}
		assertFalse("Container.add(): box wasn't full yet", box.add(new Ball()));
	}

	@Test
	public void addAll() {
		Box box = new Box();
		assertTrue("Container.addAll(Collection<? extends Item>): contents should've been added", box.addAll(Box.getFullInstance()));
		assertFalse("Container.addAll(Collection<? extends Item>): contents should've already been in box", box.addAll(box));
	}

	@Test
	public void dump() {
		String testName = "SpecialBall";
		Box box = Box.getFullInstance();
		for(Item ball : box) ball.nickName = testName;
		HashSet<Item> set = new HashSet<>(box.dump());
		for(Item item : set) assertEquals("Container.dump()", item.nickName, testName);
	}

	@Test
	public void remove() {
		Box box = new Box();
		Ball ball = new Ball();

		assertFalse("Collection.remove(Item): ball was not yet within box", box.remove(ball));
		box.add(ball);
		assertTrue("Collection.remove(Item): ball should've been removed", box.remove(ball));
	}

	private static class Box extends Container {
		public Box() {super(null, "Box", 1, 10, 1);}

		/**
		 * @return a Box instance full of Balls
		 */
		public static Box getFullInstance() {
			Box box = new Box();
			while(box.add(new Ball()));
			return box;
		}
	}
	private static class Ball extends Item {public Ball() {super("Ball", 1, 1);}}
}