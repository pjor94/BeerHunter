package it.pjor94.beerhunter.model;

import it.pjor94.beerhunter.core.ERole;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Role {
  @Id
  private String id;

  private ERole name;

  public Role() {

  }
  public Role(ERole name) {
    this.name = name;
  }
}