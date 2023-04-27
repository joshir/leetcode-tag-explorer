package com.joshir;

import com.joshir.domain.filtered.CompanyTag;
import com.joshir.domain.filtered.FilteredSet;
import com.joshir.domain.mapper.JsonMapper;
import com.joshir.domain.unfiltered.ProblemsetQuestionsList;
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
import java.util.concurrent.Executors;
import java.util.function.Function;


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

  public static void main(String[] args) {

    SpringApplication.run(LCodeLoader.class, args);

  }

  @Override
  public void run(String... args) {
    loadResources();
    resources
      .forEach((r, p) ->
        dataByType
          .put(p.getKey(),
            JsonMapper.loadResourceAsList(r, p.getKey(), p.getValue()))
      );
  }

  /*
   * for the time being it does not make much sense but
   * off load this expensive operation to a dedicated thread.
   * this will be needed at some point in the future when
   * spring-web will be introduced to this project
   * because we don't want to block the main thread event at startup
   * */
  private void loadResources() {
    Executors.newFixedThreadPool(1).submit(() -> {
      long startTs = System.currentTimeMillis();
      try {
        resources
          .put(unfiltered,
            new Pair(UnfilteredSet.class, (Function<UnfilteredSet, ProblemsetQuestionsList>)
              unfilteredSet -> unfilteredSet.getProblemsetQuestionList()));
        resources
          .put(filtered,
            new Pair(FilteredSet.class, (Function<FilteredSet, CompanyTag>)
              filteredSet -> filteredSet.getCompanyTag()));
        dataByType.put(UnfilteredSet.class, new ArrayList<ProblemsetQuestionsList>());
        dataByType.put(FilteredSet.class, new ArrayList<CompanyTag>());
      } catch (Exception e) {
        // todo handle specific exceptions specifically
        log.error("whoops");
      }
      log.info("loaded data from resources in {} ms", (System.currentTimeMillis() - startTs));
    });
  }
}
