package team3543.robot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;

public class RobotScript extends ArrayList<Robot.State> {

    static ObjectMapper objectMapper = new ObjectMapper();

    public static final long serialVersionUID = 1L;
    public static final RobotScript EMPTY = new RobotScript();

    /**
     * Returns the serialized state as UTF8-encoded string
     */
    public String toJSON() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static RobotScript fromJSON(String s) {
        try {
            return objectMapper.readValue(s, RobotScript.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // should not happen
        } catch (IOException e) {
            throw new RuntimeException(e); // also should not happen
        }
    }
}
