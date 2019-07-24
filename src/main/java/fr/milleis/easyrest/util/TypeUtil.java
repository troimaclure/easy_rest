package fr.milleis.easyrest.util;

import java.util.ArrayList;

/**
 *
 * @author ajosse
 */
public class TypeUtil {

    public static <T> Class<T> getGenericType() {
        return (Class<T>) Object.class;
    }

    public static <T> Class<? extends Object[]> getArrayGenericType() {
        return ((T[]) new ArrayList<>().toArray()).getClass();
    }
}
