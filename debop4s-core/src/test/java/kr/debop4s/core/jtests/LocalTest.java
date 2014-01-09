package kr.debop4s.core.jtests;

import kr.debop4s.core.AbstractValueObject;
import kr.debop4s.core.Local;
import kr.debop4s.core.testing.Testing;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

/**
 * kr.debop4s.core.jtests.LocalTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오전 9:49
 */
@Slf4j
public class LocalTest {

    @Getter
    @Setter
    static class User extends AbstractValueObject {
        private String name;
        private String password;
        private int age;

        public User(String name, String password, int age) {
            this.name = name;
            this.password = password;
            this.age = age;
        }

        private static final long serialVersionUID = 2697543433170138506L;
    }

    @Test
    public void multiThread() throws Exception {
        Testing.run(15, new Runnable() {
            @Override
            public void run() {
                saveAndLoadValueType();
            }
        });
    }

    @Test
    public void saveAndLoadValueType() {
        final String key = "Local.Value.Key";
        final String value = UUID.randomUUID().toString();
        Local.put(key, value);
        assertThat(Local.get(key)).isEqualTo(value);
    }
}
