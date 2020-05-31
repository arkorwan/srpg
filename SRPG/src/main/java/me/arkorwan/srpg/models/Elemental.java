package me.arkorwan.srpg.models;

/**
 * Inherent types of magic.
 * 
 * @author arkorwan
 *
 */
public enum Elemental {

	None(0), Holy(-1), Fire(1), Water(2), Nature(3);

	int id;

	Elemental(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @param other
	 * @return positive if this elemental has advantage, negative if
	 *         disadvantage, 0 otherwise
	 */
	public int advantage(Elemental other) {

		return (this.isBlackMagic() && other.isBlackMagic())
				? (4 + this.id - other.id) % 3 - 1 : 0;
	}

	public boolean isBlackMagic() {
		return id > 0;
	}

	static Elemental[] blacks = { Elemental.Fire, Elemental.Water,
			Elemental.Nature };

	public static Elemental[] blackElementals() {
		return blacks;
	}
}
