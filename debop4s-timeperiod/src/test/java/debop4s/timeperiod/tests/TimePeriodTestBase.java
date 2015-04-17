package debop4s.timeperiod.tests;

import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

/**
 * kr.hconnect.timeperiod.test.TimePeriodTestBase
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 17. 오후 2:08
 */
@Slf4j
public class TimePeriodTestBase {

//    @Rule
//    public TestRule benchmarkRun = new BenchmarkRule();

    public static final DateTime testDate = new DateTime(2000, 10, 2, 13, 45, 53, 673);
    public static final DateTime testDiffDate = new DateTime(2002, 9, 3, 7, 14, 22, 234);
    public static final DateTime testNow = Times.now();
}
