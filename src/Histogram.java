import exceptions.HistogramBoundariesRedefinition;
import exceptions.IllegalBinsValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/** Class for storing and evaluating statistical information about data.
 *
 */
public class Histogram {

    /** Append type for new value.
     *
     */
    public enum AppendType{
        ADD_TO_EDGE, // Add value to the edge of histogram if value is not in histogram.
        EXPAND,    // Adds bins to the histogram in order to add value.
        INTERVAL_ONLY, // Values from interval only added.
    }

    private double minH; // Lower histogram boundary.
    private double maxH; // Upper histogram boundary.
    private int bins; // Number of bins in histogram.
    private ArrayList<Interval> intervals; // Array of intervals.

    /** Constructor for Histogram.
     *
     * @param minH Lower histogram boundary.
     * @param maxH histogram boundary.
     * @param bins umber of bins in histogram.
     */
    public Histogram(double minH, double maxH, int bins) {
        this.minH = minH;
        this.maxH = maxH;
        this.bins = bins;
        if (bins<=0){
            throw new IllegalBinsValue("Can not initialize Histogram with negative or 0 'bins'");
        }
        generateIntervals();
    }

    /** Generate 'bins' intervals from 'minH' to 'maxH'
     *
     */
    private void generateIntervals(){
        this.intervals = new ArrayList<>();
        for (int i=0; i<this.bins; i++) {
            this.intervals.add(new Interval(this.minH + (this.maxH - this.minH) / this.bins * i,
                                             this.minH + (this.maxH - this.minH) / this.bins * (i+1)));
        }
        this.intervals.get(0).makeFirst();
    }

    /** Add intervals until it contains value.
     *
     * @param addValue adding value.
     * @param toLeft add interval to left or to right.
     */
    private void addIntervals(double addValue, boolean toLeft){
        double step = this.intervals.get(0).getSize();
        Interval newI;
        if (toLeft){
            do {
                newI = new Interval(this.intervals.get(0).getFloor() - step, this.intervals.get(0).getFloor());
                newI.makeFirst();
                newI.setCount(this.intervals.get(0).getOnLeft());
                this.intervals.get(0).undoFirst();
                this.intervals.add(0, newI);
                this.bins++;
            } while (!newI.contains(addValue));
            this.minH = this.intervals.get(0).getFloor();
        } else {
            do {
                newI = new Interval(this.intervals.get(this.bins-1).getCeil(),
                                        this.intervals.get(this.bins-1).getCeil() + step);
                this.intervals.add(newI);
                this.bins++;
            } while (!newI.contains(addValue));
            this.maxH = this.intervals.get(this.bins-1).getCeil();
        }
    }

    /** Changes number of bins and saves Histogram structure.
     *
     */
    private void updateIntervals(){
        Queue<Interval> oldIntervals = new LinkedList<>(this.intervals);
        generateIntervals();
        Queue <Interval> newIntervals = new LinkedList<>(this.intervals);

        Interval biggerInt;
        Interval smallerInt;
        if (oldIntervals.size()>newIntervals.size()){
            while (!newIntervals.isEmpty()) {
                biggerInt = newIntervals.remove();
                while (!oldIntervals.isEmpty()) {
                    smallerInt = oldIntervals.peek();
                    if (biggerInt.contains(smallerInt)){
                        oldIntervals.remove();
                        biggerInt.addElements(smallerInt.getCount());
                    } else {
                        break;
                    }
                }
            }
        } else {
            throw new IllegalBinsValue("Can not reduce interval size after adding values.");
        }
    }

