package debop4s.data.orm.model;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 계층형 구조 엔티티의 TREE 상에서의 위치를 나타냅니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 11. 오전 9:02
 */
@Embeddable
@Access(AccessType.FIELD)
public class TreeNodePosition extends ValueObjectBase {

    public static TreeNodePosition copyFrom(TreeNodePosition src) {
        return new TreeNodePosition(src);
    }

    public TreeNodePosition() {
        this(0, 0);
    }

    public TreeNodePosition(Integer level, Integer order) {
        this.level = level;
        this.order = order;
    }

    public TreeNodePosition(TreeNodePosition src) {
        this.level = src.level;
        this.order = src.order;
    }

    public void setPosition(Integer level, Integer order) {
        this.level = level;
        this.order = order;
    }

    public void setPosition(TreeNodePosition src) {
        this.level = src.level;
        this.order = src.order;
    }

    /** 트리 레벨 (몇대손) */
    @Column(name = "treeLevel")
    private Integer level;

    /** 트리 노드 정렬 순서 - 형제끼리의 순서 */
    @Column(name = "treeOrder")
    private Integer order;

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public int hashCode() {
        return Hashs.compute(level, order);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("level", level)
                    .add("order", order);
    }

    private static final long serialVersionUID = 3455568346636164669L;
}
