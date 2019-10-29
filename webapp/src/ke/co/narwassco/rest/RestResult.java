package ke.co.narwassco.rest;

/**
 * RestResult Object
 * @version 1.00
 * @author Igarashi
 */
public class RestResult<T> {

	public static int ok = 0;
	public static int error = -1;
	public static int systemerror = 99;

	private int code;
	private String message;
	private T value;

	/**
	 * Constructor
	 * @param code
	 * @param message
	 */
	public RestResult(int code, String message){
		this.code = code;
		this.message = message;
	}

	/**
	 * Constructor
	 * @param value Target Class
	 */
	public RestResult(T value){
		this.code = RestResult.ok;
		this.value = value;
	}

	/**
	 * get Code of result
	 * @return {Integer}
	 */
	public int getCode(){
		return this.code;
	}

	/**
	 * get Message of result
	 * @return {String}
	 */
	public String getMessage(){
		return this.message;
	}

	/**
	 * get Object of result
	 * @return {Object}
	 */
	public T getValue(){
		return this.value;
	}

}
