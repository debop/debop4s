package debop4s.data.orm.jtests.mapping.tree;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.tree.TreeNodeJUnitSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 6:27
 */
@Slf4j
@Transactional
public class TreeNodeTest extends JpaTestBase {

    @PersistenceContext EntityManager em;
    @Autowired TreeNodeRepository repository;

    @Test
    public void buildTreeTest() {
        repository.deleteAllInBatch();
        repository.flush();


        TreeNode root = new TreeNode();
        root.setTitle("root");

        TreeNode child1 = new TreeNode();
        child1.setTitle("child1");

        TreeNode child2 = new TreeNode();
        child1.setTitle("child2");

        root.addChild(child1);
        root.addChild(child2);

        TreeNode child11 = new TreeNode();
        child11.setTitle("child11");

        TreeNode child12 = new TreeNode();
        child12.setTitle("child12");

        child1.addChild(child11);
        child1.addChild(child12);

        repository.save(root);
        repository.flush();
        em.clear();

        TreeNode node = repository.findOne(child1.getId());

        assertThat(node.getChildren().size()).isEqualTo(2);
        assertThat(node.getParent()).isEqualTo(root);


        // JPQL 로 정의된 메소드
        List<TreeNode> roots = repository.findRoots();
        assertThat(roots.size()).isEqualTo(1);
        assertThat(roots.get(0)).isEqualTo(root);

        // 동적 생성
        roots = repository.findByParentIsNull();
        assertThat(roots.size()).isEqualTo(1);
        assertThat(roots.get(0)).isEqualTo(root);

//        List<TreeNode> nodes = Graphs.graphDepthFirstScan(roots.get(0), new Function1<TreeNode, Iterable<TreeNode>>() {
//            @Override
//            public Iterable<TreeNode> execute(TreeNode arg) {
//                return arg.getChildren();
//            }
//        });
//
//        repository.deleteInBatch(nodes);
//        repository.flush();
    }

    @Test
    public void buildTreeAndDelete() throws Exception {
        repository.deleteAllInBatch();
        repository.flush();

        TreeNode root = new TreeNode();
        root.setTitle("root");

        TreeNode child1 = new TreeNode();
        child1.setTitle("child1");

        TreeNode child2 = new TreeNode();
        child1.setTitle("child2");

        root.addChild(child1);
        root.addChild(child2);

        TreeNode child11 = new TreeNode();
        child11.setTitle("child11");

        TreeNode child12 = new TreeNode();
        child12.setTitle("child12");

        child1.addChild(child11);
        child1.addChild(child12);

        repository.saveAndFlush(root);
        em.clear();

        TreeNode child = repository.findOne(child1.getId());
        repository.delete(child);
        em.flush();
        em.clear();

        List<TreeNode> roots = repository.findRoots();
        assertThat(roots).hasSize(1);
        assertThat(roots.get(0).getChildren()).hasSize(1);

    }
}
