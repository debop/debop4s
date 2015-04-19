package debop4s.data.orm.hibernate.repository;

import debop4s.core.collections.PaginatedList;
import debop4s.core.collections.PaginatedListImpl;
import debop4s.data.orm.hibernate.HibernateParameter;
import debop4s.data.orm.hibernate.tools.CriteriaTool;
import debop4s.data.orm.hibernate.tools.HibernateTool;
import debop4s.data.orm.model.PersistentObject;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Hibernate 용 기본 Repository 입니다. (JPA 용은 이 것을 사용하는 것이 아니라
 * Spring Data JPA의 {@link org.springframework.data.jpa.repository.JpaRepository} 를 사용하세요.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오후 3:39
 * @deprecated use {@link debop4s.data.orm.hibernate.repository.HibernateDaoImpl}
 */
@Deprecated
@Slf4j
@SuppressWarnings("unchecked")
public class HibernateRepositoryImpl<T extends PersistentObject> implements HibernateRepository<T> {

    @Autowired
    SessionFactory sessionFactory;

    private final Class<T> clazz;

    public HibernateRepositoryImpl(Class<T> entityClass) {
        this.clazz = entityClass;
    }

    @Override
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void flush() {
        getSession().flush();
    }

    @Override
    public T load(Serializable id) {
        return (T) getSession().load(clazz, id);
    }

    @Override
    public T load(Serializable id, LockOptions lockOptions) {
        return (T) getSession().load(clazz, id, lockOptions);
    }

    @Override
    public T get(Serializable id) {
        return (T) getSession().get(clazz, id);
    }

    @Override
    public T get(Serializable id, LockOptions lockOptions) {
        return (T) getSession().get(clazz, id, lockOptions);
    }

    @Override
    public List<T> getIn(Collection<? extends Serializable> ids) {
        DetachedCriteria dc = CriteriaTool.addIn(DetachedCriteria.forClass(clazz), "id", ids);
        return find(dc);
    }

    @Override
    public List<T> getIn(Serializable[] ids) {
        DetachedCriteria dc = CriteriaTool.addIn(DetachedCriteria.forClass(clazz), "id", ids);
        return find(dc);
    }

    @Override
    public ScrollableResults scroll(Class<?> clazz) {
        return scroll(DetachedCriteria.forClass(clazz));
    }

    @Override
    public ScrollableResults scroll(DetachedCriteria dc) {
        return dc.getExecutableCriteria(getSession()).scroll();
    }

    @Override
    public ScrollableResults scroll(DetachedCriteria dc, ScrollMode scrollMode) {
        return dc.getExecutableCriteria(getSession()).scroll(scrollMode);
    }

    @Override
    public ScrollableResults scroll(Criteria criteria) {
        return criteria.scroll();
    }

    @Override
    public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
        return criteria.scroll(scrollMode);
    }

    @Override
    public ScrollableResults scroll(Query query, HibernateParameter... parameters) {
        return HibernateTool.setParameters(query, parameters).scroll();
    }

    @Override
    public ScrollableResults scroll(Query query, ScrollMode scrollMode, HibernateParameter... parameters) {
        return HibernateTool.setParameters(query, parameters).scroll(scrollMode);
    }

    @Override
    public List<T> findAll() {
        return findAll(new Order[0]);
    }

    @Override
    public List<T> findAll(Order... orders) {
        if (orders == null || orders.length == 0) {
            return (List<T>) getSession().createQuery("from " + clazz.getName()).list();
        } else {
            Criteria criteria = getSession().createCriteria(clazz);
            return (List<T>) HibernateTool.addOrders(criteria, orders).list();
        }
    }

    @Override
    public List<T> findAll(int firstResult, int maxResults, Order... orders) {
        if (orders == null || orders.length == 0) {
            Query query = getSession().createQuery("from " + clazz.getName());
            return (List<T>) HibernateTool.setPaging(query, firstResult, maxResults).list();
        } else {
            Criteria criteria = getSession().createCriteria(clazz);
            criteria = HibernateTool.addOrders(criteria, orders);
            criteria = HibernateTool.setPaging(criteria, firstResult, maxResults);
            return (List<T>) criteria.list();
        }
    }

    @Override
    public List<T> find(Criteria criteria, Order... orders) {
        return (List<T>) HibernateTool.addOrders(criteria, orders).list();
    }

    @Override
    public List<T> find(Criteria criteria, int firstResult, int maxResults, Order... orders) {
        Criteria cri = HibernateTool.addOrders(criteria, orders);
        return (List<T>) HibernateTool.setPaging(cri, firstResult, maxResults).list();
    }

    @Override
    public List<T> find(DetachedCriteria dc, Order... orders) {
        return find(dc.getExecutableCriteria(getSession()), orders);
    }

    @Override
    public List<T> find(DetachedCriteria dc, int firstResult, int maxResults, Order... orders) {
        return find(dc.getExecutableCriteria(getSession()), firstResult, maxResults, orders);
    }

    @Override
    public List<T> find(Query query, HibernateParameter... parameters) {
        return (List<T>) HibernateTool.setParameters(query, parameters).list();
    }

    @Override
    public List<T> find(Query query, int firstResult, int maxResults, HibernateParameter... parameters) {
        Query q = HibernateTool.setParameters(query, parameters);
        return (List<T>) HibernateTool.setPaging(q, firstResult, maxResults).list();
    }

    @Override
    public List<T> findByHql(String hql, HibernateParameter... parameters) {
        return find(getSession().createQuery(hql), parameters);
    }

    @Override
    public List<T> findByHql(String hql, int firstResult, int maxResults, HibernateParameter... parameters) {
        return find(getSession().createQuery(hql), firstResult, maxResults, parameters);
    }

    @Override
    public List<T> findByNamedQuery(String queryName, HibernateParameter... parameters) {
        return find(getSession().getNamedQuery(queryName), parameters);
    }

    @Override
    public List<T> findByNamedQuery(String queryName, int firstResult, int maxResults, HibernateParameter... parameters) {
        return find(getSession().getNamedQuery(queryName), firstResult, maxResults, parameters);
    }

    @Override
    public List<T> findBySQLString(String sqlString, HibernateParameter... parameters) {
        return find(getSession().createSQLQuery(sqlString), parameters);
    }

    @Override
    public List<T> findBySQLString(String sqlString, int firstResult, int maxResults, HibernateParameter... parameters) {
        return find(getSession().createSQLQuery(sqlString), parameters);
    }

    @Override
    public List<T> findByExample(Example example) {
        return (List<T>) getSession().createCriteria(clazz).add(example).list();
    }

    @Override
    public PaginatedList<T> getPage(Criteria criteria, int pageNo, int pageSize, Order... orders) {
        Criteria countCriteria = HibernateTool.copyCriteria(criteria);
        long totalItemCount = count(countCriteria);

        List<T> items = find(criteria, (pageNo - 1) * pageSize, pageSize, orders);
        return new PaginatedListImpl(items, pageNo, pageSize, totalItemCount);
    }

    @Override
    public PaginatedList<T> getPage(DetachedCriteria dc, int pageNo, int pageSize, Order... orders) {
        return getPage(dc.getExecutableCriteria(getSession()), pageNo, pageSize, orders);
    }

    @Override
    public PaginatedList<T> getPage(Query query, int pageNo, int pageSize, HibernateParameter... parameters) {
        long totalItemCount = count(query, parameters);
        List<T> items = find(query, (pageNo - 1) * pageSize, pageSize, parameters);
        return new PaginatedListImpl<T>(items, pageNo, pageSize, totalItemCount);
    }

    @Override
    public PaginatedList<T> getPageByHql(String hql, int pageNo, int pageSize, HibernateParameter... parameters) {
        return getPage(getSession().createQuery(hql), pageNo, pageSize, parameters);
    }

    @Override
    public PaginatedList<T> getPageByNamedQuery(String queryName, int pageNo, int pageSize, HibernateParameter... parameters) {
        return getPage(getSession().getNamedQuery(queryName), pageNo, pageSize, parameters);
    }

    @Override
    public PaginatedList<T> getPageBySQLString(String sqlString, int pageNo, int pageSize, HibernateParameter... parameters) {
        return getPage(getSession().createSQLQuery(sqlString), pageNo, pageSize, parameters);
    }

    @Override
    public T findUnique(Criteria criteria) {
        return (T) criteria.uniqueResult();
    }

    @Override
    public T findUnique(DetachedCriteria dc) {
        return findUnique(dc.getExecutableCriteria(getSession()));
    }

    @Override
    public T findUnique(Query query, HibernateParameter... parameters) {
        return (T) HibernateTool.setParameters(query, parameters).uniqueResult();
    }

    @Override
    public T findUniqueByHql(String hql, HibernateParameter... parameters) {
        return findUnique(getSession().createQuery(hql), parameters);
    }

    @Override
    public T findUniqueByNamedQuery(String queryName, HibernateParameter... parameters) {
        return findUnique(getSession().getNamedQuery(queryName), parameters);
    }

    @Override
    public T findUniqueBySQLString(String sqlString, HibernateParameter... parameters) {
        return findUnique(getSession().createSQLQuery(sqlString), parameters);
    }

    @Override
    public T findFirst(Criteria criteria, Order... orders) {
        List<T> items = find(criteria, 0, 1, orders);
        return (items != null && items.size() > 0) ? items.get(0) : null;
    }

    @Override
    public T findFirst(DetachedCriteria dc, Order... orders) {
        return findFirst(dc.getExecutableCriteria(getSession()), orders);
    }

    @Override
    public T findFirst(Query query, HibernateParameter... parameters) {
        List<T> items = find(query, 0, 1, parameters);
        return (items != null && items.size() > 0) ? items.get(0) : null;
    }

    @Override
    public T findFirstByHql(String hql, HibernateParameter... parameters) {
        return findFirst(getSession().createQuery(hql), parameters);
    }

    @Override
    public T findFirstByNamedQuery(String queryName, HibernateParameter... parameters) {
        return findFirst(getSession().getNamedQuery(queryName), parameters);
    }

    @Override
    public T findFirstBySQLString(String sqlString, HibernateParameter... parameters) {
        return findFirst(getSession().createSQLQuery(sqlString), parameters);
    }

    @Override
    public boolean exists(Class<?> clazz) {
        return findFirstByHql("from " + clazz.getName()) != null;
    }

    @Override
    public boolean exists(Criteria criteria) {
        return findFirst(criteria) != null;
    }

    @Override
    public boolean exists(DetachedCriteria dc) {
        return exists(dc.getExecutableCriteria(getSession()));
    }

    @Override
    public boolean exists(Query query, HibernateParameter... parameters) {
        return findFirst(query, parameters) != null;
    }

    @Override
    public boolean existsByHql(String hql, HibernateParameter... parameters) {
        return findFirstByHql(hql, parameters) != null;
    }

    @Override
    public boolean existsByNamedQuery(String queryName, HibernateParameter... parameters) {
        return findFirstByNamedQuery(queryName, parameters) != null;
    }

    @Override
    public boolean existsBySQLString(String sqlString, HibernateParameter... parameters) {
        return findFirstBySQLString(sqlString, parameters) != null;
    }

    @Override
    public long count() {
        return count(getSession().createCriteria(clazz));
    }

    @Override
    public long count(Criteria criteria) {
        return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

    @Override
    public long count(DetachedCriteria dc) {
        return count(dc.getExecutableCriteria(getSession()));
    }

    @Override
    public long count(Query query, HibernateParameter... parameters) {
        return (Long) HibernateTool.setParameters(query, parameters)
                                   .setResultTransformer(CriteriaSpecification.PROJECTION)
                                   .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                                   .uniqueResult();
    }

    @Override
    public long countByHql(String hql, HibernateParameter... parameters) {
        return count(getSession().createQuery(hql), parameters);
    }

    @Override
    public long countByNamedQuery(String queryName, HibernateParameter... parameters) {
        return count(getSession().getNamedQuery(queryName), parameters);
    }

    @Override
    public long countBySQLString(String sqlString, HibernateParameter... parameters) {
        return count(getSession().createSQLQuery(sqlString), parameters);
    }

    @Override
    public T merge(T entity) {
        return (T) getSession().merge(entity);
    }


    @Override
    public void persist(T entity) {
        getSession().persist(entity);
    }


    @Override
    public Serializable save(T entity) {
        return getSession().save(entity);
    }


    @Override
    public void saveOrUpdate(T entity) {
        getSession().saveOrUpdate(entity);
    }


    @Override
    public void update(T entity) {
        getSession().update(entity);
    }


    @Override
    public void delete(T entity) {
        getSession().delete(entity);
    }


    @Override
    public void deleteById(Serializable id) {
        getSession().delete(load(id));
    }


    @Override
    public void deleteAll() {
        deleteAll(findAll());
    }


    @Override
    public void deleteAll(Collection<? extends T> entities) {
        final Session s = getSession();
        for (T entity : entities) {
            s.delete(entity);
        }
    }


    @Override
    public void deleteAll(Criteria criteria) {
        deleteAll(find(criteria));
    }


    @Override
    public void deleteAll(DetachedCriteria dc) {
        deleteAll(dc.getExecutableCriteria(getSession()));
    }


    @Override
    public int deleteAllWithoutCascade() {
        return getSession().createQuery("delete from " + clazz.getName()).executeUpdate();
    }


    @Override
    public int executeUpdate(Query query, HibernateParameter... parameters) {
        return HibernateTool.setParameters(query, parameters).executeUpdate();
    }


    @Override
    public int executeUpdateByHql(String hql, HibernateParameter... parameters) {
        return executeUpdate(getSession().createQuery(hql), parameters);
    }


    @Override
    public int executeUpdateByNamedQuery(String queryName, HibernateParameter... parameters) {
        return executeUpdate(getSession().getNamedQuery(queryName), parameters);
    }


    @Override
    public int executeUpdateBySQLString(String sqlString, HibernateParameter... parameters) {
        return executeUpdate(getSession().createSQLQuery(sqlString), parameters);
    }

    private <P> Criteria buildProjectionCriteria(Class<P> projectClass,
                                                 Criteria criteria,
                                                 Projection projections,
                                                 boolean distinctResult) {
        if (distinctResult) {
            criteria.setProjection(Projections.distinct(projections));
        } else {
            criteria.setProjection(projections);
        }

        return criteria.setResultTransformer(Transformers.aliasToBean(projectClass));
    }

    @Override
    public <P> P reportOne(Class<P> projectClass, ProjectionList projectionList, Criteria criteria) {
        Criteria report = buildProjectionCriteria(projectClass, criteria, projectionList, true);
        return (P) report.uniqueResult();
    }

    @Override
    public <P> P reportOne(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc) {
        return reportOne(projectClass, projectionList, dc.getExecutableCriteria(getSession()));
    }

    @Override
    public <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, Criteria criteria) {
        Criteria report = buildProjectionCriteria(projectClass, criteria, projectionList, false);
        return (List<P>) report.uniqueResult();
    }

    @Override
    public <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, Criteria criteria, int firstResult, int maxResults) {
        Criteria report = buildProjectionCriteria(projectClass, criteria, projectionList, false);
        return (List<P>) HibernateTool.setPaging(report, firstResult, maxResults).list();
    }

    @Override
    public <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc) {
        return reportList(projectClass, projectionList, dc.getExecutableCriteria(getSession()));
    }

    @Override
    public <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc, int firstResult, int maxResults) {
        return reportList(projectClass, projectionList, dc.getExecutableCriteria(getSession()), firstResult, maxResults);
    }

    @Override
    public <P> PaginatedList<P> reportPage(Class<P> projectClass, ProjectionList projectionList, Criteria criteria, int pageNo, int pageSize) {
        Criteria report = buildProjectionCriteria(projectClass, criteria, projectionList, false);
        long totalCount = count(report);
        List<P> items = HibernateTool.setPaging(report, (pageNo - 1) * pageSize, pageSize).list();
        return new PaginatedListImpl<P>(items, pageNo, pageSize, totalCount);
    }

    @Override
    public <P> PaginatedList<P> reportPage(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc, int pageNo, int pageSize) {
        return reportPage(projectClass, projectionList, dc.getExecutableCriteria(getSession()), pageNo, pageSize);
    }
}
