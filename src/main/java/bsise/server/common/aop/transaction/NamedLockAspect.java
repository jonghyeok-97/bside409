package bsise.server.common.aop.transaction;

import bsise.server.error.NamedLockAcquisitionException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@Aspect
public class NamedLockAspect {

    private final TransactionTemplate transactionTemplate;

    @PersistenceContext
    private EntityManager em;

    public NamedLockAspect(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

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

        Propagation propagation = findPropagation(joinPoint);

        log.info("[NAMED LOCK AOP] using propagation={}", propagation);

        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(getPropagationBehavior(propagation));
        transactionTemplate.setPropagationBehavior(transactionDefinition.getPropagationBehavior());

        return transactionTemplate.execute(status -> {
            Object acquireResult = getNamedLock(finalLockName, timeout);

            if (!isTrue(acquireResult)) {
                throw new NamedLockAcquisitionException("[NAMED LOCK AOP] Failed to acquire lock=" + finalLockName);
            }

            log.info("[NAMED LOCK AOP] acquire lock={} on thread={}", finalLockName, Thread.currentThread().getName());

            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            } finally {
                Object releaseResult = releaseNamedLock(finalLockName);
                if (isTrue(releaseResult)) {
                    log.info("[NAMED LOCK AOP] successfully released lock={}", finalLockName);
                } else {
                    log.warn("[NAMED LOCK AOP] failed to release lock={}", finalLockName);
                }
            }
        });
    }

    private Propagation findPropagation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.isAnnotationPresent(Transactional.class)) {
            Transactional transactional = method.getAnnotation(Transactional.class);
            return transactional.propagation();
        }

        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (targetClass.isAnnotationPresent(Transactional.class)) {
            Transactional transactional = targetClass.getAnnotation(Transactional.class);
            return transactional.propagation();
        }

        log.warn("[NAMED LOCK AOP] propagation not found. defaulting to => Propagation.REQUIRED");
        return Propagation.REQUIRED;
    }

    private String generateUniqueKey(String lockName, Object[] args, String[] keyFields) {
        StringBuilder uniqueKey = new StringBuilder();
        uniqueKey.append(lockName);
        if (args == null || args.length == 0) {
            return returnStringWithMaxLength(uniqueKey);
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

        return returnStringWithMaxLength(uniqueKey);
    }

    private String returnStringWithMaxLength(StringBuilder sb) {
        if (sb.length() > 64) {
            return sb.substring(0, 64);
        }
        return sb.toString();
    }

    private int getPropagationBehavior(Propagation propagation) {
        return switch (propagation) {
            case REQUIRED -> TransactionDefinition.PROPAGATION_REQUIRED;
            case REQUIRES_NEW -> TransactionDefinition.PROPAGATION_REQUIRES_NEW;
            case SUPPORTS -> TransactionDefinition.PROPAGATION_SUPPORTS;
            case NOT_SUPPORTED -> TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
            case MANDATORY -> TransactionDefinition.PROPAGATION_MANDATORY;
            case NEVER -> TransactionDefinition.PROPAGATION_NEVER;
            case NESTED -> TransactionDefinition.PROPAGATION_NESTED;
        };
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
