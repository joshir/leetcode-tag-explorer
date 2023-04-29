package com.joshir.util;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair <T1, T2>{
  private T1 t1;
  private T2 t2;
}
