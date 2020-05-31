package me.arkorwan.srpg.generators;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import me.arkorwan.srpg.generators.constraints.AttributeConstraint;
import me.arkorwan.srpg.models.Attribute;
import me.arkorwan.utils.Common;

public class AttributesDistributor<T extends Attribute.AttrType> {

	public Map<T, Double> distribute(double average, double deviation,
			Collection<T> values, Set<AttributeConstraint<T>> constraints,
			Random rand) {

		Set<T> unassignedTypes = new HashSet<>(values);
		double totalAttr = average * unassignedTypes.size();

		Map<T, Double> attributes = new HashMap<>();

		for (AttributeConstraint<T> c : constraints) {

			T t = c.getType();
			if (unassignedTypes.contains(t)) {
				double val = rand.doubles(c.getMin(), c.getMax()).findFirst()
						.getAsDouble();
				attributes.put(t, val);
				totalAttr -= val;
				unassignedTypes.remove(t);
			}
		}

		int unassigneds = unassignedTypes.size();
		double avgAttr = totalAttr / unassigneds;
		double dev = deviation * 2 * avgAttr;

		Iterator<Double> stats = Common.randomDoublePartition(unassigneds,
				totalAttr / unassigneds, dev, rand).iterator();
		for (T t : unassignedTypes) {
			attributes.put(t, stats.next());
		}

		return attributes;
	}

}
