package zzuli.zw.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import zzuli.zw.main.annotation.ParameterName;
import zzuli.zw.domain.User;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.argumentResolver.*;
import zzuli.zw.main.factory.ArgumentResolvers;
import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.request.FriendRequest;

import java.lang.reflect.*;
import java.util.*;

public class RandomUtilTest {
    @Test
    public void test01(){
        System.out.println(RandomUtil.randomString(26));
    }

    @Test
    public void test02() throws JsonProcessingException {
        ResponseMessage responseMessage = new ResponseMessage();
        User user = new User();
        user.setUsername("123");
        responseMessage.setContentObject(user);
        ObjectMapper instance = ObjectMapperFactory.getInstance();
        User user1 = instance.readValue(responseMessage.getContent(), User.class);
        System.out.println(user1);
        System.out.println(responseMessage.getContent());
    }

    @Test
    public void test03(){
        FriendRequest user = new FriendRequest();
        Class<? extends FriendRequest> aClass = user.getClass();
        String name = aClass.getName();
        //System.out.println(name);
        String substring = name.substring(name.lastIndexOf(".")+1);
        //System.out.println(substring.toLowerCase());
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            String name2 = method.getName();
            System.out.println(name2);

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0)continue;
            System.out.println(parameterTypes[0] == RequestParameter.class);
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                String name1 = parameter.getName();
                System.out.println(name1);
            }
        }
    }

    @Test
    public void test04(){
        Class<User> userClass = User.class;
        Method[] methods = userClass.getMethods();
        Map<Object,Object> map = new HashMap<>();
        for (Method method : methods) {
            String name = method.getName();
            if (name.contains("set")){
                System.out.println(name);
                name = name.replace("set","");

                System.out.println(name);
            }
        }
    }

    @Test
    public void baseParamConvert() throws JsonProcessingException {
        Map<Object,Object> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        map.put("friends",list);
        User user = new User();
        user.setUsername("lisi");
        user.setStatus(1);
        user.setSignature("hahha");
        map.put("user",user);
        String s = ObjectMapperFactory.getInstance().writeValueAsString(map);
        Map objectFromString = ObjectMapperFactory.getObjectFromString(s, Map.class);

        Map user1 = (Map) objectFromString.get("user");
        User newUser = new User();
        User user2 = BeanUtil.fillBeanWithMap(user1, newUser, true, true);
        System.out.println(user2);



    }

    @Test
    public void test05() throws JsonProcessingException, InvocationTargetException, IllegalAccessException {
        Map<Object,Object> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        map.put("friends",list);
        User user = new User();
        user.setUsername("lisi");
        user.setStatus(1);
        user.setSignature("hahha");
        map.put("user",user);
        String s = ObjectMapperFactory.getInstance().writeValueAsString(map);
        Map objectFromString = ObjectMapperFactory.getObjectFromString(s, Map.class);
        Method[] methods = TestMethod.class.getMethods();
        for (Method method : methods) {
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {

                /*System.out.println(parameter.getName());
                Class<?> type = parameter.getType();
                if (BeanUtil.isBean(type)){
                    Map map2 = (Map)objectFromString.get(parameter.getAnnotation(ParameterName.class).value());
                    Object o = BeanUtil.fillBeanWithMap(map2, ClassUtil.newObject(type), true, true);
                    method.invoke(new TestMethod(),o,objectFromString.get("friends"),null);
                }*/
            }
        }
        boolean bean = BeanUtil.isBean(List.class);
        System.out.println("isBean--->" + bean);
        List friends = (List)objectFromString.get("friends");
        System.out.println(friends.get(0));
    }

    @Test
    public void test06(){
        Class<?> type = TestMethod.class.getMethods()[0].getParameters()[0].getType();
        String name = type.getName();
        name = name.substring(name.lastIndexOf(".")+1);
        name = name.substring(0,1).toLowerCase().concat(name.substring(1));
        System.out.println(name);
    }
    class TestMethod{
        public ResponseMessage test(@ParameterName("user") User user, List<User> list, Integer id){
            ResponseMessage responseMessage = new ResponseMessage();
            System.out.println("hello");
            //System.out.println(user.getUsername());
            System.out.println(list.get(0));
            return responseMessage;
        }
    }

    @Test
    public void test07() throws NoSuchMethodException, ClassNotFoundException {
        //List<User> list = new ArrayList<>();
        //加入我现在想拿到上面这个list泛型中的实际类型参数，那么先写一个方法，以之为参数
        //下面利用反射拿到list的实际类型参数
        Method m = TestMethod.class.getMethod("test",User.class,List.class,Integer.class);
        //获得泛型类型参数
        Type[] types = m.getGenericParameterTypes();

        //因为只有一个参数，所以我们拿第一个就可以了
        ParameterizedType pt = (ParameterizedType) types[1];
        //获得原始类型
        System.out.println(pt.getRawType());
        //获得实际参数类型,实际参数类型也是一个数组，比如Map<String,String>
        //这里只有一个参数，我们就不遍历了
        System.out.println(pt.getActualTypeArguments()[0]);

        Parameter[] parameters = m.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.getType() == List.class){
                Type parameterizedType = parameter.getParameterizedType();
                System.out.println(parameterizedType.getTypeName());
                String typeName = parameterizedType.getTypeName();
                typeName = typeName.substring(typeName.indexOf("<")+1).replace(">","");
                Class<?> aClass = Class.forName(typeName);
                System.out.println(aClass);
            }
        }
    }

    @Test
    public void test08() throws Exception {
        RequestParameter request = new RequestParameter();
        Map map = new HashMap();
        List<Map> list = new ArrayList<>();
        map.put("username","lisi");
        map.put("id",1);
        list.add(map);
        Map beanMap = new HashMap();
        beanMap.put("list",list);
        request.setRequestData(beanMap);
        Method m = TestMethod.class.getMethod("test",User.class,List.class,Integer.class);
        Parameter parameter = m.getParameters()[1];
        ListParameterResolver listParameterResolver = new ListParameterResolver();
        if (listParameterResolver.supportsParameter(parameter)){
            m.invoke(new TestMethod(),null,listParameterResolver.resolveArgument(parameter,request,null,null),null);
        }

    }

    @Test
    public void test09(){
        ArgumentResolvers.addResolver(new BeanParameterResolver())
                .addResolver(new SimpleParameterResolver())
                .addResolver(new RequestParameterResolver())
                .addResolver(new ResponseParameterResolver())
                .addResolver(new ListParameterResolver());
    }


}
