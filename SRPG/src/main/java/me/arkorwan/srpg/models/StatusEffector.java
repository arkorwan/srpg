package me.arkorwan.srpg.models;

import java.util.Map;

public interface StatusEffector {

	Map<BasicAttribute.Type, Integer> effects();
}
