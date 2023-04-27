package com.joshir.domain.filtered;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyTag{
  private String name;
  private List<Question> questions;
}
