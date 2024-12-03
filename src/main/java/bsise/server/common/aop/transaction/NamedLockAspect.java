package bsise.server.common.aop.transaction;

import bsise.server.error.NamedLockAcquisitionException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class NamedLockAspect {

    @PersistenceContext
    private EntityManager em;

    @Pointcut("execution(* bsise.server..*Service.*(..))")
    private void allService() {
    }

    @Around("@annotation(annotation) && allService()")
    public Object doTransactionWithNamedLock(ProceedingJoinPoint joinPoint, NamedLock annotation) throws Throwable {
        String lockName = annotation.lockName();
        Object[] args = joinPoint.getArgs();
        String[] keyFields = annotation.keyFields();

        // lockName, args, keyFields 로 고유 키 생성
        final String finalLockName = generateUniqueKey(lockName, args, keyFields);

        // timeout 음수 검증
        int timeout = annotation.timeout();
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout must not be negative");
        }

        Object acquireResult = getNamedLock(finalLockName, timeout);

        if (!isTrue(acquireResult)) {
            throw new NamedLockAcquisitionException("[NAMED LOCK AOP] Failed to acquire lock=" + finalLockName);
        }

        log.info("[NAMED LOCK AOP] acquire lock={} on thread={}", finalLockName, Thread.currentThread().getName());

        try {
            return joinPoint.proceed();
        } finally {
            Object releaseResult = releaseNamedLock(finalLockName);
            if (isTrue(releaseResult)) {
                log.info("[NAMED LOCK AOP] successfully released lock={}", finalLockName);
            } else {
                log.warn("[NAMED LOCK AOP] failed to release lock={}", finalLockName);
            }
        }
    }

    private String generateUniqueKey(String lockName, Object[] args, String[] keyFields) {
        StringBuilder uniqueKey = new StringBuilder();
        uniqueKey.append(lockName);
        if (args == null || args.length == 0) {
            return trimToMaxLength(uniqueKey);
        }

        for (String keyField : keyFields) {
            for (Object arg : args) {
                try {
                    Field field = arg.getClass().getDeclaredField(keyField);
                    field.setAccessible(true);
                    Object value = field.get(arg);
                    if (value != null) {
                        uniqueKey.append(value);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return trimToMaxLength(uniqueKey);
    }

    private String trimToMaxLength(StringBuilder sb) {
        if (sb.length() > 64) {
            return sb.substring(0, 64);
        }
        return sb.toString();
    }

    private Object getNamedLock(String lockName, int timeoutSeconds) {
        log.info("[NAMED LOCK AOP] trying to acquire named lock");

        em.setFlushMode(FlushModeType.COMMIT);

        String sql = "SELECT GET_LOCK(:name, :timeoutSeconds)";

        return em.createNativeQuery(sql)
                .setParameter("name", lockName)
                .setParameter("timeoutSeconds", timeoutSeconds)
                .getSingleResult();
    }

    private Object releaseNamedLock(String lockName) {
        log.info("[NAMED LOCK AOP] trying to release named lock");
        String sql = "SELECT RELEASE_LOCK(:name)";

        Object result = em.createNativeQuery(sql)
                .setParameter("name", lockName)
                .getSingleResult();

        em.setFlushMode(FlushModeType.AUTO);

        return result;
    }

    private boolean isTrue(Object result) {
        return result != null && result.toString().equals("1");
    }
}
