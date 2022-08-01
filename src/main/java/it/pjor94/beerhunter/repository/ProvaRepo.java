package it.pjor94.beerhunter.repository;

import it.pjor94.beerhunter.model.Candlestick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class ProvaRepo {

	@Autowired
	MongoTemplate mongoTemplate;
	
	public Collection<Candlestick> searchPair(String text) {
		Query q = new Query();
		q.addCriteria(Criteria.where("pair").is(text));
//		q.addCriteria(new Criteria().andOperator(Criteria.where("BEGIN_LAT").lt(lat+1),
//				Criteria.where("BEGIN_LAT").gt(lat-1),
//				Criteria.where("BEGIN_LON").lt(lon+1),
//				Criteria.where("BEGIN_LON").gt(lon-1)));
		return mongoTemplate.find(q, Candlestick.class);
	}
	
}