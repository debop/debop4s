package debop4s.data.orm.jtests.mapping.tree;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import debop4s.data.orm.model.TreeEntity;
import debop4s.data.orm.model.TreeNodePosition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Self-Referencing 을 하는 엔티티를 표현합니다. (부서처럼 트리형태를 가진 엔티티)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 29. 오후 2:44
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.tree", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class TreeNode extends HibernateEntityBase<Long> implements TreeEntity<TreeNode> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String title;
    private String data;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private TreeNode parent;

    @OneToMany(mappedBy = "parent", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private Set<TreeNode> children = new LinkedHashSet<TreeNode>();

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "level", column = @Column(name = "treeLevel")),
                          @AttributeOverride(name = "order", column = @Column(name = "treeOrder")) })
    TreeNodePosition nodePosition = new TreeNodePosition();

    /** 자식 노드를 추가합니다. */
    @Override
    public void addChild(TreeNode child) {
        child.setParent(this);
        children.add(child);
    }

    /** 자식 노드를 삭제합니다. */
    @Override
    public void removeChild(TreeNode child) {
        if (children.contains(child)) {
            children.remove(child);
            child.setParent(null);
        }
    }

    @Override
    public int hashCode() {
        return Hashs.compute(title);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("id", id)
                    .add("title", title)
                    .add("description", description);
    }

    private static final long serialVersionUID = 413618594652107843L;


}
