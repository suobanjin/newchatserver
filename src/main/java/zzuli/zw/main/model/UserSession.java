package zzuli.zw.main.model;

import zzuli.zw.main.interfaces.Session;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserSession implements Session {
    private final Map<String,Object> attributes;
    private String id;
    private long creatorTime;

    public UserSession(String id) {
        this.attributes = new LinkedHashMap<>();
        this.id = id;
        this.creatorTime = System.currentTimeMillis();
    }

    @Override
    public boolean setAttribute(String id, Object o) {
        if (id == null){
            return false;
        }
        return attributes.put(id,o) != null;
    }

    @Override
    public Object getAttribute(String id) {
        if (id == null)return null;
        return attributes.get(id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void removeAttribute(String id) {
        if (!attributes.containsKey(id))return;
        attributes.remove(id);
    }

    @Override
    public long getCreatorTime() {
        return this.creatorTime;
    }

    @Override
    public List<Object> getAttributes() {
        if (attributes.isEmpty())return null;
        List<Object> list = new ArrayList<>();
        list.addAll(attributes.values());
        return list;
    }

    @Override
    public List<String> getAttributeNames() {
        if (attributes.isEmpty())return null;
        List<String> list = new ArrayList<>();
        list.addAll(attributes.keySet());
        return list;
    }
}
