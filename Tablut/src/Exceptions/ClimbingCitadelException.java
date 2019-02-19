package Exceptions;

import Domain.Action;

public class ClimbingCitadelException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ClimbingCitadelException(Action a)
	{
		super("A pawn is tryng to climb over a citadel: "+a.toString());
	}

}
