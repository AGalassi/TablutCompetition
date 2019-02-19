package Exceptions;

import Domain.Action;

/**
 * This exception represent an action that is moving to an occupited box
 * @author A.Piretti
 *
 */
public class OccupitedException extends Exception {

private static final long serialVersionUID = 1L;
	
	public OccupitedException(Action a)
	{
		super("Move into a box occupited form another pawn: "+a.toString());
	}

	
}
