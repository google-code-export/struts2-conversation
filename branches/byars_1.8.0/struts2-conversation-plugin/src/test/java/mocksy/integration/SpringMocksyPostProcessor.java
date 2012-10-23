package mocksy.integration;

import mocksy.Mocksy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.StringUtils;

/**
 * The Mocksy interface to the Spring IoC layer.  Packages and classes can be declared for automatic
 * mockifying of Spring-managed beans (makes them ready for using {@link mocksy.Mocksy#delegate(Object)},
 * lazy-initialization of beans, and auto-empty-mocking of Spring beans (useful when
 * all you have available is an interface).
 * <p/>
 * Example usage:
 * <pre>
 * &lt;bean class=&quot;mocksy.SpringMocksyPostProcessor&quot;&gt;
 *    &lt;property name=&quot;proxyPackages&quot; value=&quot;orb.byars&quot;/&gt;
 *    &lt;property name=&quot;lazyInitPackages&quot; value=&quot;org.byars.foo, org.byars.bar&quot;/&gt;
 *    &lt;property name=&quot;emptyMockClasses&quot; value=&quot;org.byars.SomeService, org.byars.SomeOtherService&quot;/&gt;
 * &lt;/bean&gt;
 * </pre>
 * <p/>
 * User: reesbyars
 * Date: 9/11/12
 * Time: 5:16 PM
 * <p/>
 * SpringMocksyPostProcessor
 */
public class SpringMocksyPostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(SpringMocksyPostProcessor.class);

    String[] mockifyPackages = {"NONE"};
    String[] lazyInitPackages = {"NONE"};
    String[] emptyMockClasses = {};

    public void setMockifyPackages(String mockifyPackages) {
        this.mockifyPackages = StringUtils.trimAllWhitespace(mockifyPackages).split(",");
    }

    public void setLazyInitPackages(String lazyInitPackages) {
        this.lazyInitPackages = StringUtils.trimAllWhitespace(lazyInitPackages).split(",");
    }

    public void setEmptyMockClasses(String emptyMockClasses) {
        this.emptyMockClasses = StringUtils.trimAllWhitespace(emptyMockClasses).split(",");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        boolean doProxy = false;
        for (String pack : mockifyPackages) {
            if (bean.getClass().getName().startsWith(pack) && !beanName.endsWith("Test")) {
                doProxy = true;
                break;
            }
        }
        Object proxy = bean;
        try {
            if (doProxy) {
                proxy = Mocksy.mockify(proxy);
            }
        } catch (Exception e) {
            //nuthin
        }
        return proxy;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String bean : beanFactory.getBeanDefinitionNames()){
            BeanDefinition definition = beanFactory.getBeanDefinition(bean);
            String beanClassName = definition.getBeanClassName();
            for (String pack : this.lazyInitPackages) {
                if (beanClassName.startsWith(pack) && !beanClassName.endsWith("Test")) {
                    definition.setLazyInit(true);
                }
            }
        }
        for (String className : this.emptyMockClasses) {
            try {
                Class<?> beanClass = Class.forName(className);
                FactoryBean<?> factoryBean = SpringMocksyFactoryBean.getFor(beanClass);
                String name = className + "$MOCKSY_PROXY$";
                beanFactory.registerSingleton(name, factoryBean);
            } catch (ClassNotFoundException e) {
                LOG.error("Could not obtain class " + className + ".  Empty mocking cannot be performed.  Message:  " + e.getMessage());
            }
        }

    }

    @Override
    public void destroy() throws Exception {
        Mocksy.cleanup();
        LOG.info("Completed cleaning up Mocksy resources.");
    }
}
