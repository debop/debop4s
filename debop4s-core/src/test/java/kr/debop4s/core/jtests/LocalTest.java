package kr.debop4s.core.jtests;

import kr.debop4s.core.AbstractValueObject;
import kr.debop4s.core.Local;
import kr.debop4s.core.testing.Testing;
import kr.debop4s.core.utils.Hashs;
import kr.debop4s.core.utils.ToStringHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.scalatest.junit.JUnitSuite;

import java.util.UUID;
import java.util.concurrent.Callable;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class LocalTest extends JUnitSuite {

    @Before
    public void before() {
        Local.clear();
    }

    @Test
    public void multiThread() throws Exception {
        Testing.run(15, new Runnable() {
            @Override
            public void run() {
                try {
                    saveAndLoadValueType();
                    saveAndLoadReferenceType();
                    getOrCreate();
                } catch (Exception e) {
                    log.error("예외 발생", e);
                }
            }
        });
    }

    @Test
    public void saveAndLoadValueType() {
        final String key = "Local.Value.Key.Java";
        final String value = UUID.randomUUID().toString();
        Local.put(key, value);
        assertThat(Local.get(key)).isEqualTo(value);
    }

    @Test
    public void saveAndLoadReferenceType() throws Exception {
        final String key = "Local.Reference.Key.Java";
        final User user = new User("user", "P" + Thread.currentThread().getId(), 1);
        Local.put(key, user);

        Thread.sleep(5);

        User storedUser = Local.get(key, User.class);

        assertThat(storedUser).isNotNull();
        assertThat(storedUser).isEqualTo(user);
        assertThat(storedUser.name).isEqualTo(user.name);
    }

    @Test
    public void getOrCreate() throws Exception {
        String key = "Local.GetOrCreate.Key.Java";
        User user = Local.getOrCreate(key, new Callable<User>() {
            @Override
            public User call() throws Exception {
                return new User("user", "P" + Thread.currentThread().getId(), 1);
            }
        });

        Thread.sleep(5);

        User storedUser = Local.get(key, User.class);
        assertThat(storedUser).isNotNull();
        assertThat(storedUser).isEqualTo(user);
        assertThat(storedUser.name).isEqualTo(user.name);
    }

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

        @Override
        public int hashCode() {
            return Hashs.compute(name, password, age);
        }

        @Override
        public ToStringHelper buildStringHelper() {
            return super.buildStringHelper()
                    .add("name", name)
                    .add("password", password)
                    .add("age", age);
        }

        private static final long serialVersionUID = 2697543433170138506L;
    }
}
