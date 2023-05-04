package com.joshir.domain.unfiltered;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProblemsetQuestionsList{
  private int total;
  private List<UnfilteredQuestions> questions;
}
