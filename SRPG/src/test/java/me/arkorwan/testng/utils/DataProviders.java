package me.arkorwan.testng.utils;

/**
 * Collection of utility methods for testng dataproviders manipulation
 * 
 * @author arkorwan
 *
 */
public class DataProviders {

	private DataProviders() {
	}

	public static final Object[][] EMPTY_PROVIDER = new Object[0][0];

	/**
	 * Merge two given providers.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Object[][] merge(Object[][] a, Object[][] b) {

		Object[][] c = new Object[a.length + b.length][Math.max(width(a),
				width(b))];
		for (int i = 0; i < a.length; i++) {
			System.arraycopy(a[i], 0, c[i], 0, a[i].length);
		}
		for (int i = 0; i < b.length; i++) {
			System.arraycopy(b[i], 0, c[a.length + i], 0, b[i].length);
		}
		return c;
	}

	/**
	 * Cartesian product of the two given providers.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Object[][] cross(Object[][] a, Object[][] b) {
		if ((a = ensure(a)) != EMPTY_PROVIDER
				&& (b = ensure(b)) != EMPTY_PROVIDER) {
			Object[][] c = new Object[a.length * b.length][width(a) + width(b)];
			for (int i = 0; i < a.length; i++) {
				for (int j = 0; j < b.length; j++) {
					System.arraycopy(a[i], 0, c[i * b.length + j], 0,
							a[i].length);
					System.arraycopy(b[j], 0, c[i * b.length + j], a[i].length,
							b[j].length);
				}
			}
			return c;
		} else {
			return EMPTY_PROVIDER;
		}
	}

	/**
	 * Turn 1-dimensional array of n items into a provider with length n, width
	 * 1.
	 * 
	 * @param a
	 * @return
	 */
	public static Object[][] wrap(Object... a) {

		if (a == null || a.length == 0) {
			return EMPTY_PROVIDER;
		}
		Object[][] c = new Object[a.length][1];
		for (int i = 0; i < a.length; i++) {
			c[i] = new Object[] { a[i] };
		}
		return c;

	}

	/**
	 * Reverse the wrapping process
	 * 
	 * @param providers
	 * @return
	 */
	public static Object[] unwrap(Object[][] providers) {
		Object[] result = new Object[providers.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = providers[i][0];
		}
		return result;
	}

	private static Object[][] ensure(Object[][] a) {
		return (a == null || a.length == 0) ? EMPTY_PROVIDER : a;
	}

	private static int width(Object[][] a) {
		return (a == null || a.length == 0) ? 0 : a[0].length;
	}

}
