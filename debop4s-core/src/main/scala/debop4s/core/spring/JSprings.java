package debop4s.core.spring;

import debop4s.core.AutoCloseableAction;
import debop4s.core.utils.Local;
import debop4s.core.tools.ArrayTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import static debop4s.core.Guard.*;

/**
 * JSprings
 *
 * @author sunghyouk.bae@gmail.com
 */
@Slf4j
public final class JSprings {

    private JSprings() {}

    /**
     * Spring에서 Static 변수를 초기화하는데 사용됩니다.
     *
     * @param context the context
     */
    @Autowired
    protected JSprings(final ApplicationContext context) {
        log.info("ApplicationContext 가 Injection 되었습니다.");
        globalContext = context;
    }

    public static final String DEFAULT_APPLICATION_CONTEXT_XML = "applicationContext.xml";

    private static final String LOCAL_SPRING_CONTEXT = JSprings.class.getName() + ".globalContext";
    private static final String NOT_INITIALIZED_MSG =
            "Springs의 ApplicationContext가 초기화되지 않았습니다. Springs를 ComponentScan 해주셔야합니다!!!";

    private static volatile ApplicationContext globalContext;
    private static ThreadLocal<Stack<GenericApplicationContext>> localContextStack =
            new ThreadLocal<Stack<GenericApplicationContext>>();

    /**
     * Spring ApplicationContext 가 초기화 되었으면 true를 반환한다.
     *
     * @return the boolean
     */
    public static synchronized boolean isInitialized() {
        return (globalContext != null);
    }

    /**
     * Spring ApplicationContext 가 초기화 되지 않았으면 true를 반환한다.
     *
     * @return the boolean
     */
    public static synchronized boolean isNotInitialized() {
        return (globalContext == null);
    }

