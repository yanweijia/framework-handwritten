import org.junit.Test;

import java.util.concurrent.atomic.LongAdder;

public class LongAdderTest {
    @Test
    public void test(){
        LongAdder adder = new LongAdder();
        adder.increment();
        System.out.println(adder.longValue());
    }

    @Test
    public void testComments(){
        String name = "weijia";
        // \u000d name="yanweijia";
        System.out.println(name);
    }
}
