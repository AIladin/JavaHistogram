import exceptions.HistogramBoundariesRedefinition;
import exceptions.IllegalBinsValue;
import exceptions.IllegalIntervalBounds;
import exceptions.WrongIntervalElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HistogramTest {

    static Histogram histogram;

    @BeforeEach
    void setup(){
        histogram = new Histogram(0, 5, 5);
    }

    @Test
    void addTest(){
        histogram.addBatch(new double[]{0,1,2,3,3,4,5}, Histogram.AppendType.ADD_TO_EDGE);
        histogram.addBatch(new int[]{0,1,2,3,3,4,5}, Histogram.AppendType.ADD_TO_EDGE);
        histogram.addBatch(new float[]{0,1,2,3,3,4,5}, Histogram.AppendType.ADD_TO_EDGE);
        System.out.println(histogram);
    }

    @Test
    void AddToEdgeTest(){
        //add numbers to right edge.
        histogram.addBatch("resources/1_to_100.txt", Histogram.AppendType.ADD_TO_EDGE);
        System.out.println(histogram);
        setup();
        //add numbers to left edge.
        histogram.addBatch("resources/minus_100_to_1.txt", Histogram.AppendType.ADD_TO_EDGE);
        System.out.println(histogram);
    }

    @Test
    void ExpandTest(){
        //add numbers to right edge.
        histogram.addBatch("resources/1_to_100.txt", Histogram.AppendType.EXPAND);
        System.out.println(histogram);
        setup();
        //add numbers to left edge.
        histogram.addBatch("resources/minus_100_to_1.txt", Histogram.AppendType.EXPAND);
        System.out.println(histogram);
    }

    @Test
    void IntervalOnlyTest(){
        //add numbers to right edge.
        histogram.addBatch("resources/1_to_100.txt", Histogram.AppendType.INTERVAL_ONLY);
        System.out.println(histogram);
        setup();
        //add numbers to left edge.
        histogram.addBatch("resources/minus_100_to_1.txt", Histogram.AppendType.INTERVAL_ONLY);
        System.out.println(histogram);

    }

    @Test
    void ExceptionsTest(){
        histogram.addBatch(new double[]{0,1,2,3,3,4,5}, Histogram.AppendType.ADD_TO_EDGE);

        //setMinH exception
        Assertions.assertThrows(HistogramBoundariesRedefinition.class, () -> histogram.setMinH(2));
        //setMax exception
        Assertions.assertThrows(HistogramBoundariesRedefinition.class, () -> histogram.setMaxH(2));

        //IllegalBinsValue
        Assertions.assertThrows(IllegalBinsValue.class, () -> histogram.setBins(10));

        //Illegal interval bounds
        Assertions.assertThrows(IllegalIntervalBounds.class, () -> new Interval(2,1));
        Assertions.assertThrows(IllegalIntervalBounds.class, () -> new Interval(1,1));

        //WrongIntervalElement
        Assertions.assertThrows(WrongIntervalElement.class, () -> new Interval(1,2).addElement(5));
    }

    @Test
    void describeTest(){
        histogram.addBatch("resources/random.txt", Histogram.AppendType.EXPAND);
        System.out.println(histogram);
        System.out.println(histogram.describe());
    }

    @Test
    void resizeTest(){
        histogram = new Histogram(0,6,6);
        histogram.addBatch(new double[] {1,1,2,3,5}, Histogram.AppendType.INTERVAL_ONLY);
        System.out.println(histogram);
        histogram.setBins(3);
        System.out.println(histogram);
    }

}