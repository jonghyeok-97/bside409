package bsise.server.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class NamedLockRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.MANDATORY)
    public boolean acquireLock(String name, int timeoutSeconds) {
        String sql = "SELECT GET_LOCK(:name, :timeoutSeconds)";

        em.setFlushMode(FlushModeType.COMMIT);

        Object result = em.createNativeQuery(sql)
                .setParameter("name", name)
                .setParameter("timeoutSeconds", timeoutSeconds)
                .getSingleResult();

        log.info("[Acquire Lock] Thread Name={}, result={}", Thread.currentThread().getName(), result);
        return result != null && result.toString().equals("1");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public boolean  releaseLock(String name) {
        String sql = "SELECT RELEASE_LOCK(:name)";
        Object result = em.createNativeQuery(sql)
                .setParameter("name", name)
                .getSingleResult();

        em.setFlushMode(FlushModeType.AUTO);

        log.info("[Release Lock] Thread Name={}", Thread.currentThread().getName());
        return result != null && result.toString().equals("1");
    }
}
