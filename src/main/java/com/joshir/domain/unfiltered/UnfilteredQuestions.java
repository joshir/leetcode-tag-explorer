package com.joshir.domain.unfiltered;

import com.joshir.domain.common.ProblemSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class UnfilteredQuestions{
  private Double acRate;
  private String difficulty;
  private Double freqBar;
  private Integer frontendQuestionId;
  private Boolean isFavor;
  private Boolean paidOnly;
  private Boolean hasSolution;
  private Boolean hasVideoSolution;
  private String status;
  private String title;
  private String titleSlug;
  private List<Topic> topicTags;
}
