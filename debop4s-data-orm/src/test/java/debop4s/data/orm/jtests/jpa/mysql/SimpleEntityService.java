package debop4s.data.orm.jtests.jpa.mysql;

import debop4s.data.orm.ReadOnlyConnection;
import debop4s.data.orm.jtests.mapping.simple.SimpleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SimpleEntityService
 *
 * @author sunghyouk.bae@gmail.com 2014. 9. 7.
 */
@Slf4j
@Service
@Transactional
public class SimpleEntityService {

    @Autowired SimpleEntityRepository repo;

    @ReadOnlyConnection
    public List<SimpleEntity> findAll() {
        return repo.findAll();
    }

    public SimpleEntity save(SimpleEntity entity) {
        log.debug("save entity. {}", entity);
        return repo.save(entity);
    }

    @ReadOnlyConnection
    public SimpleEntity load(final Long id) {
        return repo.findOne(id);
    }

    @ReadOnlyConnection
    public SimpleEntity loadByJPQL(final Long id) {
        return repo.findById(id);
    }
}
