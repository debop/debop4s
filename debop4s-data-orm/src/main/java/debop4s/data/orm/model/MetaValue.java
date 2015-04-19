package debop4s.data.orm.model;

/**
 * 메타 정보를 표현하는 인터페이스
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 8. 오후 1:08
 */
public interface MetaValue {

    /** 메타 값 */
    String getValue();

    void setValue(String value);

    /** 메타 값의 라벨 */
    String getLabel();

    void setLabel(final String label);

    /** 메타 값의 설명 */
    String getDescription();

    void setDescription(final String description);

    /** 메타 정보의 추가 속성 */
    String getExAttr();

    void setExAttr(final String exAttr);
}
