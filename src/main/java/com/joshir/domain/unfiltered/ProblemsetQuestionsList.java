package com.joshir.domain.unfiltered;


import com.fasterxml.jackson.annotation.JsonRootName;
import com.joshir.domain.common.ProblemSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProblemsetQuestionsList  implements ProblemSet<UnfilteredQuestions> {
  private int total;
  private List<UnfilteredQuestions> questions;

  @Override
  public List<UnfilteredQuestions> get() {
    return questions;
  }
}
