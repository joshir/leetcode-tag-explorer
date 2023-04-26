package com.joshir.domain.filtered;

import com.joshir.domain.unfiltered.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
  private String status;
  private Integer questionId;
  private Integer questionFrontendId;
  private String translatedTitle;
  private String stats;
  private String difficulty;
  private Boolean isPaidOnly;
  private Boolean canSeeQuestion;
  private String title;
  private String titleSlug;
  private List<Topic> topicTags;
}
