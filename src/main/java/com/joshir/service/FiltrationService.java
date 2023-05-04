package com.joshir.service;

import com.joshir.domain.filtered.Question;
import com.joshir.domain.unfiltered.UnfilteredQuestions;
import com.joshir.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FiltrationService {
  private final InitializationService initializationService;

  /*TODO initilization logic for this*/
  private final Pair<List<UnfilteredQuestions>, Map<String, List<Question>>> _mem;

  public FiltrationService(InitializationService initializationService, Pair<List<UnfilteredQuestions>) {
    this.initializationService = initializationService;
  }
}
