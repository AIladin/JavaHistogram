package exceptions;

/** Exception to prevent adding wrong value to interval.
 *
 */
public class WrongIntervalElement extends IllegalArgumentException {
    public WrongIntervalElement(String message) {
        super(message);
    }
}
