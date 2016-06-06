package fr.ekinci.corbawrapper;

/**
 * Corba wrapper for exceptions
 *
 * @author Gokan EKINCI
 */
public class CORBAException extends Exception {
	public CORBAException(Throwable throwable){
		super(throwable);
	}
}
