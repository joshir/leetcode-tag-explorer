package com.joshir.domain.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public class JsonMapper {
  private static final String ROOT_NAME;
  private static final ObjectMapper mapper;

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
  public static <X, Y>  List<Y> loadResourceAsList(Resource[] resources,
                                                   Class<?> clazz,
                                                   Function<X, Y> func) {
    ObjectReader readerUnfilteredSet = mapper.reader(clazz).withRootName(ROOT_NAME);
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
      .map(func)
      .collect(Collectors.toList());
  }

  // todo handle exception
  @SneakyThrows
  public static <T> T readFromJson(String json, Class<T> clazz){
      return mapper.readValue(json, clazz);
  }

  // todo handle exception
  @SneakyThrows
  public static String writeToJson(Object obj) {
    return mapper.writeValueAsString(obj);
  }
}
