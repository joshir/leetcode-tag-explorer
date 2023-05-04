package com.joshir.service;

import com.joshir.domain.filtered.CompanyTag;
import com.joshir.domain.filtered.FilteredSet;
import com.joshir.domain.filtered.Question;
import com.joshir.domain.unfiltered.UnfilteredQuestions;
import com.joshir.domain.unfiltered.UnfilteredSet;
import com.joshir.mapper.JsonMapper;
import com.joshir.util.Pair;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InitializationService implements DisposableBean {
  /* resource[] mapped to pair<?,?> of class and operation to be performed*/
  private final Map<Resource[], Pair<Class<?>, Function<?,?>>> _resources;

  /* dedicated thread for objectMapping from JSON */
  private final ExecutorService exec = Executors.newFixedThreadPool(1);

  /* json mapper for object to json and vice-versa*/
  private final JsonMapper _mapper;

  /* in-memory map */
  /* stats for all problems */
  private final Pair<List<UnfilteredQuestions>, Map<String, List<Question>>> _mem = new Pair<>();


  public InitializationService(
          @Value("${app.data.unfiltered.context.path}") Resource[] unfilteredProblems,
          @Value("${app.data.filtered.context.path}") Resource[] filteredByCompany, JsonMapper _mapper) {
    this._mapper = _mapper;
    Objects.requireNonNull(unfilteredProblems, "File(s) not found!");
    Objects.requireNonNull(filteredByCompany, "File(s) not found!");
    _resources = Map.of(unfilteredProblems, toPair(UnfilteredSet.class), filteredByCompany, toPair(FilteredSet.class));
  }

  /*
   * for the time being it does not make much sense but
   * off load this expensive operation to a dedicated thread.
   * this will be needed at some point in the future when
   * spring-web will be introduced to this project
   * because we don't want to block the main thread event at startup
   * */
  Future<Pair<List<UnfilteredQuestions>, Map<String,List<Question>>>> loadResources() {

    return CompletableFuture.supplyAsync(getDataPair(), exec);

  }

  /*
   * load resources into the glorified "in memory" map by mapping from json
   * to Object and return the processed data as a Pair of lists of unfiltered
   * and filtered data.
   * */
  @SuppressWarnings("unchecked")
  private Supplier<Pair<List<UnfilteredQuestions>, Map<String,List<Question>>>> getDataPair() {
    return () -> {
      final Map<Class<?>, List<?>> dataByType = new HashMap<>();
      try {
        _resources.forEach((r, p) -> dataByType.put(p.getT1(), _mapper.loadResourceAsList(r, p.getT1(), p.getT2())));
      } catch (Exception e) {
        // todo handle specific exceptions specifically
        log.error("whoops");
        throw new RuntimeException();
      }

      return new Pair<> (
        (List<UnfilteredQuestions>) dataByType.get(UnfilteredSet.class),
        ((List<CompanyTag>) dataByType.get(FilteredSet.class))
          .stream()
          .collect(Collectors.groupingBy(CompanyTag::getName, Collectors.flatMapping(ctag-> ctag.getQuestions().stream(), Collectors.toList())))
);
    };
  }

  /*
   * convenience method that returns a Pair of Class<?> and Function<?,?>
   * based on simple class name
   * */
  private Pair<Class<?>,Function<?,?>> toPair(Class<?> clazz) {
    return switch (clazz.getSimpleName()) {
      case "UnfilteredSet" ->
        new Pair<>(clazz, (Function<UnfilteredSet, List<UnfilteredQuestions>>)
          ufset -> ufset.getProblemsetQuestionList().getQuestions());
      case "FilteredSet" ->
        new Pair<>(clazz, (Function<FilteredSet, List<CompanyTag>>)
          fset -> List.of(fset.getCompanyTag()));
      default -> throw new RuntimeException();
    };
  }

  @PostConstruct
  public void init() {
    loadResources();
  }

  @Override
  public void destroy() throws Exception {
    /* release thread */
    exec.shutdown();
  }
}
