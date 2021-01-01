package it.pjor94.beerhunter.repository;

import it.pjor94.beerhunter.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository  extends MongoRepository<Role,String> {
}
