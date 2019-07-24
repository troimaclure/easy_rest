/**
 *
 */
package fr.milleis.easyrest.exception;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

/**
 * @author h01612828
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class WSException extends Throwable {

    String message;
    HttpStatus httpStatus;

    public WSException(String message) {
        this.message = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
