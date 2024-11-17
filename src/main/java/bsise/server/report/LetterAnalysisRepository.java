package bsise.server.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterAnalysisRepository extends JpaRepository<LetterAnalysis, Long> {
}
