package com.joshir.domain.filtered;

import com.joshir.domain.common.ProblemSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyTag implements ProblemSet<Question> {
  private String name;
  private List<Question> questions;

  @Override
  public List<Question> get() {
    return questions;
  }
}
