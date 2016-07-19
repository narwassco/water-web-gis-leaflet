package ke.co.narwassco.rest;

public class RestResult<T> {

	public static int ok = 0;
	public static int error = -1;
	public static int systemerror = 99;

	private int code;
	private String message;
	private T value;

	public RestResult(int code, String message){
		this.code = code;
		this.message = message;
	}

	public RestResult(T value){
		this.code = RestResult.ok;
		this.value = value;
	}

	public int getCode(){
		return this.code;
	}

	public String getMessage(){
		return this.message;
	}

	public T getValue(){
		return this.value;
	}

}
