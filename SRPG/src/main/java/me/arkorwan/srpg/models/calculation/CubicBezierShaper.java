package me.arkorwan.srpg.models.calculation;

/**
 * Adapted from http://www.flong.com/texts/code/shapers_bez/
 * 
 * @author wi
 *
 */
public class CubicBezierShaper extends AbstractShaper {

	private double A, B, C, D, E, F, G, H;

	public CubicBezierShaper(double a, double b, double c, double d) {
		double y0a = 0.0;
		double x0a = 0.0;
		double y1a = b;
		double x1a = a;
		double y2a = d;
		double x2a = c;
		double y3a = 1.0;
		double x3a = 1.0;

		A = x3a - 3 * x2a + 3 * x1a - x0a;
		B = 3 * x2a - 6 * x1a + 3 * x0a;
		C = 3 * x1a - 3 * x0a;
		D = x0a;

		E = y3a - 3 * y2a + 3 * y1a - y0a;
		F = 3 * y2a - 6 * y1a + 3 * y0a;
		G = 3 * y1a - 3 * y0a;
		H = y0a;
	}

	// Helper functions:
	double slopeFromT(double t) {
		return 1.0 / (3.0 * A * t * t + 2.0 * B * t + C);
	}

	double xFromT(double t) {
		return A * (t * t * t) + B * (t * t) + C * t + D;
	}

	double yFromT(double t) {
		return E * (t * t * t) + F * (t * t) + G * t + H;
	}

	@Override
	public double apply(double x) {
		if (x == 0) {
			return 0;
		} else if (x == 1) {
			return 1;
		}
		// Solve for t given x (using Newton-Raphelson), then solve for y given
		// t.
		// Assume for the first guess that t = x.
		double currentt = x;
		int nRefinementIterations = 5;
		for (int i = 0; i < nRefinementIterations; i++) {
			double currentx = xFromT(currentt);
			double currentslope = slopeFromT(currentt);
			currentt -= (currentx - x) * (currentslope);
			if (currentt < 0) {
				currentt = 0;
			} else if (currentt > 1) {
				currentt = 1;
			}
		}

		double y = yFromT(currentt);
		return y;
	}

}
