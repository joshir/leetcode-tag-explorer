package com.joshir.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;



@Component
public class JsonMapper {
  private static final String ROOT_NAME;
  public static final ObjectMapper mapper;

  static {
    ROOT_NAME = "data";
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
  }

  /**
   * Use Class<?> to read X into a list of X
   * and eventually transform it with Func<X,Y>
   * into List<Y>
   * */
  @SuppressWarnings("unchecked")
  public <X, Y> List<Y> loadResourceAsList(Resource[] resources,
                                                   Class<?> clazz,
                                                   Function<X, Y> func) {
    ObjectReader readerUnfilteredSet = mapper.readerFor(clazz).withRootName(ROOT_NAME);
    List<X> list = new ArrayList<>();
    Arrays
      .asList(resources)
      .forEach(resource -> {
        try {
          list.add(readerUnfilteredSet.readValue(resource.getInputStream()));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });

    return list
      .stream()
      .flatMap(func.andThen(s-> ((List<Y>) s).stream()))
      .collect(Collectors.toList());
  }

  @SneakyThrows
  public <T> T readFromJson(String json, Class<T> clazz){
      return mapper.readValue(json, clazz);
  }

  @SneakyThrows
  public String writeToJson(Object obj) { return mapper.writeValueAsString(obj); }
}
