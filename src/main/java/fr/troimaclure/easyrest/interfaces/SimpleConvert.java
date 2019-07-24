package fr.troimaclure.easyrest.interfaces;

/**
 *
 * @author ajosse
 * @param <T>
 * @param <K>
 */
public interface SimpleConvert<T, K> {

    T dto(K k);

    T dtoWithNested(K k);

    K entity(T t);

    K entityWithNested(T t);
}
