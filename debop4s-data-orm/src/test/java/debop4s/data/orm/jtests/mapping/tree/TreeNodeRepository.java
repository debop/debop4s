package debop4s.data.orm.jtests.mapping.tree;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * {@link TreeNode}용 Repository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 8:29
 */
public interface TreeNodeRepository
        extends JpaRepository<TreeNode, Long>, QueryDslPredicateExecutor<TreeNode> {

    @Query("select node from TreeNode node where node.parent is null")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TreeNode> findRoots();

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TreeNode> findByParentIsNull();
}
