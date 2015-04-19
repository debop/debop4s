package debop4s.data.orm.hibernate.tools;

import debop4s.core.io.Serializers;
import debop4s.data.orm.hibernate.HibernateParameter;
import debop4s.data.orm.hibernate.listener.UpdatedTimestampListener;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionFactoryImpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Hibernate 와 관련된 Helper class 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 28. 오후 4:37
 * @deprecated use {@link debop4s.data.orm.hibernate.utils.HibernateUtils}
 */
@Deprecated
@Slf4j
public final class HibernateTool {

    private HibernateTool() { }

    /**
     * Hibernate 4 이상에서 {@link Configuration}을 이용하여 {@link SessionFactory}를 빌드합니다.
     *
     * @param cfg Hibernate 4 용 환경설정
     * @return [[org.hibernate.SessionFactory]] 인스턴스
     */
    public static SessionFactory buildSessionFactory(Configuration cfg) {
        assert (cfg != null);
        log.info("SessionFactory를 빌드합니다...");

        // for hibernate 4.2.x
//        ServiceRegistryBuilder registryBuilder = new ServiceRegistryBuilder().applySettings(cfg.getProperties());
//        SessionFactory factory = cfg.buildSessionFactory(registryBuilder.buildServiceRegistry());

        // for hibernate 4.3.x
        StandardServiceRegistryBuilder registryBuilder =
                new StandardServiceRegistryBuilder().applySettings(cfg.getProperties());
        SessionFactory factory = cfg.buildSessionFactory(registryBuilder.build());

        log.info("SessionFactory를 빌드했습니다.");
        return factory;
    }

    /**
     * Hibernate SessionFactory에 event listener 를 등록합니다.
     *
     * @param sessionFactory SessionFactory instance
     * @param listener       Listener instance
     * @param eventTypes     리스닝할 이벤트 종류
     * @tparam T Listener 수형
     */
    @SafeVarargs
    public static <T extends Serializable> void registerEventListener(SessionFactory sessionFactory,
                                                                      T listener,
                                                                      EventType<T>... eventTypes) {
        EventListenerRegistry registry =
                ((SessionFactoryImpl) sessionFactory)
                        .getServiceRegistry()
                        .getService(EventListenerRegistry.class);

        for (EventType<T> eventType : eventTypes) {
            registry.getEventListenerGroup(eventType).appendListener(listener);
        }
    }

    /**
     * Hibernate SessionFactory에 event listener 를 등록합니다.
     *
     * @param sessionFactory SessionFactory instance
     * @param listener       Listener instance
     * @param eventTypes     리스닝할 이벤트 종류
     * @tparam T Listener 수형
     */
    public static <T extends Serializable> void registerEventListener(SessionFactory sessionFactory,
                                                                      T listener,
                                                                      Iterable<EventType<T>> eventTypes) {
        EventListenerRegistry registry =
                ((SessionFactoryImpl) sessionFactory)
                        .getServiceRegistry()
                        .getService(EventListenerRegistry.class);

        for (EventType<T> eventType : eventTypes) {
            registry.getEventListenerGroup(eventType).appendListener(listener);
        }
    }

    /**
     * SessionFactory 에 {@link UpdatedTimestampListener} 를 추가합니다.
     *
     * @param sessionFactory SessionFactory instance
     */
    public static void registerUpdateTimestampEventListener(SessionFactory sessionFactory) {
        UpdatedTimestampListener listener = new UpdatedTimestampListener();

        EventListenerRegistry registry =
                ((SessionFactoryImpl) sessionFactory)
                        .getServiceRegistry()
                        .getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(listener);
        registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(listener);
    }

    /**
     * {@link debop4s.data.orm.hibernate.HibernateParameter} 배열을 {@link Map}으로 변환합니다.
     *
     * @param parameters Hibernate Parameters
     * @return Map
     */
    public static Map<String, Object> toMap(HibernateParameter... parameters) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (HibernateParameter parameter : parameters) {
            map.put(parameter.getName(), parameter.getValue());
        }
        return map;
    }

    public static DetachedCriteria createDetachedCriteria(Class<?> clazz) {
        return DetachedCriteria.forClass(clazz);
    }

    public static Criteria createCriteria(Class<?> clazz, Session session, Order[] orders, Criterion... criterions) {
        Criteria crit = session.createCriteria(clazz);
        addOrders(crit, orders);
        addCriterions(crit, criterions);

        return crit;
    }

    public static DetachedCriteria copyDetachedCriteria(DetachedCriteria src) {
        return Serializers.copyObject(src);
    }

    public static Criteria copyCriteria(Criteria src) {
        return (CriteriaImpl) Serializers.copyObject((CriteriaImpl) src);
    }

    public static Criteria getExecutableCriteria(Session session, DetachedCriteria dc) {
        return dc.getExecutableCriteria(session);
    }

    public static Criteria getExecutableCriteria(DetachedCriteria dc, Session session, Order... orders) {
        Criteria criteria = getExecutableCriteria(session, dc);
        for (Order o : orders) {
            criteria.addOrder(o);
        }
        return criteria;
    }

    public static Criteria getExecutableCriteria(DetachedCriteria dc, Session session, Iterable<Order> orders) {
        Criteria criteria = getExecutableCriteria(session, dc);
        for (Order o : orders) {
            criteria.addOrder(o);
        }
        return criteria;
    }

    public static DetachedCriteria addOrders(DetachedCriteria dc, Order... orders) {
        for (Order o : orders) {
            dc.addOrder(o);
        }
        return dc;
    }

    public static DetachedCriteria addOrders(DetachedCriteria dc, Iterable<Order> orders) {
        for (Order o : orders) {
            dc.addOrder(o);
        }
        return dc;
    }

    public static Criteria addOrders(Criteria criteria, Order... orders) {
        for (Order o : orders) {
            criteria.addOrder(o);
        }
        return criteria;
    }

    public static Criteria addOrders(Criteria criteria, Iterable<Order> orders) {
        for (Order o : orders) {
            criteria.addOrder(o);
        }
        return criteria;
    }


    public static Criteria addCriterions(Criteria criteria, Criterion... criterions) {
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    public static Criteria addCriterions(Criteria criteria, Iterable<Criterion> criterions) {
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    public static Query setParameters(Query query, HibernateParameter... parameters) {
        for (HibernateParameter p : parameters) {
            query.setParameter(p.getName(), p.getValue());
        }
        return query;
    }

    public static Query setParameters(Query query, Iterable<HibernateParameter> parameters) {
        for (HibernateParameter p : parameters) {
            query.setParameter(p.getName(), p.getValue());
        }
        return query;
    }

    public static Criteria setFirstResult(Criteria criteria, int firstResult) {
        if (firstResult >= 0)
            criteria.setFirstResult(firstResult);
        return criteria;
    }

    public static Query setFirstResult(Query query, int firstResult) {
        if (firstResult >= 0)
            query.setFirstResult(firstResult);
        return query;
    }

    public static Criteria setMaxResults(Criteria criteria, int maxResults) {
        if (maxResults > 0)
            criteria.setMaxResults(maxResults);
        return criteria;
    }

    public static Query setMaxResults(Query query, int maxResults) {
        if (maxResults > 0)
            query.setMaxResults(maxResults);
        return query;
    }

    public static Criteria setPaging(Criteria criteria, int firstResult, int maxResults) {
        criteria = setFirstResult(criteria, firstResult);
        return setMaxResults(criteria, maxResults);
    }

    public static Query setPaging(Query query, int firstResult, int maxResults) {
        query = setFirstResult(query, firstResult);
        return setMaxResults(query, maxResults);
    }


}
