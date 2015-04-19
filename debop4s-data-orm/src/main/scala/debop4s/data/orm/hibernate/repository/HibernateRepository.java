package debop4s.data.orm.hibernate.repository;

import debop4s.core.collections.PaginatedList;
import debop4s.data.orm.hibernate.HibernateParameter;
import org.hibernate.*;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * HibernateRepository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 8. 오후 3:49
 * @deprecated use {@link debop4s.data.orm.hibernate.repository.HibernateDao}
 */
@Deprecated
@Transactional(readOnly = true)
public interface HibernateRepository<T> {

    Session getSession();

    void flush();

    T load(Serializable id);

    T load(Serializable id, LockOptions lockOptions);

    T get(Serializable id);

    T get(Serializable id, LockOptions lockOptions);

    List<T> getIn(Collection<? extends Serializable> ids);

    List<T> getIn(Serializable[] ids);

    ScrollableResults scroll(Class<?> clazz);

    ScrollableResults scroll(DetachedCriteria dc);

    ScrollableResults scroll(DetachedCriteria dc, ScrollMode scrollMode);

    ScrollableResults scroll(Criteria criteria);

    ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode);

    ScrollableResults scroll(Query query, HibernateParameter... parameters);

    ScrollableResults scroll(Query query, ScrollMode scrollMode, HibernateParameter... parameters);

    List<T> findAll();

    List<T> findAll(Order... orders);

    List<T> findAll(int firstResult, int maxResult, Order... orders);

    List<T> find(Criteria criteria, Order... orders);

    List<T> find(Criteria criteria, int firstResult, int maxResult, Order... orders);

    List<T> find(DetachedCriteria dc, Order... orders);

    List<T> find(DetachedCriteria dc, int firstResult, int maxResults, Order... orders);

    List<T> find(Query query, HibernateParameter... parameters);

    List<T> find(Query query, int firstResult, int maxResults, HibernateParameter... parameters);

    List<T> findByHql(final String hql, HibernateParameter... parameters);

    List<T> findByHql(final String hql, int firstResult, int maxResults, HibernateParameter... parameters);

    List<T> findByNamedQuery(String queryName, HibernateParameter... parameters);

    List<T> findByNamedQuery(final String queryName, int firstResult, int maxResults, HibernateParameter... parameters);

    List<T> findBySQLString(final String sqlString, HibernateParameter... parameters);

    List<T> findBySQLString(final String sqlString, int firstResult, int maxResults, HibernateParameter... parameters);

    List<T> findByExample(Example example);

    PaginatedList<T> getPage(Criteria criteria, int pageNo, int pageSize, Order... orders);

    PaginatedList<T> getPage(DetachedCriteria dc, int pageNo, int pageSize, Order... orders);

    PaginatedList<T> getPage(Query query, int pageNo, int pageSize, HibernateParameter... parameters);

    PaginatedList<T> getPageByHql(String hql, int pageNo, int pageSize, HibernateParameter... parameters);

    PaginatedList<T> getPageByNamedQuery(final String queryName, int pageNo, int pageSize, HibernateParameter... parameters);

    PaginatedList<T> getPageBySQLString(final String sqlString, int pageNo, int pageSize, HibernateParameter... parameters);


    /**
     * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
     *
     * @param criteria 조회 조건
     * @return 조회된 엔티티
     */
    T findUnique(Criteria criteria);

    /**
     * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
     *
     * @param dc 조회 조건
     * @return 조회된 엔티티
     */
    T findUnique(DetachedCriteria dc);

    /**
     * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
     *
     * @param query 조회 조건
     * @return 조회된 엔티티
     */
    T findUnique(Query query, HibernateParameter... parameters);

    /**
     * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
     *
     * @param hql 조회 조건
     * @return 조회된 엔티티
     */
    T findUniqueByHql(String hql, HibernateParameter... parameters);

    /**
     * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
     *
     * @param queryName 쿼리 명
     * @return 조회된 엔티티
     */
    T findUniqueByNamedQuery(final String queryName, HibernateParameter... parameters);

    /**
     * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
     *
     * @param sqlString 쿼리 명
     * @return 조회된 엔티티
     */
    T findUniqueBySQLString(final String sqlString, HibernateParameter... parameters);

    /**
     * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
     *
     * @param criteria 조회 조건
     * @return 조회된 엔티티
     */
    T findFirst(Criteria criteria, Order... orders);

    /**
     * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
     *
     * @param dc 조회 조건
     * @return 조회된 엔티티
     */
    T findFirst(DetachedCriteria dc, Order... orders);

    /**
     * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
     *
     * @param query 조회 조건
     * @return 조회된 엔티티
     */
    T findFirst(Query query, HibernateParameter... parameters);

    /**
     * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
     *
     * @param hql 조회 조건
     * @return 조회된 엔티티
     */
    T findFirstByHql(String hql, HibernateParameter... parameters);

    /**
     * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
     *
     * @param queryName 쿼리 명
     * @return 조회된 엔티티
     */
    T findFirstByNamedQuery(final String queryName, HibernateParameter... parameters);

    /**
     * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
     *
     * @param sqlString 쿼리 명
     * @return 조회된 엔티티
     */
    T findFirstBySQLString(final String sqlString, HibernateParameter... parameters);

    boolean exists(Class<?> clazz);

    boolean exists(Criteria criteria);

    boolean exists(DetachedCriteria dc);

    boolean exists(Query query, HibernateParameter... parameters);

    boolean existsByHql(String hql, HibernateParameter... parameters);

    boolean existsByNamedQuery(final String queryName, HibernateParameter... parameters);

    boolean existsBySQLString(final String sqlString, HibernateParameter... parameters);

    long count();

    long count(Criteria criteria);

    long count(DetachedCriteria dc);

    long count(Query query, HibernateParameter... parameters);

    long countByHql(String hql, HibernateParameter... parameters);

    long countByNamedQuery(final String queryName, HibernateParameter... parameters);

    long countBySQLString(final String sqlString, HibernateParameter... parameters);


    T merge(T entity);

    @Transactional
    void persist(T entity);

    @Transactional
    Serializable save(T entity);

    @Transactional
    void saveOrUpdate(T entity);

    @Transactional
    void update(T entity);

    @Transactional
    void delete(T entity);

    @Transactional
    void deleteById(Serializable id);

    @Transactional
    void deleteAll();

    @Transactional
    void deleteAll(Collection<? extends T> entities);

    @Transactional
    void deleteAll(Criteria criteria);

    @Transactional
    void deleteAll(DetachedCriteria dc);


    /** Cascade 적용 없이 엔티티들을 모두 삭제합니다. */
    @Transactional
    int deleteAllWithoutCascade();

    /**
     * 쿼리를 실행합니다.
     *
     * @param query      실행할 Query
     * @param parameters 인자 정보
     * @return 실행에 영향 받은 행의 수
     */
    @Transactional
    int executeUpdate(Query query, HibernateParameter... parameters);

    /**
     * 지정한 HQL 구문 (insert, update, del) 을 수행합니다.
     *
     * @param hql        수행할 HQL 구문
     * @param parameters 인자 정보
     * @return 실행에 영향 받은 행의 수
     */
    @Transactional
    int executeUpdateByHql(String hql, HibernateParameter... parameters);

    /**
     * 지정한 쿼리 구문 (insert, update, del) 을 수행합니다.
     *
     * @param queryName  수행할 Query 명
     * @param parameters 인자 정보
     * @return 실행에 영향 받은 행의 수
     */
    @Transactional
    int executeUpdateByNamedQuery(final String queryName, HibernateParameter... parameters);

    /**
     * 지정한 쿼리 구문 (insert, update, del) 을 수행합니다.
     *
     * @param sqlString  수행할 Query
     * @param parameters 인자 정보
     * @return 실행에 영향 받은 행의 수
     */
    @Transactional
    int executeUpdateBySQLString(final String sqlString, HibernateParameter... parameters);

    <P> P reportOne(Class<P> projectClass, ProjectionList projectionList, Criteria criteria);

    <P> P reportOne(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc);

    <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, Criteria criteria);

    <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, Criteria criteria, int firstResult, int maxResults);

    <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc);

    <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc, int firstResult, int maxResults);

    <P> PaginatedList<P> reportPage(Class<P> projectClass, ProjectionList projectionList, Criteria criteria, int pageNo, int pageSize);

    <P> PaginatedList<P> reportPage(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc, int pageNo, int pageSize);
}
