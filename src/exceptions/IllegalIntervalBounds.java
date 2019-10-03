package exceptions;

/** Illegal interval value. Floor can not be greater or equal than ceil.
 *
 */
public class IllegalIntervalBounds extends IllegalArgumentException {
    public IllegalIntervalBounds(String message) {
        super(message);
    }
}
