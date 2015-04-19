package debop4s.data.orm.model;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;

import javax.persistence.Transient;

/**
 * 영구 저장소에 저장 여부를 나타내는 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 1:04
 */
abstract public class PersistentObjectBase extends ValueObjectBase implements PersistentObject {

    /** 영구 저장된 엔티티인가 여부 */
    @Transient
    private boolean persisted = false;

    @Override
    public boolean isPersisted() {
        return persisted;
    }

    protected void setPersisted(boolean v) {
        this.persisted = v;
    }

    @Override
    public void onSave() {
        persisted = true;
    }

    @Override
    public void onPersist() {
        persisted = true;
    }

    @Override
    public void onLoad() {
        persisted = true;
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("persisted", persisted);
    }

    private static final long serialVersionUID = -7066710546641101707L;
}
