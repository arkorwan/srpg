package me.arkorwan.srpg.models.calculation;

public class CurveMatcher {

	public static void main(String[] args) {

		Shaper s = ShaperCollection.getShaper("logistic", 10);

		int grain = 1000;

		double[] ys = new double[grain];

		for (int i = 0; i < grain; i++) {
			double x = i * 1.0 / grain;
			ys[i] = s.apply(x);

			// System.out.println(ys[i]);
		}

		double le = 1.0;

		for (int i1 = 425; i1 <= 435; i1++) {
			double x1 = i1 * 1.0 / grain;
			for (int j1 = 195; j1 <= 205; j1++) {
				double y1 = j1 * 1.0 / grain;
				for (int i2 = 565; i2 <= 575; i2++) {
					double x2 = i2 * 1.0 / grain;
					for (int j2 = 798; j2 <= 805; j2++) {
						double y2 = j2 * 1.0 / grain;

						double e = error(x1, y1, x2, y2, ys);
						if (e < le) {
							System.out.println(
									String.format("... %.3f, %.3f, %.3f, %.3f, %f",
											x1, y1, x2, y2, e));
							le = e;
						}
					}
				}
			}
		}

	}

	static double error(double x1, double y1, double x2, double y2,
			double[] ys) {

		CubicBezierShaper est = new CubicBezierShaper(x1, y1, x2, y2);

		double ses = 0;

		for (int i = 0; i < ys.length; i++) {
			double x = i * 1.0 / (ys.length);
			double y = est.apply(x);
			double e = y - ys[i];
			double se = e * e;
			ses += se;
			// System.out.println(y);
		}

		return ses / ys.length;

	}

}
