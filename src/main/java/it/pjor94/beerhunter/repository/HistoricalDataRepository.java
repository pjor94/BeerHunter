package it.pjor94.beerhunter.repository;

import it.pjor94.beerhunter.model.HistoricalData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistoricalDataRepository extends MongoRepository<HistoricalData, String> {
    HistoricalData findByPair(String pair);
}
