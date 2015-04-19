package debop4s.data.orm.jtests.hibernate.config;

import debop4s.data.orm.hibernate.spring.OracleNamingStrategy;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.junit.Assert;
import org.junit.Test;

public class OracleNamingStrategyTest {

    private final String[] classNames = new String[] { "User", "UserLog", "GroupMember", "EventLog" };
    private final String[] tableNames = new String[] { "user", "user_log", "group_member", "event_log" };

    private final String[] propertyNames = new String[] { "username", "email", "companyName", "eventLog" };
    private final String[] columnNames = new String[] { "username", "email", "company_name", "event_log" };

    @Test
    public void improvedNaming_ClassToTableName() {
        ImprovedNamingStrategy improvedNamingStrategy = new ImprovedNamingStrategy();

        for (int i = 0; i < classNames.length; i++) {
            Assert.assertEquals(tableNames[i],
                                improvedNamingStrategy.classToTableName(classNames[i]));
        }
    }

    @Test
    public void improvedNaming_PropertyToColumnName() {
        ImprovedNamingStrategy improvedNamingStrategy = new ImprovedNamingStrategy();

        for (int i = 0; i < propertyNames.length; i++) {
            Assert.assertEquals(columnNames[i],
                                improvedNamingStrategy.propertyToColumnName(propertyNames[i]));
        }
    }

    @Test
    public void oracleNaming_ClassToTableName() {
        OracleNamingStrategy oracleNamingStrategy = new OracleNamingStrategy();

        for (int i = 0; i < classNames.length; i++) {
            Assert.assertEquals(tableNames[i].toUpperCase(),
                                oracleNamingStrategy.classToTableName(classNames[i]));
        }
    }

    @Test
    public void oracleNaming_PropertyToColumnName() {
        OracleNamingStrategy oracleNamingStrategy = new OracleNamingStrategy();

        for (int i = 0; i < propertyNames.length; i++) {
            Assert.assertEquals(columnNames[i].toUpperCase(),
                                oracleNamingStrategy.propertyToColumnName(propertyNames[i]));
        }
    }
}
