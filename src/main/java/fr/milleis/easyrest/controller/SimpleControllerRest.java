package fr.milleis.easyrest.controller;

import fr.milleis.easyrest.aspect.log.SimpleLog;
import fr.milleis.easyrest.exception.ErrorDetails;
import fr.milleis.easyrest.exception.WSException;
import fr.milleis.easyrest.interfaces.SimpleRest;
import fr.milleis.easyrest.util.MapperUtil;
import fr.milleis.easyrest.util.ParseUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.WebRequest;

/**
 * no need to be override if requestmapping is simple
 *
 * @author ajosse
 * @param <T>
 */
@Slf4j
public abstract class SimpleControllerRest<T> {

    private static final String _ID = "/{id}";
    private static final String FIELDS_ARE_EMPTY = "fields are empty";
    private static final String ID = "id";
    private static final String BULK = "/bulk";
    private static final String BULKIDS = "/bulk/{ids}";

    public abstract SimpleRest<T> getService();

    public String getPrimaryFieldName() {
        return ID;
    }

    /**
     * Handle all WSException
     *
     * @param ex
     * @param request
     * @return
     */
    @SimpleLog
    @ExceptionHandler(WSException.class)
    public final ResponseEntity<ErrorDetails> handleWsException(WSException ex, WebRequest request) {
        var errorDetails = new ErrorDetails(new Date(), ex.getMessage());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(errorDetails, ex.getHttpStatus());
    }

    /**
     *
     * @param id
     * @return
     * @throws fr.milleis.easyrest.exception.WSException
     */
    @SimpleLog
    @GetMapping(_ID)
    public ResponseEntity<T> get(@PathVariable(ID) String id) throws WSException {
        var parseId = ParseUtil.parseId(id);
        return new ResponseEntity<>(this.getService().get(parseId), HttpStatus.OK);

    }

    /**
     *
     * @return @throws WSException
     */
    @SimpleLog
    @GetMapping
    public ResponseEntity<List<T>> get() throws WSException {
        return new ResponseEntity<>(this.getService().get(), HttpStatus.OK);
    }

    /**
     *
     * @param t
     * @return
     * @throws fr.milleis.easyrest.exception.WSException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchFieldException
     */
    @SimpleLog
    @PostMapping
    public ResponseEntity<T> post(@RequestBody T t) throws WSException, IllegalAccessException, NoSuchFieldException {
        return new ResponseEntity<>(this.getService().post(t), HttpStatus.CREATED);
    }

    /**
     *
     * @param fields
     * @param id
     * @return
     * @throws fr.milleis.easyrest.exception.WSException
     */
    @SimpleLog
    @PatchMapping(_ID)
    public ResponseEntity<T> patch(@RequestBody Map<String, Object> fields, @PathVariable(ID) String id) throws WSException {
        if (fields == null || fields.isEmpty()) throw new WSException(FIELDS_ARE_EMPTY, HttpStatus.BAD_REQUEST);
        var parseId = ParseUtil.parseId(id);
        return new ResponseEntity<>(this.getService().patch(fields, parseId), HttpStatus.OK);
    }

    /**
     *
     * @param t
     * @param id
     * @return
     * @throws fr.milleis.easyrest.exception.WSException
     */
    @SimpleLog
    @PutMapping(_ID)
    public ResponseEntity<T> put(@RequestBody T t, @PathVariable(ID) String id) throws WSException {
        var parseId = ParseUtil.parseId(id);
        return new ResponseEntity<>(this.getService().put(t, parseId), HttpStatus.OK);
    }

    /**
     *
     * @param id
     * @return
     * @throws fr.milleis.easyrest.exception.WSException
     */
    @SimpleLog
    @DeleteMapping(_ID)
    public ResponseEntity delete(@PathVariable(ID) String id) throws WSException {
        var parseId = ParseUtil.parseId(id);
        this.getService().delete(parseId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     *
     * @param tList
     * @return
     * @throws WSException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    @SimpleLog
    @PostMapping(BULK)
    public ResponseEntity<ResponseEntity<T>> bulkPost(@RequestBody LinkedHashMap[] tList) throws WSException, IllegalAccessException, NoSuchFieldException {
        ArrayList<ResponseEntity<T>> responseEntitys = new ArrayList<>();
        for (LinkedHashMap t : tList) {
            try {
                T post = this.getService().post(MapperUtil.transformLinkedHashMap(t));
                responseEntitys.add(new ResponseEntity(post, HttpStatus.CREATED));
            } catch (WSException wSException) {
                //conflict or internal error
                log.error(wSException.getMessage(), wSException);
                responseEntitys.add(new ResponseEntity(t, wSException.getHttpStatus()));
            }
        }
        return new ResponseEntity(responseEntitys, HttpStatus.OK);
    }

    /**
     *
     * @param tList
     * @return
     * @throws WSException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalAccessException
     */
    @SimpleLog
    @PutMapping(BULK)
    public ResponseEntity<ResponseEntity<T>> bulkPut(@RequestBody LinkedHashMap[] tList) throws WSException, NoSuchFieldException, SecurityException, IllegalAccessException {
        ArrayList<ResponseEntity<T>> responseEntitys = new ArrayList<>();
        for (LinkedHashMap t : tList) {
            try {
                T put = this.getService().put(MapperUtil.transformLinkedHashMap(t), (int) t.get(getPrimaryFieldName()));
                responseEntitys.add(new ResponseEntity(put, HttpStatus.OK));
            } catch (WSException wSException) {
                //conflict or internal error
                log.error(wSException.getMessage(), wSException);
                responseEntitys.add(new ResponseEntity(t, wSException.getHttpStatus()));
            }
        }
        return new ResponseEntity(responseEntitys, HttpStatus.OK);
    }

    /**
     *
     * @param ids
     * @return
     * @throws WSException
     */
    @SimpleLog
    @DeleteMapping(BULKIDS)
    public ResponseEntity<ResponseEntity<T>> bulkDelete(@PathVariable String[] ids) throws WSException {
        ArrayList<ResponseEntity<T>> responseEntitys = new ArrayList<>();
        for (String t : ids) {
            try {
                this.getService().delete(ParseUtil.parseId(t));
                responseEntitys.add(new ResponseEntity(HttpStatus.OK));
            } catch (WSException wSException) {
                //conflict or internal error
                log.error(wSException.getMessage(), wSException);
                responseEntitys.add(new ResponseEntity(t, wSException.getHttpStatus()));
            }
        }
        return new ResponseEntity(responseEntitys, HttpStatus.OK);
    }
}
