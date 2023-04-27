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

import java.util.*;
import java.util.function.Function;


@Slf4j
@SpringBootApplication
public class LCodeLoader implements CommandLineRunner {
  private final Map<Resource[], Pair<Class<?>, Function<?,?> >> resources = new HashMap();
  private final Map<Class<?>, List<?>> dataByType = new HashMap();
  private final Resource[] unfiltered;
  private final Resource[] filtered;

  public LCodeLoader(
          @Value("${app.data.unfiltered.context.path}") Resource[] unfilteredProblems,
          @Value("${app.data.filtered.context.path}") Resource[] filteredByCompany) {

    unfiltered = unfilteredProblems;
    filtered = filteredByCompany;
    loadResources();
  }

  public static void main(String[] args) {
    SpringApplication.run(LCodeLoader.class, args);
  }

  @Override
  public void run(String... args) {
    resources.forEach((r, p) -> {
      dataByType
        .put(p.getKey(),JsonMapper.loadResourceAsList(r, p.getKey(), p.getValue()));
    });

    // test
    log.info(JsonMapper.writeToJson(dataByType.get(UnfilteredSet.class)));
    log.info(JsonMapper.writeToJson(dataByType.get(FilteredSet.class)));
  }

  private void loadResources() {
    resources
      .put(unfiltered, new Pair<> (UnfilteredSet.class, (Function<UnfilteredSet, ProblemsetQuestionsList>) unfilteredSet -> unfilteredSet.getProblemsetQuestionList()));
    resources
      .put(filtered, new Pair<>(FilteredSet.class, (Function<FilteredSet, CompanyTag>) filteredSet -> filteredSet.getCompanyTag()));

    dataByType.put(UnfilteredSet.class, new ArrayList<ProblemsetQuestionsList>());

    dataByType.put(FilteredSet.class, new ArrayList<CompanyTag>());
  }
}
