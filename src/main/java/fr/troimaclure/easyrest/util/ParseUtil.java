package fr.troimaclure.easyrest.util;

import fr.troimaclure.easyrest.exception.WSException;
import org.springframework.http.HttpStatus;

/**
 *
 * @author ajosse
 */
public class ParseUtil {

    public static int parseId(String id) throws WSException {
        if (id.matches("\\d+")) return Integer.parseInt(id);
        else throw new WSException("id must be a not null integer", HttpStatus.BAD_REQUEST);
    }
}
