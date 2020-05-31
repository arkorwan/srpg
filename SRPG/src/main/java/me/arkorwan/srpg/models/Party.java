package me.arkorwan.srpg.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Party implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 56661307508637419L;
	
	private List<Unit> members = new ArrayList<>();

	public Party() {

	}

	public void addUnit(Unit u) {
		members.add(u);
	}

	public List<Unit> getMembers() {
		return Collections.unmodifiableList(members);
	}

	public boolean removeUnit(Unit u) {
		return members.remove(u);
	}

}
