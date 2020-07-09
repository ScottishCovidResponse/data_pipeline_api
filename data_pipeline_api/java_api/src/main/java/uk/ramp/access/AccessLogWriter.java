package uk.ramp.access;

import uk.ramp.writer.Writer;
import uk.ramp.yaml.YamlWriter;

class AccessLogWriter implements Writer<ImmutableAccessLog> {
  private final YamlWriter yamlWriter;
  private final java.io.Writer writer;

  AccessLogWriter(YamlWriter yamlWriter, java.io.Writer writer) {
    this.writer = writer;
    this.yamlWriter = yamlWriter;
  }

  @Override
  public void write(ImmutableAccessLog data) {
    yamlWriter.write(writer, data);
  }
}
