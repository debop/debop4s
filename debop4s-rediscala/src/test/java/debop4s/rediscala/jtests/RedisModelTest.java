package debop4s.rediscala.jtests;

import debop4s.rediscala.MemberScore;
import org.junit.Test;
import scala.Tuple2;

import static org.fest.assertions.Assertions.assertThat;

/**
 * RedisModelTest
 *
 * @author debop created at 2014. 4. 29.
 */
public class RedisModelTest {

    @Test
    public void tupleTest() throws Exception {
        MemberScore ms = new MemberScore("member", 123.0D);
        Tuple2<String, Object> tuple = ms.tuple();

        assertThat(tuple._1()).isEqualTo("member");
        assertThat(tuple._2()).isEqualTo(123.0D);
    }
}
