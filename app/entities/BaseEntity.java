package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseEntity {

    private static Map<String, AtomicInteger> objectsCountsMap = new HashMap<>();

    private int id;

    protected BaseEntity() {
        BaseEntity.objectsCountsMap.putIfAbsent(
                this.getClass().getName(),
                new AtomicInteger(0)
        );
        this.setId(objectsCountsMap.get(this.getClass().getName()).incrementAndGet());
    }

    protected BaseEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
