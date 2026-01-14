package com.minjor.web.model.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;


/**
 * 可序列化的单键值对Map
 * @param <K>
 * @param <V>
 */
public class KeyValue<K, V> extends HashMap<K, V> implements Serializable {
    
    public KeyValue() {
        super(1);
    }
    
    public KeyValue(K key, V value) {
        this();
        set(key, value);
    }
    
    public void set(K key, V value) {
        super.clear();
        super.put(key, value);
    }
    
    // 可选：重写序列化方法以保证单条目特性
    @Serial
    private void writeObject(java.io.ObjectOutputStream out)
            throws java.io.IOException {
        // 默认序列化
        out.defaultWriteObject();
    }
    
    @Serial
    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        // 默认反序列化
        in.defaultReadObject();
        // 读取后可以添加验证逻辑
        if (size() > 1) {
            throw new IllegalStateException("SingleEntryMap should have only one entry after deserialization");
        }
    }
}