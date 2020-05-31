package me.arkorwan.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 
 * Modified from http://andrdev.blogspot.co.uk/search/label/Name%20Generator
 * 
 * Raw-type warnings fixed.
 *
 */
public class NameGenerator {

	private List<String> vocals = new ArrayList<>();
	private List<String> startConsonants = new ArrayList<>();
	private List<String> endConsonants = new ArrayList<>();
	private List<String> nameInstructions = new ArrayList<>();
	private Random rand;

	public NameGenerator(Random rand) {
		this.rand = rand;
		String demoVocals[] = { "a", "e", "i", "o", "u", "ee", "ai", "ou", "y",
				"oo" };

		String demoStartConsonants[] = { "b", "c", "d", "f", "g", "h", "j", "k",
				"l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "z",
				"cl", "cr", "ch", "chr", "bl", "br", "dr", "dh", "dw", "fl",
				"fr", "gl", "gr", "gh", "kl", "kw", "kr", "ph", "pl", "pr",
				"st", "str", "sh", "sch", "shr", "th", "thr", "tr", "wr",
				"wh" };

		String demoEndConsonants[] = { "b", "d", "f", "g", "h", "k", "l", "m",
				"n", "p", "r", "s", "t", "v", "w", "z", "ch", "gh", "nn", "st",
				"sh", "th", "tt", "ss", "ng", "nt", "ff", "ft", "zz", "nth" };

		String nameInstructions[] = { "vd", "vdv", "cvdvd", "cvd", "vdvd",
				"cvdv" };

		this.vocals.addAll(Arrays.asList(demoVocals));
		this.startConsonants.addAll(Arrays.asList(demoStartConsonants));
		this.endConsonants.addAll(Arrays.asList(demoEndConsonants));
		this.nameInstructions.addAll(Arrays.asList(nameInstructions));
	}

	public String getName() {
		return firstCharUppercase(
				getNameByInstructions(getRandomElementFrom(nameInstructions)));
	}

	private String getNameByInstructions(String nameInstructions) {
		String name = "";
		int l = nameInstructions.length();

		for (int i = 0; i < l; i++) {
			char x = nameInstructions.charAt(0);
			switch (x) {
			case 'v':
				name += getRandomElementFrom(vocals);
				break;
			case 'c':
				name += getRandomElementFrom(startConsonants);
				break;
			case 'd':
				name += getRandomElementFrom(endConsonants);
				break;
			}
			nameInstructions = nameInstructions.substring(1);
		}
		return name;
	}

	private String firstCharUppercase(String name) {
		return Character.toString(name.charAt(0)).toUpperCase()
				+ name.substring(1);
	}

	private String getRandomElementFrom(List<String> v) {
		return v.get(rand.nextInt(v.size()));
	}
}