    /** Add single number to Histogram
     *
     * @param newValue value.
     * @param appendType
     *         ADD_TO_EDGE, // Add value to the edge of histogram if value is not in histogram.
     *         EXPAND,    // Adds bins to the histogram in order to add value.
     *         INTERVAL_ONLY, // Values from interval only added.
     */
    public void addNumber(double newValue, AppendType appendType){

        switch (appendType) {

            case ADD_TO_EDGE:
                if (newValue<getMinH()) {
                    this.intervals.get(0).addElement(this.intervals.get(0).getFloor());
                } else if (newValue>getMaxH()){
                    this.intervals.get(this.bins - 1).addElement(this.intervals.get(this.bins - 1).getCeil());
                }
                break;

            case EXPAND:
                if (newValue<getMinH()) {
                    addIntervals(newValue, true);
                } else if (newValue>getMaxH()){
                    addIntervals(newValue, false);
                }
                break;
            case INTERVAL_ONLY:
                break;
        }
        if (newValue<=this.maxH || newValue >= this.minH){
            for (int i=0; i<getBins(); i++){
                if (this.intervals.get(i).contains(newValue)) {
                    this.intervals.get(i).addElement(newValue);
                    break;
                }
            }
        }
    }

    /** Add array of values to histogram.
     *
     * @param data array.
     * @param appendType
     *              ADD_TO_EDGE, // Add value to the edge of histogram if value is not in histogram.
     *              EXPAND,    // Adds bins to the histogram in order to add value.
     *              INTERVAL_ONLY, // Values from interval only added.
     */
    public void addBatch(double[] data, AppendType appendType){
        for (double num: data){
            addNumber(num, appendType);
        }
    }

    /** Add array of values to histogram.
     *
     * @param data array.
     * @param appendType
     *              ADD_TO_EDGE, // Add value to the edge of histogram if value is not in histogram.
     *              EXPAND,    // Adds bins to the histogram in order to add value.
     *              INTERVAL_ONLY, // Values from interval only added.
     */
    public void addBatch(float[] data, AppendType appendType){
        for (float num: data){
            addNumber(num, appendType);
        }
    }

    /** Add array of values to histogram.
     *
     * @param data array.
     * @param appendType
     *              ADD_TO_EDGE, // Add value to the edge of histogram if value is not in histogram.
     *              EXPAND,    // Adds bins to the histogram in order to add value.
     *              INTERVAL_ONLY, // Values from interval only added.
     */
    public void addBatch(int[] data, AppendType appendType){
        for (int num: data){
            addNumber(num, appendType);
        }
    }

