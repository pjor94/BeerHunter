package it.pjor94.beerhunter.config;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.math.BigDecimal;
import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class ConfigurationSpring /*extends AbstractMongoClientConfiguration */{
//    spring.data.mongodb.host=192.168.1.9
//    spring.data.mongodb.port=27017
//    spring.data.mongodb.database=BEERHUNTER
//    spring.data.mongodb.username=BEERHUNTER_USER
//    spring.data.mongodb.password=0987654321
    @Value("${spring.data.mongodb.username}")
    String mongoUser;
    @Value("${spring.data.mongodb.database}")
    String databaseName;
    @Value("${spring.data.mongodb.password}")
    String mongoPass;
    @Value("${spring.data.mongodb.host}")
    String mongoHost;
    @Value("${spring.data.mongodb.port}")
    String mongoPort;

//    @Override
//    protected String getDatabaseName() {
//        return databaseName;
//    }
//
//    @Override
//    public MongoClient mongoClient() {
//        ConnectionString connectionString = new ConnectionString("mongodb://"+mongoHost+":"+mongoPort+"/"+databaseName);
//        MongoCredential credential = MongoCredential.createCredential(mongoUser, databaseName, mongoPass.toCharArray());
//
//        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
//                .credential(credential)
//                .applyConnectionString(connectionString)
//                .build();
//
//        return MongoClients.create(mongoClientSettings);
//    }
    @Bean
    public MongoClient mongo() {
        ConnectionString connectionString = new ConnectionString("mongodb://"+mongoHost+":"+mongoPort+"/"+databaseName);
        MongoCredential credential = MongoCredential.createCredential(mongoUser, databaseName, mongoPass.toCharArray());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .credential(credential)
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), databaseName);
    }
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        var list = new ArrayList<>();
        Converter<Decimal128, BigDecimal> decimal128ToBigDecimal = new Converter<Decimal128, BigDecimal>() {
            @Override
            public BigDecimal convert(Decimal128 s) {
                return s==null ? null : s.bigDecimalValue();
            }
        };

        Converter<BigDecimal, Decimal128> bigDecimalToDecimal128 = new Converter<BigDecimal, Decimal128>() {
            @Override
            public Decimal128 convert(BigDecimal s) {
                return s==null ? null : new Decimal128(s);
            }
        };
        list.add(decimal128ToBigDecimal);
        list.add(bigDecimalToDecimal128);
        return new MongoCustomConversions(list);
    }
//}
}