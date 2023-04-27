package com.joshir;

import com.joshir.domain.filtered.CompanyTag;
import com.joshir.domain.filtered.FilteredSet;
import com.joshir.domain.filtered.Question;
import com.joshir.domain.mapper.JsonMapper;
import com.joshir.domain.unfiltered.ProblemsetQuestionsList;
import com.joshir.domain.unfiltered.UnfilteredQuestions;
import com.joshir.domain.unfiltered.UnfilteredSet;
import com.joshir.domain.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
@SpringBootApplication
public class LCodeLoader implements CommandLineRunner {

  /* resource[] mapped to pair<?,?> of class and operation to be performed*/
  private final Map<Resource[], Pair<Class<?>, Function<?,?>>> resources = new HashMap();

  /* in-memory map */
  private final Map<Class<?>, List<?>> dataByType = new HashMap();

  /* stats for all problems */
  private final Resource[] unfiltered;

  /* stats for all problems by company tag */
  private final Resource[] filtered;

  public LCodeLoader(
          @Value("${app.data.unfiltered.context.path}") Resource[] unfilteredProblems,
          @Value("${app.data.filtered.context.path}") Resource[] filteredByCompany) {
    unfiltered = unfilteredProblems;
    filtered = filteredByCompany;
  }

  /*
   * for the time being it does not make much sense but
   * off load this expensive operation to a dedicated thread.
   * this will be needed at some point in the future when
   * spring-web will be introduced to this project
   * because we don't want to block the main thread event at startup
   * */
  private void loadResources() {
    long startTs = System.currentTimeMillis();
    Supplier<Pair<List<UnfilteredQuestions>, List<Question>>> c = getDataPair();

    CompletableFuture
      .supplyAsync(c, Executors.newFixedThreadPool(1))
        .thenAccept(p ->{
          log.info("loaded {} problems and a total of {} repeated problems from resources in {} ms", p.getKey().size(), p.getValue().size(), System.currentTimeMillis() - startTs);
        });
  }

  private Supplier<Pair<List<UnfilteredQuestions>, List<Question>>> getDataPair() {
    return () -> {
      List<UnfilteredQuestions> pList = null;
      List<Question> qList = null;
      try {
        resources.put(unfiltered, toPair(UnfilteredSet.class));
        resources.put(filtered, toPair(FilteredSet.class));
        dataByType.put(UnfilteredSet.class, new ArrayList<ProblemsetQuestionsList>());
        dataByType.put(FilteredSet.class, new ArrayList<CompanyTag>());
        resources.forEach((r, p) -> dataByType.put(p.getKey(), JsonMapper.loadResourceAsList(r, p.getKey(), p.getValue())));

        pList = flatten(UnfilteredSet.class, (Function<ProblemsetQuestionsList, List<UnfilteredQuestions>>) p -> p.getQuestions());
        qList = flatten(FilteredSet.class, (Function<CompanyTag, List<Question>>) filteredSet -> filteredSet.getQuestions());
      } catch (Exception e) {
        // todo handle specific exceptions specifically
        log.error("whoops");
        throw new RuntimeException();
      }
      return new Pair<>(pList, qList);
    };
  }

  private <X,Y> List<Y> flatten(Class<?> clazz, Function<X,List<Y>> func) {
    return((List<X>) dataByType.get(clazz))
      .stream()
      .map(func)
      .flatMap(List::stream)
      .collect(Collectors.toList());
  }

  private static Pair<Class<?>,Function<?,?>> toPair(Class<?> clazz) {
    switch(clazz.getSimpleName()){
      case "UnfilteredSet":
        return new Pair<>(clazz, (Function<UnfilteredSet, ProblemsetQuestionsList>) ufset -> ufset.getProblemsetQuestionList());
      case "FilteredSet":
        return new Pair<>(clazz, (Function<FilteredSet, CompanyTag>) fset -> fset.getCompanyTag());
      default:
        throw new RuntimeException();
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(LCodeLoader.class, args);
  }

  @Override
  public void run(String... args) {
    loadResources();
  }
}
