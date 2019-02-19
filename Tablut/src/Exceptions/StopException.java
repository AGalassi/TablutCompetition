package Exceptions;

import Domain.Action;

/**
 * This exception represent an action that is trying to do nothing
 * @author A.Piretti
 *
 */
public class StopException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public StopException(Action a)
	{
		super("Action not allowed, a pawn need to move: "+a.toString());
	}

}
