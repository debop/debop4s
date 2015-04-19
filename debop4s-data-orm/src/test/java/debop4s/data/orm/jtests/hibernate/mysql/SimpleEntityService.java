package debop4s.data.orm.jtests.hibernate.mysql;

import debop4s.data.orm.ReadOnlyConnection;
import debop4s.data.orm.jtests.mapping.simple.SimpleEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface SimpleEntityService {

    @ReadOnlyConnection
    List<SimpleEntity> findAll();

    @ReadOnlyConnection
    List<SimpleEntity> getAll();

    SimpleEntity save(SimpleEntity entity);
}
