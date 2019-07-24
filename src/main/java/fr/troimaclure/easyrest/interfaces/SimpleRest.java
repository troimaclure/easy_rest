package fr.troimaclure.easyrest.interfaces;

import fr.troimaclure.easyrest.exception.WSException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ajosse
 * @param <T>
 */
public interface SimpleRest<T> {

    /**
     *
     * @param id
     * @return
     * @throws WSException
     */
    public T get(int id) throws WSException;

    /**
     *
     * @return
     *
     * @throws WSException
     */
    public List<T> get() throws WSException;

    /**
     *
     * @param t
     * @return
     * @throws WSException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchFieldException
     */
    public T post(T t) throws WSException, WSException, IllegalAccessException, NoSuchFieldException;

    /**
     *
     *
     * @param fields
     * @param id
     * @return
     * @throws WSException
     */
    public T patch(Map<String, Object> fields, int id) throws WSException;

    /**
     *
     * @param t
     * @param id
     * @return
     * @throws WSException
     */
    public T put(T t, int id) throws WSException;

    /**
     *
     * @param id
     * @return
     * @throws WSException
     */
    public boolean head(int id) throws WSException;

    /**
     *
     * @param id
     * @throws WSException
     */
    public void delete(int id) throws WSException;

}
