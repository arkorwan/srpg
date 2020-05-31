package me.arkorwan.srpg.generators.constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class QuotaConstraint<T> implements Constraint {

	public static class Quota<T> {

		Set<T> q = new HashSet<>();
		Map<T, Integer> items;

		Quota() {
			items = Collections.emptyMap();
		}

		Quota(Map<T, Integer> items) {
			this.items = new HashMap<>(items);
			for (T item : this.items.keySet()) {
				if (this.items.get(item) > 0) {
					q.add(item);
				}
			}
		}

		public T getRandomItem(Random rand, T[] fullItems, boolean isPeeking) {
			if (q.isEmpty()) {

				List<T> candidates = new ArrayList<>();
				for (T c : fullItems) {
					if (!items.containsKey(c)) {
						candidates.add(c);
					}
				}

				return candidates.get(rand.nextInt(candidates.size()));
			} else {
				T selected = null;
				Set<T> fullSet = new HashSet<>();
				fullSet.addAll(Arrays.asList(fullItems));
				Iterator<T> it = q.iterator();
				while (selected == null && it.hasNext()) {
					T n = it.next();
					if (fullSet.contains(n)) {
						selected = n;
					}
				}

				if (!isPeeking) {
					int newQuota = items.get(selected) - 1;
					items.put(selected, newQuota);
					if (newQuota == 0) {
						q.remove(selected);
					}
				}

				return selected;
			}
		}

		public T getRandomItem(Random rand, T[] fullItems) {
			return getRandomItem(rand, fullItems, false);
		}
	}

	Map<T, Integer> items;

	public QuotaConstraint(Map<T, Integer> items) {
		this.items = items;
	}

	public static <T> Quota<T> createFreshQuota(QuotaConstraint<T> qc) {
		return qc == null ? new Quota<T>() : new Quota<T>(qc.items);
	}

}
