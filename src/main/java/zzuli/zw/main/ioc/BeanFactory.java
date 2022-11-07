package zzuli.zw.main.ioc;

import zzuli.zw.main.ioc.interfaces.ObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 索半斤
 * @description 对象工厂
 * @date 2022/2/9
 * @className BeanFactory
 */
public class BeanFactory implements ObjectFactory {
    private Map<String,Object> beansByName = new ConcurrentHashMap<>();
    private Map<Class<?>,Object> beansByClass = new ConcurrentHashMap<>();

    @Override
    public void init(String objectName,Object object) {
        beansByName.put(objectName,object);
        beansByClass.put(object.getClass(),object);
    }

    @Override
    public Object getBean(String objectName) {
        if (beansByName.size() == 0)return null;
        return beansByName.get(objectName);
    }

    @Override
    public Object getBean(Class<?> objectType) {
        if (beansByClass.size() == 0)return null;
        return beansByClass.get(objectType);
    }

    @Override
    public void release() {
        if (beansByClass.size() != 0)beansByClass.clear();
        if (beansByName.size() != 0)beansByName.clear();
    }

    public List<Object> getBeans(){
        List<Object> objectList = new ArrayList<>();
        beansByName.forEach((key,value)->{
            objectList.add(value);
        });
        return objectList;
    }
}
