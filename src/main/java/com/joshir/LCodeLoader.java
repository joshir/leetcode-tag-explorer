package com.joshir;

import com.joshir.domain.filtered.FilteredSet;
import com.joshir.domain.filtered.Question;
import com.joshir.mapper.JsonMapper;
import com.joshir.domain.unfiltered.UnfilteredQuestions;
import com.joshir.domain.unfiltered.UnfilteredSet;
import com.joshir.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;


@Slf4j
@SpringBootApplication
/* todo move these out of the driver class */
public class LCodeLoader implements CommandLineRunner, DisposableBean {

  /* resource[] mapped to pair<?,?> of class and operation to be performed*/
  private final Map<Resource[], Pair<Class<?>, Function<?,?>>> _resources;

  /* in-memory map */
  /* stats for all problems */
  private final Pair<List<UnfilteredQuestions>, List<Question>> _mem ;

  /* dedicated thread for objectMapping from JSON */
  private final ExecutorService exec = Executors.newFixedThreadPool(1);

  public LCodeLoader(
          @Value("${app.data.unfiltered.context.path}") Resource[] unfilteredProblems,
          @Value("${app.data.filtered.context.path}") Resource[] filteredByCompany) {
    Objects.requireNonNull(unfilteredProblems, "Files not found!");
    Objects.requireNonNull(filteredByCompany, "Files not found!");
    _resources = Map.of(unfilteredProblems, toPair(UnfilteredSet.class), filteredByCompany, toPair(FilteredSet.class));
    _mem = new Pair<>();
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
    Supplier<Pair<List<UnfilteredQuestions>, List<Question>>> suite = getDataPair();

    CompletableFuture
      .supplyAsync(suite, exec)
        .thenAcceptAsync(p -> {
          _mem.setT1(p.getT1());
          _mem.setT2(p.getT2());
          log.info(
            "loaded {} problems and a total of {} repeated problems from resources in {} ms",
            p.getT1().size(),
            p.getT2().size(),
            System.currentTimeMillis() - startTs);
        }, exec);
  }

  /*
   * load resources into the glorified "in memory" map by mapping from json
   * to Object and return the processed data as a Pair of lists of unfiltered
   * and filtered data.
   * */
  private Supplier<Pair<List<UnfilteredQuestions>, List<Question>>> getDataPair() {
    return () -> {
      final Map<Class<?>, List<?>> dataByType = new HashMap<>();
      try {
        _resources.forEach((r, p) -> dataByType.put(p.getT1(), JsonMapper.loadResourceAsList(r, p.getT1(), p.getT2())));
      } catch (Exception e) {
        // todo handle specific exceptions specifically
        log.error("whoops");
        throw new RuntimeException();
      }
      return Pair.castTogether(dataByType.get(UnfilteredSet.class), dataByType.get(FilteredSet.class));
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
        new Pair<>(clazz, (Function<FilteredSet, List<Question>>)
          fset -> fset.getCompanyTag().getQuestions());
      default -> throw new RuntimeException();
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(LCodeLoader.class, args);
  }

  @Override
  public void run(String... args) {
    loadResources();
  }

  @Override
  public void destroy() throws Exception {
    /* release thread */
    exec.shutdown();
  }


}
