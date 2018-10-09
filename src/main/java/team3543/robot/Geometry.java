package team3543.robot;

/**
 * Place for specifying all the physical characteristics of the bot, field, etc.
 *
 * Notes:
 * - All lengths should be in meters, weights in kg.  Use double types.
 * - The game specs are in feet and inches, because USA.  Use Constants.M_PER_INCH to convert
 * - Assume looking at the field from the alliance station perspective: lengths downfield, widths across.
 *
 * @author mk
 */
final public class Geometry {
	Robot robot;

	Geometry(Robot robot) {
		this.robot = robot;
	}

	/////////////////////////////////////////////////////////
	//////////////// Bot geometry
	final double wheelRadius	= in_to_m(6);						// m
	////////// Add geometry methods for the bot here (that are computed relative to component positions)


	/////////////////////////////////////////////////////////
	//////////////// Field geometry, from 2018 Game Manual
	// Should all be public static final double
	public static final double FIELD_TAPE_WIDTH				= in_to_m(2);		// m
	public static final double FIELD_AUTO_LINE_DISTANCE		= 3.05;				// m
	public static final double FIELD_LENGTH 					= 16.46; 			// m
	public static final double FIELD_WIDTH 					= 8.23;	 			// m
	public static final double FIELD_CENTERLINE				= FIELD_LENGTH / 2;	// m

	public static final double FIELD_NULL_TERRITORY_LENGTH 	= 1.83; 			// m
	public static final double FIELD_NULL_TERRITORY_WIDTH 	= 2.42;				// m

	// Field geometry functions should be public static final since they are "world" and independent of the bot

	////////////////////////////////////////////////////////////
	// Conversion functions
	/**
	 * Inches to metres
	 *
	 * @param in inches
	 * @return metres
	 */
	public static double in_to_m(double in) {
		return in * M_PER_INCH;
	}

	/**
	 * Feet to metres
	 *
	 * @param in
	 * @return
	 */
	public static double ft_to_m(double in) {
		return in * M_PER_FOOT;
	}

	public static double degrees_to_radians(double degrees) {
		return Math.toRadians(degrees);
	}

	public static double radians_to_degrees(double radians) {
		return Math.toDegrees(radians);
	}

	////////////////////////////////////////////////////////////
	// Conversion constants
	public static final double M_PER_INCH = 0.0254;
	public static final double IN_PER_FOOT = 12;
	public static final double M_PER_FOOT = M_PER_INCH * IN_PER_FOOT;

}