    /** Add values from file to histogram.
     *
     * @param fileName path to file
     * @param appendType
     *              ADD_TO_EDGE, // Add value to the edge of histogram if value is not in histogram.
     *              EXPAND,    // Adds bins to the histogram in order to add value.
     *              INTERVAL_ONLY, // Values from interval only added.
     */
    public void addBatch(String fileName, AppendType appendType){
        File file = new File(fileName);

        try (Scanner scanner = new Scanner(file)) {
            double val;
            while(scanner.hasNextDouble()) {
                val = scanner.nextDouble();
                addNumber(val, appendType);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Getter for minH.
     *
     * @return Lower boundary of histogram.
     */
    public double getMinH() {
        return minH;
    }

    /** Setter for minH.
     *
     * @param minH Lower boundary of histogram.
     */
    public void setMinH(double minH) {
        if (count()>0) {
            throw  new HistogramBoundariesRedefinition("Can not change histogram size after added values.");
        }
        this.minH = minH;
    }

    /** Getter for maxH.
     *
     * @return Upper boundary of histogram.
     */
    public double getMaxH() {
        return maxH;
    }

    /** Setter for maxH.
     *
     * @param maxH Upper boundary of histogram.
     */
    public void setMaxH(double maxH) {
        if (count()>0) throw new HistogramBoundariesRedefinition("Can not change histogram size after adding values.");
        this.maxH = maxH;

    }

    /** Getter for bins.
     *
     * @return number of bins.
     */
    public int getBins() {
        return bins;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Histogram:\n");
        for (Interval interval: this.intervals){
            sb.append(' ');
            sb.append(interval);
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Setter for bins.
     *
     * @param bins new number of bins.
     */
    public void setBins(int bins) {
        if (bins <= 0) {
            throw new IllegalBinsValue("Can not initialize Histogram with negative or 0 'bins'");
        } else if (count()!=0 && this.bins<bins) {
            throw new IllegalBinsValue("Can not reduce interval size after adding values.");
        } else if (count() == 0){
            this.bins = bins;
            generateIntervals();
        } else {
            this.bins = bins;
            updateIntervals();
        }
    }

    /** Count all elements in histogram.
     *
     * @return number of elements in histogram.
     */
    public int count(){
        return  this.intervals.stream().mapToInt(Interval::getCount).sum();
    }

    /** Count number of elements from interval
     *
     * @param index index of the interval.
     * @return number of elements.
     */
    public int count(int index){
        if (index>=0 && index < this.bins) {
            return this.intervals.get(index).getCount();
        } else {
            throw new IndexOutOfBoundsException("Interval index out of bounds.");
        }
    }

    /** Mean
     *
     * @return mean
     */
    public double mean(){
        return rawMoment(1);
    }

    /** Approx variational series.
     *
     * @return variational series.
     */
    public ArrayList<Double> variationalSeries(){
        ArrayList<Double> vSeries = new ArrayList<>();
        for (Interval interval: this.intervals){
            for (int c=0; c<interval.getCount(); c++) {
                vSeries.add(interval.getMedian());
            }
        }
        return vSeries;
    }

    /**
     *
     * @return Median.
     */
    public double median(){
        ArrayList<Double> vSeries= variationalSeries();
        return vSeries.size()%2==0 ?
                (vSeries.get(vSeries.size()/2)+vSeries.get((vSeries.size()+1)/2))/2:
                vSeries.get((vSeries.size()+1)/2);
    }

    /**
     *
     * @return Standard Deviation.
     */
    public double std() {
        return Math.sqrt(rawMoment(2));
    }

    /**
     *
     * @return variance.
     */
    public double variance(){
        return std()/mean();
    }

    /**
     *
     * @return skewness.
     */
    public double skewness(){
        return centralMoment(3)/Math.pow(std(),3);
    }

    /**
     *
     * @return excess.
     */
    public double excess(){
        return kurtosis()-3;
    }

    /**
     *
     * @return kurtosis = excess + 3.
     */
    public double kurtosis(){
        return centralMoment(4)/Math.pow(std(), 4);
    }

    /** Raw moment of n-th degree.
     *
     * @param n degree of moment.
     * @return Raw moment.
     */
    public double rawMoment(int n){
        assert n>0;
        double sum = 0;
        for (Double val : variationalSeries()){
            sum += Math.pow(val, n);
        }
        return 1/(double)count()*sum;
    }


    /** Central moment of n-th degree.
     *
     * @param n degree of moment.
     * @return central moment.
     */
    public double centralMoment(int n) {
        assert n > 0;
        double sum = 0;
        double meanV = mean();
        for (Double val : variationalSeries()) {
            sum += Math.pow((val - meanV), n);
        }
        return 1 / (double)count() * sum;
    }

    /** Range of histogram.
     *
     * @return range.
     */
    public double range(){
        double l = this.intervals.get(0).getFloor();
        double u = this.intervals.get(getBins()-1).getCeil();
        for (int i=0; i<getBins(); i++){
            if (this.count(i)!=0){
                l = this.intervals.get(i).getFloor();
                break;
            }
            if (this.count(getBins()-1-i)!=0){
                u = this.intervals.get(getBins()-1-i).getCeil();
                break;
            }
        }
        return u-l;

    }

    /** Median absolute deviation
     *
     * @return median absolute deviation.
     */
    public double mad(){
        //
        double sum = 0;
        double med = median();
        for (Double val : variationalSeries()) {
            sum += Math.abs(val - med);
        }
        return 1 / (double) count() * sum;

    }

    /** Statistical describe of histogram.
     *
     * @return string.
     */
    public String describe() {
        return   "Describe histogram:\n" + "Count = " +
                count() +
                "\nRange = " +
                range() +
                "\nMean = " +
                mean() +
                "\nMedian = " +
                median() +
                "\nSTD = " +
                std() +
                "\nVariance coef = " +
                variance() +
                "\nSkewness = " +
                skewness() +
                "\nExcess = " +
                excess() +
                "\nKurtosis = " +
                kurtosis() +
                "\nMedian absolute deviation = " +
                mad();
    }
}
