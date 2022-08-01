package it.pjor94.beerhunter.repository;

import it.pjor94.beerhunter.model.Candlestick;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface CandlestickRepository extends MongoRepository<Candlestick, String> {

//  public Candlestick findByFirstName(String firstName);
//  public List<Candlestick> findByLastName(String lastName);
    Candlestick findFirstByPairOrderByOpenTimeDesc(String pair);
    Candlestick findFirstByPairOrderByOpenTimeAsc(String pair);
    Page<Candlestick> findByPairOrderByOpenTimeDesc(String pair,Pageable pageable);
    Stream<Candlestick> findByPairOrderByOpenTimeDesc(String pair);

    @Query("{'pair' : :#{#pair}}")
    Stream<Candlestick>  findByPairAndSort(@Param("pair") String pair, Sort sort);


}