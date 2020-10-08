package pathMinder.core;
//TODO: Implement Encumbrance for various sizes, leg counts, etc.
/**
 * ... See Core Rulebook Ch.7 (Carrying Capacity)
 */
public enum Encumbrance {
	Light("Light", Integer.MAX_VALUE, 0, 4) {
		@Override
		public int getSpeed(int baseSpeed) {
			return baseSpeed;
		}
		@Override
		public int getCapacity(int strength) {
			return Heavy.getCapacity(strength)/3;
		}
	},
	Medium("Medium", +3, -3, 4) {
		@Override
		public int getSpeed(int baseSpeed) {
			return (int) (5*Math.ceil(2 * baseSpeed / 15.0f));
		}
		@Override
		public int getCapacity(int strength) {
			return 2*Heavy.getCapacity(strength)/3;
		}
	},
	Heavy("Heavy", +1, -6, 3) {
		@Override
		public int getSpeed(int baseSpeed) {
			return Medium.getSpeed(baseSpeed);
		}
		@Override
		public int getCapacity(int strength) {
			if(strength < 30) {
				return strengthVals[strength];
			}

			//See Core Rulebook Ch.7 (Tremendous Strength);
			int modIndex = strength%10+20; //the index of the capacity which is multiplied to calculate the true value
			int factor = (int) Math.pow(4, Math.floorDiv((strength-modIndex), 10)); //the factor by which the capacity is multiplied
			return strengthVals[modIndex] * factor;
		}
	},
	Overencumbered("Overencumbered", 0, 0, 0) {
		@Override
		public int getSpeed(int baseSpeed) {
			return 1;
		}
		@Override
		public int getCapacity(int strength) {
			return 2*Heavy.getCapacity(strength);
		}
	},
	Atlassian("Atlassian", 0, 0, 0) { //TODO: Come up with a better name;
		@Override
		public int getSpeed(int baseSpeed) {
			return 0;
		}
		@Override
		public int getCapacity(int strength) {
			return (int) (Math.pow(3, 31)-1);
		}
	}; //This value is physically impossible, the player should not be able to proceed in this state.

	private static final int[] strengthVals = {
			0, 10, 20, 30, 40, 50, 60, 70, 80, 90,
			100, 115, 130, 150, 175, 200, 230, 260, 300, 350,
			400, 460, 520, 600, 700, 800, 920, 1040, 1200, 1400};

	private final String name;
	public final int maxDex; //Maximum dexterity bonus to armor class
	public final int ACP; //ArmorCheckPenalty: affects all dexterity and strength based skills
	public final int runMult; //Speed multiplier for running calculations

	Encumbrance(String name, int maxDex, int ACP, int runMult) {
		this.name = name;
		this.maxDex = maxDex;
		this.ACP = ACP;
		this.runMult = runMult;
	}

	public String toString() { return name; }

	/**
	 * This static method can be used to determine the current weight-encumbrance of a creature based on their strength and equipment
	 * <p>
	 * See Core Rulebook Ch.7 (Table 7-4)
	 * @param equipmentWeight the total weight that the character is carrying.
	 * @param strength the strength score of the creature
	 * @return the encumbrance level of the creature
	 */
	public static Encumbrance getEncumbrance(float equipmentWeight, int strength) { //iterates through and finds the lowest Encumbrance value that equipmentWeight does not exceed;
		for(Encumbrance encumbrance : Encumbrance.values()) {
			if(equipmentWeight <= encumbrance.getCapacity(strength)) {
				return encumbrance;
			}
		}
		return null;
	}

	/**
	 * Calculates the speed of a player based on their base speed.
	 * <p>
	 * See Core Rulebook Ch.7 (Armor and Encumbrance for Other Base Speeds)
	 * @param baseSpeed the base speed at which the player runs
	 * @return the modified speed, based on the player's encumbrance value
	 */
	public abstract int getSpeed(int baseSpeed);

	/**
	 * Calculates the maximum weight a player at a given Encumbrance can carry without increasing their encumbrance.
	 * <p>
	 * See Core Rulebook Ch.7 (Table 7-4)
	 * @param strength the strength value of the character;
	 * @return the maximum equipment weight before increasing encumbrance
	 */
	public abstract int getCapacity(int strength);
}