package fr.milleis.easyrest.exception;

import java.util.Date;
import lombok.AccessLevel;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author ajosse
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ErrorDetails {

    private Date timestamp;
    private String message;

}
