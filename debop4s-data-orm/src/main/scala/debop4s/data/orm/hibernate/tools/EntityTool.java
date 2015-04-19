package debop4s.data.orm.hibernate.tools;

import com.google.common.collect.Sets;
import debop4s.core.Func1;
import debop4s.core.ValueObject;
import debop4s.core.json.JacksonSerializer;
import debop4s.core.json.JsonSerializer;
import debop4s.core.tools.MapperTool;
import debop4s.core.utils.Graphs;
import debop4s.data.orm.hibernate.HibernateParameter;
import debop4s.data.orm.hibernate.repository.HibernateDao;
import debop4s.data.orm.model.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.*;

/**
 * Entity 와 관련된 여러가지 정보 제공
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 8. 오후 7:33
 * @deprecated use {@link org.apache.http.util.EntityUtils}
 */
@Deprecated
@Slf4j
@SuppressWarnings("unchecked")
public final class EntityTool {

    private EntityTool() { }

    private static final String PROPERTY_ANCESTORS = "ancestors";
    private static final String PROPERTY_DESCENDENTS = "descendents";

    private static final JsonSerializer jsonSerializer = new JacksonSerializer();

    public static String entityToString(ValueObject entity) {
        return (entity == null) ? "" : entity.toString();
    }

    public static String asJsonText(ValueObject entity) {
        return jsonSerializer.serializeToText(entity);
    }

    public static <T extends HierarchyEntity<T>> void assertNotCirculaHierarchy(T child, T parent) {
        if (child == parent)
            throw new IllegalArgumentException("child and parent are same.");
        if (child.getDescendents().contains(parent))
            throw new IllegalArgumentException("child has parent as descendents");

        if (Sets.intersection(parent.getAncestors(), child.getDescendents()).size() > 0)
            throw new IllegalArgumentException("ancestors of parent and descendents of child has same thing.");
    }

    public static <T extends HierarchyEntity<T>> void setHierarchy(T child, T oldParent, T newParent) {
        assert (child != null);
        log.trace("현재 노드의 부모를 변경하고, 계층구조를 변경합니다...child=[{}], oldParent=[{}], newParent=[{}]", child, oldParent, newParent);

        if (oldParent != null)
            removeHierarchy(child, oldParent);
        if (newParent != null)
            setHierarchy(child, newParent);
    }

    public static <T extends HierarchyEntity<T>> void setHierarchy(T child, T parent) {
        if (parent == null || child == null)
            return;
        log.trace("노드의 부모 및 조상을 설정합니다. child=[{}], parent=[{}]", child, parent);

        parent.getDescendents().add(child);
        parent.getDescendents().addAll(child.getDescendents());

        for (T ancestor : parent.getAncestors()) {
            ancestor.getDescendents().add(child);
            ancestor.getDescendents().addAll(child.getDescendents());
        }
        child.getAncestors().add(parent);
        child.getAncestors().addAll(parent.getAncestors());
    }

    public static <T extends HierarchyEntity<T>> void removeHierarchy(T child, T parent) {
        if (parent == null || child == null)
            return;
        log.trace("노드의 부모 및 조상을 제거합니다. child=[{}], parent=[{}]", child, parent);

        child.getAncestors().remove(parent);
        child.getAncestors().removeAll(parent.getAncestors());

        for (T ancestor : parent.getAncestors()) {
            ancestor.getDescendents().remove(child);
            ancestor.getDescendents().removeAll(child.getDescendents());
        }

        for (T des : child.getDescendents()) {
            des.getAncestors().remove(parent);
            des.getAncestors().removeAll(parent.getAncestors());
        }
    }

    public static <T extends HierarchyEntity<T>> DetachedCriteria getAncestorsCriteria(T entity, Session session, Class<T> entityClass) {
        return DetachedCriteria.forClass(entityClass)
                               .createAlias(PROPERTY_DESCENDENTS, "des")
                               .add(Restrictions.eq("des.id", entity.getId()));
    }

    public static <T extends HierarchyEntity<T>> DetachedCriteria getDescendentsCriteria(T entity, Session session, Class<T> entityClass) {
        return DetachedCriteria.forClass(entityClass)
                               .createAlias(PROPERTY_ANCESTORS, "ans")
                               .add(Restrictions.eq("ans.id", entity.getId()));
    }

    public static <T extends HierarchyEntity<T>> DetachedCriteria getAncestorIds(T entity, Session session, Class<T> entityClass) {
        return getAncestorsCriteria(entity, session, entityClass)
                .setProjection(Projections.distinct(Projections.id()));
    }

    public static <T extends HierarchyEntity<T>> DetachedCriteria getDescendentIds(T entity, Session session, Class<T> entityClass) {
        return getDescendentsCriteria(entity, session, entityClass)
                .setProjection(Projections.distinct(Projections.id()));
    }

    /** 특정 로케일 키를 가지는 엔티티를 조회하는 HQL 문. */
    private static final String GET_LIST_BY_LOCALE_KEY =
            "select distinct loen from %s loen where :key in indices (loen.localeMap)";

    /** 특정 로케일 속성값에 따른 엔티티를 조회하는 HQL 문. */
    private static final String GET_LIST_BY_LOCALE_PROPERTY =
            "select distinct loen from %s loen join loen.localeMap locale where locale.%s = :%s";

    public static <T extends LocaleEntity<TLocaleValue>, TLocaleValue extends LocaleValue>
    void copyLocale(T src, T dest) {
        for (Locale locale : src.getLocales()) {
            dest.addLocaleValue(locale, src.getLocaleValue(locale));
        }
    }

