import exceptions.IllegalIntervalBounds;
import exceptions.WrongIntervalElement;

/** Helper class for Histogram.
 *
 */
public class Interval{
    private double floor; // lower boundary of interval
    private double ceil; // upper boundary
    private int count; // Number of values in interval
    private boolean isFirst; // define whether [,] or (,] interval
    private int onLeft; // if isFirst number of values on left boundary

    /** Getter for onLeft.
     *
     * @return number of values on left boundary.
     */
    public int getOnLeft() {
        return onLeft;
    }

    /** Constructor for Interval.
     *
     * @param floor lower boundary.
     * @param ceil upper boundary.
     */
    Interval(double floor, double ceil) {
        if (floor>=ceil){
            throw new IllegalIntervalBounds("Invalid interval boundary. 'ceil' should be bigger than 'floor'.");
        }
        this.floor = floor;
        this.ceil = ceil;
        this.count = 0;
        this.isFirst = false;
    }

    /** Change interval type from (,] to [,].
     *
     */
    void makeFirst(){
        this.isFirst = true;
    }

    /** Change interval type from [,] to (,].
     *
     */
    void undoFirst() {
        this.isFirst = false;
        this.count -= this.onLeft;
        this.onLeft = 0;
    }

    /** Size of interval.
     *
     * @return size.
     */
    double getSize() {
        return this.ceil-this.floor;
    }

    /** Check interval type.
     *
     * @return true if [,]; false (,].
     */
    boolean isFirst(){
        return this.isFirst;
    }

    /** Getter for floor.
     *
     * @return lower boundary of interval.
     */
    public double getFloor() {
        return floor;
    }

    /** Getter for ceil.
     *
     * @return Upper boundary of interval.
     */
    public double getCeil() {
        return ceil;
    }

    /** Whether interval contains a value.
     *
     * @param value double value to check.
     * @return true if contains.
     */
    boolean contains(double value) {

        return isFirst() ? this.floor<=value && value<=this.ceil : this.floor<value && value<=this.ceil;
    }

    /** Proper way to add single element to interval
     *
     * @param value double value
     */
    void addElement(double value) {
        if (!contains(value)){
            throw new WrongIntervalElement("Illegal value for this interval.");
        }
        this.count++;
        if (value == this.floor){
            this.onLeft++;
        }
    }

    /** Add some amount of elements to interval.
     *
     * @param value amount of added elements.
     */
    void addElements(int value){
        this.count += value;
    }

    /** Getter for count.
     *
     * @return Number of values in interval
     */
    public int getCount() {
        return count;
    }

    /** Setter for count.
     *
     * @param count Number of values in interval.
     */
    void setCount(int count) {
        this.count = count;
    }

    /** Returns whether interval contains other interval.
     *
     * @param other other interval.
     * @return true if this contains other's ceil.
     */
    public boolean contains(Interval other){
        return contains(other.ceil);
    }

    /** Returns median of an interval.
     *
     * @return median.
     */
    public double getMedian(){
        return (getCeil()+getFloor())/2;
    }

    /** toString method override.
     *
     * @return String representation of interval.
     */
    @Override
    public String toString() {
        return "Interval" + (isFirst() ?"[" : "(") +
                floor +
                "; " + ceil +
                "]: " + count;
    }
}
