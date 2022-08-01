package it.pjor94.beerhunter.repository;

import it.pjor94.beerhunter.model.HistoricalData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HistoricalDataRepository extends MongoRepository<HistoricalData, String> {
    HistoricalData findByPair(String pair);
    Page<HistoricalData> findAll(Pageable pageable);
}
