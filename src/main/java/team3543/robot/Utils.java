package team3543.robot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    static ObjectMapper objectMapper = new ObjectMapper();

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


	/**
	 * Get the nth element of an array, or return the default if no value there
	 */
	public static double arrGet(double [] arr, int pos, double defaultValue) {
		if (arr.length <= pos) {
			return defaultValue;
		}
		else {
			return arr[pos];
		}
	}

	/**
	 * Returns true for non-zero (using 0.0001 as tolerance)
	 */
	public static boolean asBool(double val) {
		return Math.abs(0.0 - val) < 0.00001 ? false : true;
	}

	public static double forBool(boolean b) {
		return b ? 1.0 : 0.0;
	}

	/**
	 * Returns the serialized state as UTF8-encoded string
	 */
	public static String toJSON(Robot.RecordedRobotState o) {
		try {
			return objectMapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static Robot.RecordedRobotState fromJSON(String s) {
		try {
			return objectMapper.readValue(s, Robot.RecordedRobotState.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e); // should not happen
		} catch (IOException e) {
			throw new RuntimeException(e); // also should not happen
		}
	}
}