    private static synchronized void assertInitialized() {
        shouldBe(isInitialized(), NOT_INITIALIZED_MSG);
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public static synchronized GenericApplicationContext getContext() {
        ApplicationContext context = getLocalContext();
        if (context == null)
            context = globalContext;
        shouldBe(context != null, NOT_INITIALIZED_MSG);

        return (GenericApplicationContext) context;
    }

    private static synchronized GenericApplicationContext getLocalContext() {
        if (getLocalContextStack().size() == 0)
            return null;
        return getLocalContextStack().peek();
    }

    private static synchronized Stack<GenericApplicationContext> getLocalContextStack() {
        if (localContextStack.get() == null) {
            localContextStack.set(new Stack<GenericApplicationContext>());
        }
        return localContextStack.get();
    }

    /** 초기화를 합니다. */
    public static synchronized void init() {
        init(DEFAULT_APPLICATION_CONTEXT_XML);
    }

    /**
     * 초기화를 합니다.
     *
     * @param resourceLocations the resource locations
     */
    public static synchronized void init(final String... resourceLocations) {
        init(new GenericXmlApplicationContext(resourceLocations));
    }

    /**
     * 초기화를 합니다.
     *
     * @param applicationContext the application context
     */
    public static synchronized void init(final ApplicationContext applicationContext) {
        shouldNotBeNull(applicationContext, "applicationContext");
        log.info("Springs ApplicationContext 를 초기화 작업을 시작합니다...");

        if (globalContext != null) {
            log.info("Springs ApplicationContext가 이미 초기화 되었으므로, 무시합니다. reset 후 init 을 호출하세요.");
        }

        globalContext = applicationContext;
        log.info("Springs ApplicationContext를 초기화 작업을 완료했습니다.");
    }

    /**
     * Init by annotated classes.
     *
     * @param annotatedClasses the annotated classes
     */
    public static synchronized void initByAnnotatedClasses(final Class<?>... annotatedClasses) {
        init(new AnnotationConfigApplicationContext(annotatedClasses));
    }

    /**
     * Init by packages.
     *
     * @param basePackages the debop4s.redis.base packages
     */
    public static synchronized void initByPackages(final String... basePackages) {
        init(new AnnotationConfigApplicationContext(basePackages));
    }


    /**
     * Use local context.
     *
     * @param localContext the local context
     * @return the auto closeable action
     */
    public static synchronized AutoCloseableAction useLocalContext(final GenericApplicationContext localContext) {
        shouldNotBeNull(localContext, "localContext");

        log.debug("로컬 컨텍스트를 사용하려고 합니다... localContext=[{}]", localContext);

        getLocalContextStack().push(localContext);
        return new AutoCloseableAction(new Runnable() {
            @Override
            public void run() {
                reset(localContext);
            }
        });
    }

    /**
     * 지정된 ApplicationContext 를 초기화합니다.
     *
     * @param contextToReset 초기화 시킬 ApplicationContext
     */
    public static synchronized void reset(final ApplicationContext contextToReset) {

        if (contextToReset == null) {
            globalContext = null;
            log.info("Global Springs Context 를 Reset 했습니다!!!");
            return;
        }


        log.debug("ApplicationContext=[{}] 을 Reset 합니다...", contextToReset);

        if (getLocalContext() == contextToReset) {
            getLocalContextStack().pop();

            if (getLocalContextStack().size() == 0)
                Local.put(LOCAL_SPRING_CONTEXT, null);

            log.info("Local Application Context 를 Reset 했습니다.");
            return;
        }

        if (globalContext == contextToReset) {
            globalContext = null;
            log.info("Global Application Context 를 Reset 했습니다!!!");
        }
    }

    /** Springs ApplicationContext를 초기화합니다. */
    public static synchronized void reset() {
        if (getLocalContext() != null)
            reset(getLocalContext());
        else
            reset(globalContext);
    }


    public static synchronized Object getBean(final String name) {
        assertInitialized();

        log.debug("ApplicationContext로부터 Bean을 가져옵니다. name=[{}]", name);

        return getContext().getBean(name);
    }

    public static synchronized Object getBean(final String name,
                                              final Object... args) {
        assertInitialized();

        log.debug("ApplicationContext로부터 Bean을 가져옵니다. name=[{}], args=[{}]", name, args);

        return getContext().getBean(name, args);
    }

    public static synchronized <T> T getBean(final Class<T> beanClass) {
        assertInitialized();

        log.debug("ApplicationContext로부터 Bean을 가져옵니다. beanClass=[{}]", beanClass.getName());
        return getContext().getBean(beanClass);
    }

    public static synchronized <T> T getBean(final String name, final Class<T> beanClass) {
        assertInitialized();

        log.debug("ApplicationContext로부터 Bean을 가져옵니다. beanName=[{}], beanClass=[{}]", name, beanClass);

        return getContext().getBean(name, beanClass);
    }

    public static synchronized <T> String[] getBeanNamesForType(final Class<T> beanClass) {
        return getBeanNamesForType(beanClass, true, true);
    }

    public static synchronized <T> String[] getBeanNamesForType(final Class<T> beanClass,
                                                                final boolean includeNonSingletons,
                                                                final boolean allowEagerInit) {
        shouldNotBeNull(beanClass, "beanClass");

        log.debug("해당 수형의 모든 Bean의 이름을 조회합니다. beanClass=[{}], includeNonSingletons=[{}], allowEagerInit=[{}]",
                  beanClass.getName(), includeNonSingletons, allowEagerInit);

        return getContext().getBeanNamesForType(beanClass, includeNonSingletons, allowEagerInit);
    }

    /** 지정한 타입의 Bean 들의 인스턴스를 가져옵니다. (Prototype Bean 도 포함됩니다.) */
    public static <T> List<T> getBeansByType(final Class<T> beanClass) {
        return getBeansByType(beanClass, true, true);
    }

    public static <T> List<T> getBeansByType(final Class<T> beanClass,
                                             final boolean includeNonSingletons,
                                             final boolean allowEagerInit) {
        Map<String, T> beanMap = getBeansOfType(beanClass, includeNonSingletons, allowEagerInit);
        return ArrayTool.toList(beanMap.values());
    }

    public static <T> T getFirstBeanByType(final Class<T> beanClass) {
        return getFirstBeanByType(beanClass, true, true);
    }

    public static <T> T getFirstBeanByType(final Class<T> beanClass,
                                           final boolean includeNonSingletons,
                                           final boolean allowEagerInit) {
        List<T> beans = getBeansByType(beanClass, includeNonSingletons, allowEagerInit);
        if (beans != null && beans.size() > 0)
            return beans.get(0);
        else
            return null;
    }

    /**
     * 지정된 수형 또는 상속한 수형으로 등록된 bean 들을 조회합니다.
     *
     * @param beanClass Bean의 수형
     */
    public static synchronized <T> Map<String, T> getBeansOfType(final Class<T> beanClass) {
        return getBeansOfType(beanClass, true, true);
    }

    /**
     * 지정된 수형 또는 상속한 수형으로 등록된 bean 들을 조회합니다.
     *
     * @param beanClass            Bean 수형
     * @param includeNonSingletons Singleton 타입의 Bean 이 아닌 경우도 포함
     * @param allowEagerInit       미리 초기화를 수행할 것인가?
     */
    public static synchronized <T> Map<String, T> getBeansOfType(final Class<T> beanClass,
                                                                 final boolean includeNonSingletons,
                                                                 final boolean allowEagerInit) {
        assert beanClass != null;

        log.debug("해당 수형의 모든 Bean을 조회합니다. beanClass=[{}], includeNonSingletons=[{}], allowEagerInit=[{}]",
                  beanClass.getName(), includeNonSingletons, allowEagerInit);

        return getContext().getBeansOfType(beanClass,
                                           includeNonSingletons,
                                           allowEagerInit);
    }

    /**
     * 지정된 수형의 Bean 을 조회합니다. 등록되지 않았으면 등록하고 반환합니다.
     *
     * @param beanClass Bean 수형
     * @return Bean 인스턴스
     */
    public static synchronized <T> T getOrRegisterBean(final Class<T> beanClass) {
        return getOrRegisterBean(beanClass, ConfigurableBeanFactory.SCOPE_SINGLETON);
    }

    /**
     * 지정된 수형의 Bean 을 조회합니다. 등록되지 않았으면 등록하고 반환합니다.
     *
     * @param beanClass Bean 수형
     * @param scope     scope ( singleton, prototype )
     * @return Bean 인스턴스
     */
    public static synchronized <T> T getOrRegisterBean(final Class<T> beanClass,
                                                       final String scope) {
        return getOrRegisterBean(beanClass, beanClass, scope);
    }

    /**
     * 지정된 수형의 Bean 을 조회합니다. 등록되지 않았으면 등록하고 반환합니다.
     *
     * @param beanClass       Bean 수형
     * @param registBeanClass 등록되지 않은 beanClass 일때, 실제 등록할 Bean의 수형 (Concrete Class)
     * @return Bean 인스턴스
     */
    public static synchronized <T> T getOrRegisterBean(final Class<T> beanClass,
                                                       final Class<? extends T> registBeanClass) {
        return getOrRegisterBean(beanClass, registBeanClass, ConfigurableBeanFactory.SCOPE_SINGLETON);
    }

    /**
     * 등록된 beanClass 를 조회 (보통 Interface) 하고, 없다면, registerBeanClass (Concrete Class) 를 등록합니다.
     *
     * @param beanClass       조회할 Bean의 수형 (보통 인터페이스)
     * @param registBeanClass 등록되지 않은 beanClass 일때, 실제 등록할 Bean의 수형 (Concrete Class)
     * @param scope           "singleton", "prototype"
     * @param <T>             Bean의 수형
     * @return 등록된 Bean의 인스턴스
     */
    public static synchronized <T> T getOrRegisterBean(final Class<T> beanClass,
                                                       final Class<? extends T> registBeanClass,
                                                       final String scope) {
        T bean = getFirstBeanByType(beanClass, true, true);
        if (bean != null)
            return bean;

        registerBean(registBeanClass.getName(), registBeanClass, scope);
        return getContext().getBean(registBeanClass);
    }

    /**
     * Singleton Bean을 가져옵니다. 없으면 새로 등록하고 Bean을 반환합니다.
     *
     * @param beanClass Bean의 수형
     * @param <T>       Bean의 수형
     * @return Bean 인스턴스
     */
    public static synchronized <T> T getOrRegisterSingletonBean(final Class<T> beanClass) {
        return getOrRegisterBean(beanClass, ConfigurableBeanFactory.SCOPE_SINGLETON);
    }

    /**
     * Prototype Bean을 가져옵니다. 없으면 새로 등록하고 Bean을 반환합니다.
     *
     * @param beanClass Bean의 수형
     * @param <T>       Bean의 수형
     * @return Bean 인스턴스
     */
    public static synchronized <T> T getOrRegisterPrototypeBean(final Class<T> beanClass) {
        return getOrRegisterBean(beanClass, ConfigurableBeanFactory.SCOPE_PROTOTYPE);
    }

    /**
     * 지정된 Bean 이름이 사용되었는가?
     *
     * @param beanName Bean 이름
     * @return 사용 여부
     */
    public static synchronized boolean isBeanNameInUse(final String beanName) {
        return getContext().isBeanNameInUse(beanName);
    }

    /**
     * 지정된 Bean 이름이 현재 Context에 등록되었는가?
     *
     * @param beanName Bean 이름
     * @return 사용 여부
     */
    public static synchronized boolean isRegisteredBean(final String beanName) {
        return getContext().isBeanNameInUse(beanName);
    }

    /**
     * 지정한 수형의 Bean이 등록되었는지 여부를 반환한다.
     *
     * @param beanClass the bean class
     * @return the boolean
     */
    public static synchronized <T> boolean isRegisteredBean(final Class<T> beanClass) {
        assert beanClass != null;
        try {
            return (getContext().getBean(beanClass) != null);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Register bean.
     *
     * @param beanName  the bean name
     * @param beanClass the bean class
     * @return the boolean
     */
    public static synchronized <T> boolean registerBean(final String beanName,
                                                        final Class<T> beanClass) {
        return registerBean(beanName, beanClass, ConfigurableBeanFactory.SCOPE_SINGLETON);
    }

    /**
     * Register bean.
     *
     * @param beanName       the bean name
     * @param beanClass      the bean class
     * @param scope          the scope
     * @param propertyValues the property values
     * @return the boolean
     */
    public static synchronized <T> boolean registerBean(final String beanName,
                                                        final Class<T> beanClass,
                                                        final String scope,
                                                        final PropertyValue... propertyValues) {
        assert beanClass != null;
        BeanDefinition definition = new RootBeanDefinition(beanClass);
        definition.setScope(scope);

        for (PropertyValue pv : propertyValues) {
            definition.getPropertyValues().addPropertyValue(pv);
        }
        return registerBean(beanName, definition);
    }

    /**
     * Register bean.
     *
     * @param beanName       the bean name
     * @param beanDefinition the bean definition
     * @return the boolean
     */
    public static synchronized boolean registerBean(final String beanName,
                                                    final BeanDefinition beanDefinition) {
        shouldNotBeEmpty(beanName, "beanName");
        shouldNotBeNull(beanDefinition, "beanDefinition");

        if (isBeanNameInUse(beanName))
            throw new BeanDefinitionValidationException("이미 등록된 Bean입니다. beanName=" + beanName);

        log.info("새로운 Bean을 등록합니다. beanName=[{}], beanDefinition=[{}]", beanName, beanDefinition);

        try {
            getContext().registerBeanDefinition(beanName, beanDefinition);
            return true;
        } catch (Exception e) {
            log.error("새로운 Bean 등록에 실패했습니다. beanName=" + beanName, e);
        }
        return false;
    }

    /**
     * Register bean.
     *
     * @param beanName the bean name
     * @param instance the instance
     * @return the boolean
     */
    public static synchronized boolean registerBean(final String beanName,
                                                    final Object instance) {
        shouldNotBeEmpty(beanName, "beanName");

        try {
            getContext().getBeanFactory().registerSingleton(beanName, instance);
            return true;
        } catch (Exception e) {
            log.error("인스턴스를 빈으로 등록하는데 실패했습니다. beanName=" + beanName, e);
            return false;
        }
    }

    /**
     * Register singleton bean.
     *
     * @param beanName the bean name
     * @param instance the instance
     * @return the boolean
     */
    public static synchronized boolean registerSingletonBean(final String beanName,
                                                             final Object instance) {
        return registerBean(beanName, instance);
    }

    /**
     * Register singleton bean.
     *
     * @param beanClass the bean class
     * @param pvs       the pvs
     * @return the boolean
     */
    public static synchronized <T> boolean registerSingletonBean(final Class<T> beanClass,
                                                                 final PropertyValue... pvs) {
        assert beanClass != null;
        return registerSingletonBean(beanClass.getName(), beanClass, pvs);
    }

    /**
     * Register singleton bean.
     *
     * @param beanName  the bean name
     * @param beanClass the bean class
     * @param pvs       the pvs
     * @return the boolean
     */
    public static synchronized <T> boolean registerSingletonBean(final String beanName,
                                                                 final Class<T> beanClass,
                                                                 final PropertyValue... pvs) {
        return registerBean(beanName, beanClass, ConfigurableBeanFactory.SCOPE_SINGLETON, pvs);
    }

    /**
     * Register prototype bean.
     *
     * @param beanClass the bean class
     * @param pvs       the pvs
     * @return the boolean
     */
    public static synchronized <T> boolean registerPrototypeBean(final Class<T> beanClass,
                                                                 final PropertyValue... pvs) {
        shouldNotBeNull(beanClass, "beanClass");
        return registerPrototypeBean(beanClass.getName(), beanClass, pvs);
    }

    /**
     * Register prototype bean.
     *
     * @param beanName  the bean name
     * @param beanClass the bean class
     * @param pvs       the pvs
     * @return the boolean
     */
    public static synchronized <T> boolean registerPrototypeBean(final String beanName,
                                                                 final Class<T> beanClass,
                                                                 final PropertyValue... pvs) {
        return registerBean(beanName, beanClass, ConfigurableBeanFactory.SCOPE_PROTOTYPE, pvs);
    }

    /**
     * Remove bean.
     *
     * @param beanName the bean name
     */
    public static synchronized void removeBean(final String beanName) {
        shouldNotBeEmpty(beanName, "beanName");

        if (isBeanNameInUse(beanName)) {

            log.debug("ApplicationContext에서 name=[{}]인 Bean을 제거합니다.", beanName);
            getContext().removeBeanDefinition(beanName);
        }
    }

    /**
     * Remove bean.
     *
     * @param beanClass the bean class
     */
    public static synchronized <T> void removeBean(final Class<T> beanClass) {
        shouldNotBeNull(beanClass, "beanClass");

        log.debug("Bean 형식 [{}]의 모든 Bean을 ApplicationContext에서 제거합니다.", beanClass.getName());

        String[] beanNames = getContext().getBeanNamesForType(beanClass, true, true);
        for (String beanName : beanNames)
            removeBean(beanName);
    }

    /**
     * Try getMulti bean.
     *
     * @param beanName the bean name
     * @return bean instance.
     */
    public static synchronized Object tryGetBean(final String beanName) {
        shouldNotBeEmpty(beanName, "beanName");
        try {
            return getBean(beanName);
        } catch (Exception e) {
            log.warn("bean을 찾는데 실패했습니다. null을 반환합니다. beanName=" + beanName, e);
            return null;
        }
    }

    /**
     * Try getMulti bean.
     *
     * @param beanName the bean name
     * @param args     the args
     * @return bean instance
     */
    public static synchronized Object tryGetBean(final String beanName,
                                                 final Object... args) {
        shouldNotBeEmpty(beanName, "beanName");
        try {
            return getBean(beanName, args);
        } catch (Exception e) {
            log.warn("bean을 찾는데 실패했습니다. null을 반환합니다. beanName=" + beanName, e);
            return null;
        }
    }

    /**
     * Try getMulti bean.
     *
     * @param beanClass the bean kind
     * @return Bean instance
     */
    public static synchronized <T> T tryGetBean(final Class<T> beanClass) {
        shouldNotBeNull(beanClass, "beanClass");
        try {
            return getBean(beanClass);
        } catch (Exception e) {
            log.warn("bean을 찾는데 실패했습니다. null을 반환합니다. beanClass=" + beanClass.getName(), e);
            return null;
        }
    }

    /**
     * Try Get Bean
     *
     * @param beanName  bean name
     * @param beanClass bean kind
     * @return Bean instance
     */
    public static synchronized <T> T tryGetBean(final String beanName,
                                                final Class<T> beanClass) {
        shouldNotBeNull(beanClass, "beanClass");
        try {
            return getBean(beanName, beanClass);
        } catch (Exception e) {
            log.warn("bean을 찾는데 실패했습니다. null을 반환합니다. beanName=" + beanName, e);
            return null;
        }
    }
}
