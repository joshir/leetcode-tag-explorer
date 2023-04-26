package com.joshir.domain.unfiltered;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Topic {
  private String name;
  private String id;
  private String slug;
}
