package fr.troimaclure.easyrest.service;

import fr.troimaclure.easyrest.aspect.log.SimpleLog;
import fr.troimaclure.easyrest.exception.WSException;
import fr.troimaclure.easyrest.interfaces.SimpleConvert;
import fr.troimaclure.easyrest.interfaces.SimpleRest;
import fr.troimaclure.easyrest.util.MapperUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;

/**
 *
 * @author ajosse
 * @param <T>
 * @param <E>
 */
@Slf4j
public abstract class SimpleServiceRepositoryRest<T, E> implements SimpleRest<T> {

    private static final String ENTITY_DOESNT_EXIST = "Entity doesn't exist";
    private static final String ID = "id";
    private static final String ENTITY_NOT_FOUND = "Entity not found";

    public abstract JpaRepository getRepository();

    public abstract SimpleConvert<T, E> getConverter();

    @SimpleLog
    public String getPrimaryFieldName() {
        return ID;
    }

    /**
     * return if exist dto with nested <br>
     * 404 if not found
     *
     * @param id
     * @return
     * @throws fr.troimaclure.easyrest.exception.WSException
     */
    @SimpleLog
    @Override
    public T get(int id) throws WSException {
        Optional<E> t = this.getRepository().findById(id);
        if (!t.isPresent())
            throw new WSException(ENTITY_DOESNT_EXIST, HttpStatus.NOT_FOUND);
        return getConverter().dtoWithNested(t.get());
    }

    /**
     * return list of dto <br>
     * could be empty
     *
     * @return
     */
    @SimpleLog
    @Override
    public List<T> get() {
        return (List<T>) this.getRepository().findAll().stream().map(e -> getConverter().dto((E) e))
                .collect(Collectors.toList());
    }

    /**
     * check if entity'id exist in database, throw WSException if exist<br>
     * entity's getId() method have to exists if check wanted<br>
     * if getId() not exist in entity , check is passed
     *
     * @param t
     * @return
     * @throws WSException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchFieldException
     */
    @SimpleLog
    @Override
    public T post(T t) throws WSException, IllegalAccessException, NoSuchFieldException {
        E e = getConverter().entity(t);
        if (this.getRepository().existsById(MapperUtil.getValueField(e, getPrimaryFieldName()))) throw new WSException("This id already exists", HttpStatus.CONFLICT);
        return persist(e);
    }

    /**
     * persist in database
     *
     * @param t
     * @return
     */
    @SimpleLog
    protected T persist(E t) {
        var save = (E) this.getRepository().save(t);
        return getConverter().dto(save);
    }

    /**
     * check if id exist in database
     *
     * @param id
     * @return
     */
    @SimpleLog
    @Override
    public boolean head(int id) {
        return this.getRepository().existsById(id);
    }

    /**
     *
     * feed entity in database with fields<br>
     * call put()
     *
     * @param fields
     * @param id
     * @return
     * @throws WSException
     */
    @SimpleLog
    @Override
    public T patch(Map<String, Object> fields, int id) throws WSException {
        return this.persist(MapperUtil.syncFields(fields, syncId(this.get(find(id)), id)));
    }

    protected int find(int id) throws WSException {
        if (!this.head(id)) {
            throw new WSException(ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return id;
    }

    /**
     * check if entity exist in database <br>
     * return 404 if not exist<br>
     * persist
     *
     * @param t
     * @return
     * @throws WSException
     */
    @Override
    public T put(T t, int id) throws WSException {
        return this.persist(syncId(t, find(id)));
    }

    /**
     * If PUT or PATCH does not contains id
     *
     * @param t
     * @param id
     * @return
     */
    private E syncId(T t, int id) {
        E e = getConverter().entity(t);
        HashMap<String, Object> map = new HashMap<>();
        map.put(getPrimaryFieldName(), id);
        return MapperUtil.syncFields(map, e);
    }

    /**
     *
     * @param id
     * @throws WSException
     */
    @Override
    public void delete(int id) throws WSException {
        getRepository().deleteById(id);
    }
}
