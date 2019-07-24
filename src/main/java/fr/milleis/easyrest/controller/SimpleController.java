package fr.milleis.easyrest.controller;

import fr.milleis.easyrest.exception.ErrorDetails;
import fr.milleis.easyrest.exception.WSException;
import fr.milleis.easyrest.interfaces.SimpleRest;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * need to be overriden
 *
 * @author ajosse
 * @param <T>
 */
@Slf4j
public abstract class SimpleController<T> {

    public abstract SimpleRest<T> getService();

    /**
     * Handle all WSException
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(WSException.class)
    public final ResponseEntity<ErrorDetails> handleWsException(WSException ex, WebRequest request) {
        var errorDetails = new ErrorDetails(new Date(), ex.getMessage());
        log.error(ex.getMessage(), ex.getCause());
        return new ResponseEntity<>(errorDetails, ex.getHttpStatus());
    }

}
