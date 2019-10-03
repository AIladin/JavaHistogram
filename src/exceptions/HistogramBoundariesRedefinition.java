package exceptions;

/** Exception to prevent data loss from filled histogram.
 *
 */
public class HistogramBoundariesRedefinition extends IllegalArgumentException{
    public HistogramBoundariesRedefinition(String message) {
        super(message);
    }
}
