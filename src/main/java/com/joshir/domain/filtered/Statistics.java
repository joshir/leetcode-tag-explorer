package com.joshir.domain.filtered;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Statistics {
  private String totalAccepted;
  private String totalSubmission;
  private Long totalAcceptedRaw;
  private Long totalSubmissionRaw;
  private String acRate;

  @JsonCreator
  public static Statistics create(String str) throws JsonParseException, JsonMappingException, IOException {
    return (new ObjectMapper()).readValue(str, Statistics.class);
  }
}
