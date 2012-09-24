package mocksy.integration;

import mocksy.Mocksy;
import org.springframework.beans.factory.FactoryBean;

/**
 *
 * <p>
 * User: reesbyars
 * Date: 9/10/12
 * Time: 11:21 PM
 * <p/>
 * SpringMocksyFactoryBean
 */
public class SpringMocksyFactoryBean<C> implements FactoryBean<C> {

    private Class<C> type;
    public SpringMocksyFactoryBean(Class<C> type){
        this.type = type;
    }

    @Override
    public C getObject() throws Exception {
        return Mocksy.newEmptyMock(type);
    }

    @Override
    public Class<C> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
