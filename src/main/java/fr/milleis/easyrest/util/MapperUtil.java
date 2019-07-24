package fr.milleis.easyrest.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.util.ReflectionUtils;

/**
 *
 * @author ajosse
 */
@Slf4j
public class MapperUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T syncFields(Map< String, Object> fields, T target) {
        fields.forEach((String name, Object value) -> {
            var f = ReflectionUtils.findField(target.getClass(), name);
            if (f == null) return;
            f.setAccessible(true);
            ReflectionUtils.setField(f, target, value);
        });
        return target;
    }

    public static <T> T syncObjects(T source, T target) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            Field f = ReflectionUtils.findField(target.getClass(), field.getName());
            if (f == null) continue;
            f.setAccessible(true);
            field.setAccessible(true);
            try {
                var value = field.get(source);
                if (value != null)
                    ReflectionUtils.setField(f, target, value);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(MapperUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return target;
    }

    public static <T> Object getValueField(T t, String name) throws NoSuchFieldException, SecurityException, IllegalAccessException {
        Field field = t.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(t);

    }

    public static <T> T transformLinkedHashMap(LinkedHashMap o) {
        T convertValue = mapper.convertValue(o, TypeUtil.getGenericType());
        return convertValue;
    }

    public static <T> T convertValueToGenericType(Object o, Include include) {
        return mapper.setSerializationInclusion(include).convertValue(o, TypeUtil.getGenericType());
    }

}
