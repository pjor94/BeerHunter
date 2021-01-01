package it.pjor94.beerhunter.core.strategy;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public class ParserException extends Exception {

  @Getter
  private List<ParserError> errors;

  public ParserException(String message, List<ParserError> errors) {
    super(message);
    this.errors = errors;
  }

  public ParserException(Throwable e) {
    super("Parse Errors", e);
    this.errors = new ArrayList<>();
    this.errors.add(new ParserError(e.getMessage()));
  }

  public ParserException(List<ParserError> errors) {
    this("Parse Errors", errors);
  }


}
