package uk.ramp.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

public class BaseYamlWriter implements YamlWriter {
  private final ObjectMapper yamlMapper;

  BaseYamlWriter() {
    this.yamlMapper =
        new YAMLMapper().disable(Feature.WRITE_DOC_START_MARKER).registerModule(new Jdk8Module());
  }

  @Override
  public <T> void write(Writer writer, T data) {
    try {
      yamlMapper.writeValue(writer, data);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
