package debop4s.data.orm.jtests.hibernate.mysql;

import debop4s.data.orm.ReadOnlyConnection;
import debop4s.data.orm.hibernate.repository.HibernateQueryDslDao;
import debop4s.data.orm.jtests.mapping.simple.QSimpleEntity;
import debop4s.data.orm.jtests.mapping.simple.SimpleEntity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings("unchecked")
@Transactional
public class SimpleEntityServiceImpl implements SimpleEntityService {

    @Autowired
    SessionFactory sessionFactory;

    @ReadOnlyConnection
    @Override
    public List<SimpleEntity> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return (List<SimpleEntity>) session.createCriteria(SimpleEntity.class).list();
    }

    @Autowired
    HibernateQueryDslDao dao;


    // @ReadOnlyConnection  // 없어도 dao.findAll 이 readOnly 이다!!!
    @Override
    public List<SimpleEntity> getAll() {
        QSimpleEntity entity = QSimpleEntity.simpleEntity;
        return dao.findAll(entity);
    }

    public SimpleEntity save(SimpleEntity entity) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(entity);
        return entity;
    }
}
