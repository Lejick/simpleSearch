
import org.junit.Assert;

import java.util.Calendar;

public class Test {

    @org.junit.Test
    public void calculate() {
        FileExt fileExt = new FileExt(Calendar.getInstance().getTimeInMillis(), "f:\\simpleSearch\\resources\\test1", "test1");

        fileExt.calculate("cat");
        Assert.assertEquals(100, fileExt.getCurrentQuality().intValue());

        fileExt.calculate("dog");
        Assert.assertEquals(100, fileExt.getCurrentQuality().intValue());

        fileExt.calculate("dog", "cat");
        Assert.assertEquals(100, fileExt.getCurrentQuality().intValue());

        fileExt.calculate("dog", "cat", "rabbit");
        Assert.assertEquals(66.66666, fileExt.getCurrentQuality().floatValue(), 0.001);

        fileExt.calculate("dog", "cat", "cat", "cat", "dog");
        Assert.assertEquals(100, fileExt.getCurrentQuality().intValue());

        fileExt.calculate("dog", "cat", "cat", "cat", "dog", "rabbit");
        Assert.assertEquals(66.66666, fileExt.getCurrentQuality().floatValue(), 0.001);

    }

    @org.junit.Test
    public void calculateAdvPerfect() {
        FileExt fileExt = new FileExt(Calendar.getInstance().getTimeInMillis(), "f:\\simpleSearch\\resources\\test2", "test2");
        fileExt.relevantCalculate("my", "cat", "is", "fat");
        Assert.assertEquals(100, fileExt.getCurrentQuality().intValue());

    }

    @org.junit.Test
    public void calculateAdvNoPerfect() {
        FileExt fileExt = new FileExt(Calendar.getInstance().getTimeInMillis(), "f:\\simpleSearch\\resources\\test3", "test3");
        fileExt.relevantCalculate("my", "cat", "is", "fat");
        Assert.assertEquals(100, fileExt.getCurrentQuality().intValue());

    }

}
