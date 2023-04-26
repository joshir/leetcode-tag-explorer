package com.joshir.domain.unfiltered;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName(value="data")
public class UnfilteredSet {
  private ProblemsetQuestionsList problemsetQuestionList;
}
