package it.pjor94.beerhunter.core.strategy;

import lombok.Getter;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

public class ErrorListener extends BaseErrorListener {
  @Getter
  private final List<ParserError> errors = new ArrayList<>();

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int position, String msg, RecognitionException e) {
    errors.add(new ParserError(line, position, msg));
  }

}
