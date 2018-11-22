package team3543.robot;

public class Utils {

	/**
	 * Clips a value between the min and max
	 *
	 * @param value
	 * @param min
	 * @param max
	 * @return clipped value
	 */
	public static double clip(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	/**
	 * Clips a value between -1 and 1
	 *
	 * @param value
	 * @return clipped value
	 */
	public static double clip(double value) {
		return clip(value, -1, 1);
	}

}
