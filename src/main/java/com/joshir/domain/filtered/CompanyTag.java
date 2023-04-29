package com.joshir.domain.filtered;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyTag{
  private String name;
  private List<Question> questions;
}
