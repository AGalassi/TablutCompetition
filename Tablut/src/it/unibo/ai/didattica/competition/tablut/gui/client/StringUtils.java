package it.unibo.ai.didattica.competition.tablut.gui.client;

import it.unibo.ai.didattica.competition.tablut.domain.State;

/**
 * Utility class for Strings.
 * 
 * @author Michele Righi
 * (<a href="https://github.com/mikyll">GitHub</a>,
 * <a href="https://www.linkedin.com/in/michele-righi/">LinkedIn</a>)
 */
public class StringUtils {
    public static String capitalize(String s) {
    	return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
    
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    public static int parseInteger(String s) {
    	try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return 60;
        }
    }
    
    public static State.Turn parseSide(String side) {
    	if(side.equalsIgnoreCase(State.Turn.BLACK.name()))
    		return State.Turn.BLACK;
    	return State.Turn.WHITE;
    }
}
