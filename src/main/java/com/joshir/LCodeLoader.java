package com.joshir;

import com.fasterxml.jackson.databind.*;
import com.joshir.domain.filtered.CompanyTag;
import com.joshir.domain.filtered.FilteredSet;
import com.joshir.domain.unfiltered.ProblemsetQuestionsList;
import com.joshir.domain.unfiltered.UnfilteredSet;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pl.touk.throwing.ThrowingFunction.unchecked;

@SpringBootApplication
public class LCodeLoader implements CommandLineRunner {
  private static final String ROOT_NAME;
  private static final ObjectMapper mapper;
  private final List<ProblemsetQuestionsList> questionLists = new ArrayList<>();;
  private final List<CompanyTag> questionsListByCompany = new ArrayList<>();;
  private final List<Resource[]> resources = new ArrayList<>();

  static {
    ROOT_NAME = "data";
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
  }
  public LCodeLoader(
       @Value("${app.data.unfiltered.context.path}") Resource[] unfilteredProblems,
       @Value("${app.data.filtered.context.path}") Resource[] filteredByCompany) {
      resources.add(unfilteredProblems);
      resources.add(filteredByCompany);
  }

  public static void main(String[] args) {
    SpringApplication.run(LCodeLoader.class, args);
  }

  @Override
  @SneakyThrows
  public void run(String... args) {
    questionLists
      .addAll(
        handle(
          resources.get(0),
          UnfilteredSet.class,
          UnfilteredSet::getProblemsetQuestionList)
      );

    questionsListByCompany
      .addAll(
        handle(
          resources.get(1),
          FilteredSet.class,
          FilteredSet::getCompanyTag)
      );
  }

  public <X, Y> List<Y> handle( Resource[] resources,
                                Class<?> clazz,
                                Function<X,Y> func) {
    ObjectReader readerUnfilteredSet = mapper.reader(clazz).withRootName(ROOT_NAME);
    List<X> list = new ArrayList<X>();
    Arrays
      .asList(resources)
      .forEach(resource ->
        unchecked((
          __ -> list.add(readerUnfilteredSet.readValue(resource.getInputStream()))
        )
      )
    );
    return list
      .stream()
      .map(func)
      .collect(Collectors.toList());
  }
}
