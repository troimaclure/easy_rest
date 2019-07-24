package fr.milleis.easyrest.component;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.milleis.easyrest.model.HeaderCtxt;
import fr.milleis.easyrest.model.ZimbraTokenRequest;
import fr.milleis.easyrest.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zvcn
 *
 */
@Component
@Slf4j
public class MessagingAPI {

    /**
     * prefixe pour les id zimbra des clients
     */
    public static final String TARGET_PDV = "pdv";
    public static final String TARGET_MGR = "mgr";

    public static final String ACTIVE = "1";
    public static final String INACTIVE = "0";

    private RestTemplate restTemplate;

    /**
     * @param <T>
     * @param url
     * @param messageRequest
     * @return
     * @throws Exception
     */
    public <T> T callMessagingAPI(String url, Object messageRequest) throws Exception {
        return callMessagingAPI(url, messageRequest, HttpMethod.POST);
    }

    /**
     * @param <T>
     * @param url
     * @param messageRequest
     * @param httpMethod
     * @return
     * @throws Exception
     */
    public <T> T callMessagingAPI(String url, @Nullable Object messageRequest, HttpMethod httpMethod)
            throws Exception {
        String param = url;
        try {
            log.info("MessagingAPI.callMessagingAPI on URL : {}", url);

            HttpEntity<String> entity = buildHttpEntity(messageRequest, buildHeaders());

            ResponseEntity<T> responseEntity = restTemplate.exchange(param, httpMethod, entity,
                    new ParameterizedTypeReference<T>() {
            });

            if (isValid(responseEntity)) {
                return handleResponse(responseEntity);
            } else {
                throw createException(responseEntity);
            }
        } catch (HttpServerErrorException hsre) {
            String rbas = hsre.getResponseBodyAsString();
            log.error("HttpServerErrorException : {} stacktrace {} ", rbas,
                    StringUtils.join(Arrays.deepToString(ExceptionUtils.getRootCauseStackTrace(hsre))), System.lineSeparator());
            throw hsre;
        } catch (IllegalArgumentException iae) {
            log.error("Error call Jackson deserialisation : {}",
                    StringUtils.join(Arrays.deepToString(ExceptionUtils.getRootCauseStackTrace(iae))), System.lineSeparator());
            throw iae;
        } catch (RestClientException rce) {
            log.error("Error call RESTClientException : {}",
                    StringUtils.join(Arrays.deepToString(ExceptionUtils.getRootCauseStackTrace(rce))), System.lineSeparator());
            throw rce;
        } catch (Exception e) {
            log.error("Error call callMessagingAPI : {}",
                    StringUtils.join(Arrays.deepToString(ExceptionUtils.getRootCauseStackTrace(e))), System.lineSeparator());
            throw e;
        }
    }

    private <T> T handleResponse(ResponseEntity<T> responseEntity) throws Exception {
        if (!responseEntity.hasBody()) {
            log.error("no body in REST response");
            throw new Exception("no body in REST response");
        } else {
            T t = MapperUtil.convertValueToGenericType(responseEntity.getBody(), Include.NON_NULL);
            log.info("Response callMessagingAPI OK : {}", t.toString());
            return t;
        }
    }

    private Exception createException(ResponseEntity responseEntity) {
        if (responseEntity == null || responseEntity.getStatusCode() != null) {
            log.error("Error call callMessagingAPI response or statuscode is null");
            return new Exception("Error call callMessagingAPI response or statuscode is null");
        } else {
            log.error("Error call callMessagingAPI responseStatusCode is: {}, codevalue is {}, reason is {}",
                    responseEntity.getStatusCodeValue(), responseEntity.getStatusCodeValue(),
                    responseEntity.getStatusCode().getReasonPhrase());
            return new Exception(responseEntity.getStatusCode().getReasonPhrase());
        }
    }

    /**
     *
     * @return
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     *
     * @param messageRequest
     * @param headers
     * @return
     * @throws JsonProcessingException
     */
    private HttpEntity<String> buildHttpEntity(Object messageRequest, HttpHeaders headers) throws JsonProcessingException {
        HttpEntity<String> entity;
        if (messageRequest != null) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(messageRequest);
            entity = new HttpEntity<>(jsonInString, headers);
        } else {
            entity = new HttpEntity<>(headers);
        }
        return entity;
    }

    /**
     * @param headerContext
     * @return
     * @throws java.lang.Exception
     */
    public ZimbraTokenRequest getZimbraToken(HeaderCtxt headerContext) throws Exception {
        if (headerContext == null || StringUtils.isAllBlank(headerContext.getAuthToken())) {
            log.error("Zimbra token problem : header or zimbra is empty");
            throw new Exception("Zimbra token problem : header or zimbra is empty");
        }
        return ZimbraTokenRequest.builder().authToken(headerContext.getAuthToken()).build();
    }

    private <T> boolean isValid(ResponseEntity<T> responseEntity) {
        return responseEntity != null && responseEntity.getStatusCode() != null
                && responseEntity.getStatusCode().is2xxSuccessful();
    }
}
