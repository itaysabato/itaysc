package undermind.utilities;

/**
 * Created By: Itay Sabato<br/>
 * Date: 10/07/2011 <br/>
 * Time: 22:03:35 <br/>
 */
public class UndermindException extends Exception {

    public UndermindException() {
	super();
    }

    public UndermindException(String message) {
	super(message);
    }
    public UndermindException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndermindException(Throwable cause) {
        super(cause);
    }
}