    public static <T extends LocaleEntity<TLocaleValue>, TLocaleValue extends LocaleValue>
    List<T> containsLocale(HibernateDao dao, Class<T> entityClass, Locale locale) {
        String hql = String.format(GET_LIST_BY_LOCALE_KEY, entityClass.getName());
        return (List<T>) dao.findByHql(hql, new HibernateParameter("key", locale));
    }

    public static final String GET_LIST_BY_META_KEY = "select distinct me from %s me where :key in indices(me.metaMap)";

    public static final String GET_LIST_BY_META_VALUE = "select distinct me from %s me join me.metaMap meta where meta.value = :value";

    public static <T extends MetaEntity> List<T> containsMetaKey(HibernateDao dao, Class<T> entityClass, String key) {
        String hql = String.format(GET_LIST_BY_META_KEY, entityClass.getName());
        return (List<T>) dao.findByHql(hql, new HibernateParameter("key", key));
    }

    public static <T extends MetaEntity> List<T> containsMetaValue(HibernateDao dao, Class<T> entityClass, String value) {
        String hql = String.format(GET_LIST_BY_META_VALUE, value);
        return (List<T>) dao.findByHql(hql, new HibernateParameter("value", value));
    }


    public static <S, T> T mapEntity(S source, T target) {
        MapperTool.map(source, target);
        return target;
    }

    public static <S, T> T mapEntity(S source, Class<T> targetClass) {
        return MapperTool.createMap(source, targetClass);
    }

    public static <S, T> List<T> mapEntities(List<S> sources, List<T> targets) {
        int size = Math.min(sources.size(), targets.size());
        for (int i = 0; i < size; i++) {
            MapperTool.map(sources.get(i), targets.get(i));
        }
        return targets;
    }

    public static <S, T> List<T> mapEntities(List<S> sources, Class<T> targetClass) {
        List<T> targets = new ArrayList<>();
        for (S src : sources) {
            targets.add(mapEntity(src, targetClass));
        }
        return targets;
    }

    @Deprecated
    public static <S, T> List<T> mapEntitiesAsParallel(List<S> sources, Class<T> targetClass) {
        List<T> targets = new ArrayList<>();
        for (S src : sources) {
            targets.add(mapEntity(src, targetClass));
        }
        return targets;
    }

    public static <T extends TreeEntity<T>> void updateTreeNodePosition(T entity) {
        assert (entity != null);

        TreeNodePosition np = entity.getNodePosition();
        if (entity.getParent() != null) {
            np.setLevel(entity.getParent().getNodePosition().getLevel() + 1);
            if (!entity.getParent().getChildren().contains(entity)) {
                np.setOrder(entity.getParent().getChildren().size());
            }
        } else {
            np.setPosition(0, 0);
        }
    }

    public static <T extends TreeEntity<T>> long getChildCount(HibernateDao dao, T entity) {
        DetachedCriteria dc = DetachedCriteria.forClass(entity.getClass());
        dc.add(Restrictions.eq("parent", entity));
        return dao.count(dc);
    }

    public static <T extends TreeEntity<T>> boolean hasChildren(HibernateDao dao, T entity) {
        DetachedCriteria dc = DetachedCriteria.forClass(entity.getClass());
        dc.add(Restrictions.eq("parent", entity));

        return dao.exists(dc);
    }

    public static <T extends TreeEntity<T>> void setNodeOrder(T node, int order) {
        assert (node != null);

        if (node.getParent() != null) {
            for (T child : node.getParent().getChildren()) {
                if (child.getNodePosition().getOrder() >= order) {
                    child.getNodePosition().setOrder(child.getNodePosition().getOrder() + 1);
                }
            }
        }
        node.getNodePosition().setOrder(order);
    }

    public static <T extends TreeEntity<T>> void adjustChildOrders(T parent) {
        assert (parent != null);

        List<T> children = new ArrayList<>(parent.getChildren());
        Collections.sort(children, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.getNodePosition().getOrder() - o2.getNodePosition().getOrder();
            }
        });
        int order = 0;
        for (T node : children) {
            node.getNodePosition().setOrder(order);
            order++;
        }
    }

    public static <T extends TreeEntity<T>> void changeParent(T node, T oldParent, T newParent) {
        assert (node != null);

        if (oldParent != null) {
            oldParent.getChildren().remove(node);
        }
        if (newParent != null) {
            newParent.getChildren().add(node);
        }
        node.setParent(newParent);
        updateTreeNodePosition(node);
    }

    public static <T extends TreeEntity<T>> void setParent(T node, T parent) {
        assert (node != null);
        changeParent(node, node.getParent(), parent);
    }

    public static <T extends TreeEntity<T>> void insertChildNode(T parent, T child, int order) {
        assert (parent != null);
        assert (child != null);

        int ord = Math.max(0, Math.min(order, parent.getChildren().size() - 1));
        parent.addChild(child);
        setNodeOrder(child, ord);
    }

    public static <T extends TreeEntity<T>> Iterable<T> getAncestors(T current) {
        List<T> ancestors = new ArrayList<>();
        if (current != null) {
            T parent = current;
            while (parent != null) {
                ancestors.add(parent);
                parent = parent.getParent();
            }
        }
        return ancestors;
    }

    public static <T extends TreeEntity<T>> Iterable<T> getDescendents(T current) {
        return Graphs.depthFirstScanJava(
                current,
                new Func1<T, Iterable<T>>() {
                    @Override
                    public Iterable<T> execute(T arg) {
                        return arg.getChildren();
                    }
                }
                                        );
    }

    public static <T extends TreeEntity<T>> T getRoot(T current) {
        if (current == null)
            return current;

        T root = current;
        T parent = current.getParent();
        while (parent != null) {
            root = parent;
            parent = parent.getParent();
        }
        return root;
    }


}
