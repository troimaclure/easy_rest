package fr.troimaclure.easyrest.aspect.log;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 *
 * @author ajosse
 */
@Component
@Aspect
@Slf4j
public class SimpleLogAspect {

    @Around("@annotation(SimpleLog)")
    public void log(ProceedingJoinPoint j) throws Throwable {
        String methodName = j.getSignature().getName();
        String className = j.getTarget().getClass().getSimpleName();
        String args = Arrays.asList(j.getArgs()).stream().map(Object::toString).collect(Collectors.joining(" | "));
        log.info(className + " | " + methodName + " | " + args);
        j.proceed();
    }
}
