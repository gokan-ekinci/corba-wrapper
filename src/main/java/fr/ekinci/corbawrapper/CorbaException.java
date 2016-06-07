package fr.ekinci.corbawrapper;

/**
 * Corba wrapper for exceptions
 *
 * @author Gokan EKINCI
 */
public class CorbaException extends Exception {
	public CorbaException(Throwable throwable){
		super(throwable);
	}
}
