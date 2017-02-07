package com.zhukai.spring.integration.proxy.cglib;

import com.zhukai.spring.integration.commons.annotation.Transactional;
import com.zhukai.spring.integration.context.WebContext;
import com.zhukai.spring.integration.jdbc.DBConnectionPool;
import com.zhukai.spring.integration.logger.Logger;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by zhukai on 17-1-22.
 */
public class CommonProxy implements MethodInterceptor {

    //创建一个clazz的继承类，额外增加intercept()方法，该clazz可以不是接口的实现类
    public <T> T getProxyInstance(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Connection connection = null;
        if (method.isAnnotationPresent(Transactional.class)) {
            Logger.info("Transactional begin...");
            connection = DBConnectionPool.getConnection();
            connection.setAutoCommit(false);
            WebContext.setTransaction(connection);
        }
        Object result = proxy.invokeSuper(obj, args);
        if (connection != null) {
            try {
                connection.commit();
                Logger.info("Transactional over...");
            } catch (SQLException e) {
                connection.rollback();
                Logger.error("Transactional rollback...");
                Logger.error();
                e.printStackTrace();
            } finally {
                DBConnectionPool.freeConnection(connection);
            }
        }
        return result;
    }

}
