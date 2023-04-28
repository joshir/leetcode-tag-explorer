package com.joshir.domain.util;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair <T1, T2>{
  private T1 t1;
  private T2 t2;
}
