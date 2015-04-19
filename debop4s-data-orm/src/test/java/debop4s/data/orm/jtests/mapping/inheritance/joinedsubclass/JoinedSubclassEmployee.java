package debop4s.data.orm.jtests.mapping.inheritance.joinedsubclass;

import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * JoinedSubclassEmployee
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오전 11:15
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.inheritance", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class JoinedSubclassEmployee extends JoinedSubclassPerson {

    @Column(name = "EmpNo", nullable = false)
    private String empNo;

    //
    // Employee는 계층 구조를 가진다.
    //
    @ManyToOne
    @JoinColumn(name = "ManagerId")
    private JoinedSubclassEmployee manager;

    @OneToMany(mappedBy = "manager", cascade = { CascadeType.ALL })
    private Set<JoinedSubclassEmployee> members = new HashSet<JoinedSubclassEmployee>();

    @Override
    public int hashCode() {
        return Hashs.compute(super.hashCode(), empNo);
    }

    private static final long serialVersionUID = -7464607704283317130L;
}
