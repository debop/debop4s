package debop4s.core.reflect;

import debop4s.core.Guard;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

/**
 * 객체의 생성자에 접근하여, 생성자를 생성할 수 있도록 합니다.
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 13. 1. 21
 */
@Slf4j
@SuppressWarnings("all")
public abstract class ConstructorAccess<T> {

    @Getter
    boolean nonStaticMemberClass;

    /** Constructor for top-level classes and static nested classes. */
    public abstract T newInstance();

    /** Constructor for inner classes (non-static nested classes) - except static nested classes */
    public abstract T newInstance(final Object enclosingInstance);

    private static final Object syncLock = new Object();

    /** 지정한 수형의 생성자에 대한 접근자를 생성합니다. */
    @SuppressWarnings("unchecked")
    public static <T> ConstructorAccess<T> get(final Class<T> type) {
        assert (type != null);
        log.trace("수형[{}]의 생성자에 대한 접근자를 조회합니다.", type.getName());

        Class enclosingType = type.getEnclosingClass();
        boolean isNonStaticMemberClass = enclosingType != null && type.isMemberClass() && !Modifier.isStatic(type.getModifiers());

        String className = type.getName();
        String accessClassName = className + "ConstructorAccess";
        if (accessClassName.startsWith("java."))
            accessClassName = ReflectConsts.BASE_PACKAGE + "." + accessClassName;

        Class accessClass = null;
        AccessClassLoader loader = AccessClassLoader.get(type);
        Guard.shouldNotBeNull(loader, "loader");

        synchronized (syncLock) {
            try {
                accessClass = loader.loadClass(accessClassName);
            } catch (ClassNotFoundException ignored) {
                String accessClassNameInternal = accessClassName.replace('.', '/');
                String classNameInternal = className.replace('.', '/');
                String enclosingClassNameInternal;

                if (!isNonStaticMemberClass) {
                    enclosingClassNameInternal = null;
                    try {
                        type.getConstructor((Class[]) null);
                    } catch (Exception ex) {
                        throw new RuntimeException("[" + type.getName() + "] 을 생성하지 못합니다. 기본 생성자를 찾지 못했습니다.", ex);
                    }
                } else {
                    enclosingClassNameInternal = enclosingType.getName().replace('.', '/');
                    try {
                        type.getConstructor(enclosingType); // Inner classes should have this.
                    } catch (Exception ex) {
                        throw new RuntimeException("Non-static member class 를 생성할 수 없습니다. 기본 생성자가 없습니다. kind=" + type.getName(), ex);
                    }
                }

                ClassWriter cw = new ClassWriter(0);
                cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null, ReflectConsts.CONSTRUCTOR_ACCESS_PATH, null);

                insertConstructor(cw);
                insertNewInstance(cw, classNameInternal);
                insertNewInstanceInner(cw, classNameInternal, enclosingClassNameInternal);

                cw.visitEnd();
                accessClass = loader.defineClass(accessClassName, cw.toByteArray());
            }
        }

        try {
            ConstructorAccess<T> access = (ConstructorAccess<T>) accessClass.newInstance();
            access.nonStaticMemberClass = isNonStaticMemberClass;

            log.trace("기본 생성자에 접근 가능한 ConstructorAccess 를 반환합니다. accessClassName=[{}]", accessClassName);
            return access;
        } catch (Exception ex) {
            throw new RuntimeException("Error constructing constructor access class: [" + accessClassName + "]", ex);
        }
    }

    /** 생성자를 추가한다. */
    static private void insertConstructor(final ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, ReflectConsts.CONSTRUCTOR_ACCESS_PATH, "<init>", "()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    /** 새로운 인스턴스를 추가합니다. */
    static void insertNewInstance(final ClassWriter cw, final String classNameInternal) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, classNameInternal);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "()V");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    /** inner 인스턴스를 추가한다 */
    static void insertNewInstanceInner(final ClassWriter cw, final String classNameInternal, final String enclosingClassNameInternal) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();
        if (enclosingClassNameInternal != null) {
            mv.visitTypeInsn(NEW, classNameInternal);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, enclosingClassNameInternal);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
            mv.visitInsn(POP);
            mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "(L" + enclosingClassNameInternal + ";)V");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(4, 2);
        } else {
            mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("Not an inner class.");
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V");
            mv.visitInsn(ATHROW);
            mv.visitMaxs(3, 2);
        }
        mv.visitEnd();
    }
}
