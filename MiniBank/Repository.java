import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import model.annotation.Id;

public class Repository<T> {
    private final HashMap<String, T> items = new HashMap<>();

    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null.");
        }
        items.put(getId(entity), entity);
        return entity;
    }

    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) {
            save(item);
        }
    }

    public T findById(String id) {
        return items.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(items.values());
    }

    public boolean delete(String id) {
        return items.remove(id) != null;
    }

    public static <N extends Number> double average(N[] values) {
        if (values.length == 0) {
            return 0.0;
        }
        double sum = 0;
        for (N value : values) {
            sum += value.doubleValue();
        }
        return sum / values.length;
    }

    private String getId(T entity) {
        Class<?> type = entity.getClass();
        while (type != null) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        Object id = field.get(entity);
                        if (!(id instanceof String)) {
                            throw new IllegalArgumentException("@Id field must contain a String.");
                        }
                        return (String) id;
                    } catch (IllegalAccessException exception) {
                        throw new IllegalStateException("Unable to read @Id field.", exception);
                    }
                }
            }
            type = type.getSuperclass();
        }
        throw new IllegalArgumentException("Entity has no field marked with @Id.");
    }
}
