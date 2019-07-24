package fr.troimaclure.easyrest.service;

import fr.troimaclure.easyrest.aspect.log.SimpleLog;
import fr.troimaclure.easyrest.interfaces.SimpleRest;
import fr.troimaclure.easyrest.util.TypeUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ajosse
 * @param <T>
 */
@Slf4j
public abstract class SimpleServiceApiCallRest<T> implements SimpleRest<T> {

    private static final String URL_SEPARATOR = "/";

    private final RestTemplate restTemplate;

    public SimpleServiceApiCallRest(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected RestTemplate getRest() {
        return restTemplate;
    }

    /**
     * Example : http://host.com<br>
     * WARNING : do not include the final '/'
     *
     * @return
     */
    protected abstract String getApiUrl();

    /**
     * Example : users <br>
     * WARNING : do not include the final '/'
     *
     * @return
     */
    protected abstract String getEndPoint();

    protected String getFullUrl() {
        return this.getApiUrl() + URL_SEPARATOR + getEndPoint();
    }

    /**
     *
     * @param id
     */
    @SimpleLog
    @Override
    public void delete(int id) {
        getRest().delete(getFullUrl() + URL_SEPARATOR + id);
    }

    @SimpleLog
    @Override
    public boolean head(int id) {
        ResponseEntity<Boolean> exchange = getRest().exchange(getFullUrl() + id, HttpMethod.HEAD, null, Boolean.class);
        return exchange.getBody();
    }

    @SimpleLog
    @Override
    public T put(T t, int id) {
        getRest().put(getFullUrl() + id, t);
        return t;
    }

    @SimpleLog
    @Override
    public T patch(Map<String, Object> fields, int id) {
        return getRest().patchForObject(getFullUrl(), fields, TypeUtil.getGenericType(), fields);
    }

    @SimpleLog
    @Override
    public T post(T t) {
        return getRest().postForObject(getFullUrl(), t, TypeUtil.getGenericType());
    }

    @SimpleLog
    @Override
    public List<T> get() {
        return Arrays.asList((T[]) getRest().getForObject(getFullUrl(), TypeUtil.getArrayGenericType()));
    }

    @SimpleLog
    @Override
    public T get(int id) {
        return getRest().getForObject(getFullUrl() + id, TypeUtil.getGenericType());
    }

}
