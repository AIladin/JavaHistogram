package exceptions;

/** Illegal bins value.
 *
 *  Exception to prevent data loss from filled histogram.
 *
 */
public class IllegalBinsValue extends IllegalArgumentException {
    public IllegalBinsValue(String message) {
        super(message);
    }
}
