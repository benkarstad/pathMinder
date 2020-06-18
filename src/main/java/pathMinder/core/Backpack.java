package pathMinder.core;

public class Backpack extends Container{
	public Backpack() { this("Backpack, common", 2f, 1/4f, 2); }
	protected Backpack(String name, int cost) { this(name, 2f, 1/4f, cost); }

	protected Backpack(String name, float weight, float volume, int cost) {
		super(name, weight, volume, cost, Float.MAX_VALUE, 2, Integer.MAX_VALUE, false);
	}
}
