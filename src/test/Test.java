
import org.junit.Assert;

import java.util.Calendar;

public class Test {

    @org.junit.Test
    public void calculate() {
        FileExt fileExt = new FileExt(Calendar.getInstance().getTimeInMillis(), "e:\\JAExp\\test\\test1", "test1");

        fileExt.calculate("cat");
        Assert.assertEquals(100, fileExt.getCurrentQuality().intValue());

        fileExt.calculate("dog");
        Assert.assertEquals(100, fileExt.getCurrentQuality().intValue());
    }


}
