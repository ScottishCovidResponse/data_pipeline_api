package uk.ramp.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.ramp.estimate.ImmutableEstimate;
import uk.ramp.distribution.Distribution;
import uk.ramp.objects.StandardArrayDataReader;
import uk.ramp.parameters.Component;
import uk.ramp.parameters.ParameterDataWriter;
import uk.ramp.file.CleanableFileChannel;
import uk.ramp.parameters.ParameterDataReader;
import uk.ramp.samples.Samples;

public class StandardApiTest {
  private FileApi fileApi;
  private ParameterDataReader parameterDataReader;
  private StandardArrayDataReader standardArrayDataReader;
  private ParameterDataWriter parameterDataWriter;
  private CleanableFileChannel fileChannel;
  private Distribution distribution;
  private Samples samples;
  private Component component;

  @Before
  public void setUp() throws Exception {
    this.fileApi = mock(FileApi.class);
    this.parameterDataReader = mock(ParameterDataReader.class);
    this.parameterDataWriter = mock(ParameterDataWriter.class);
    this.fileChannel = mock(CleanableFileChannel.class);
    this.distribution = mock(Distribution.class);
    this.samples = mock(Samples.class);
    this.component = mock(Component.class);
    this.standardArrayDataReader = mock(StandardArrayDataReader.class);

    when(fileApi.openForRead(any())).thenReturn(fileChannel);
    when(fileApi.openForWrite(any())).thenReturn(fileChannel);
    when(parameterDataReader.read(fileChannel, "component")).thenReturn(component);
  }

  @Test
  public void readEstimate() {
    when(component.getEstimate()).thenReturn(5);
    var api = new StandardApi(fileApi, parameterDataReader, parameterDataWriter, standardArrayDataReader);
    assertThat(api.readEstimate("dataProduct", "component")).isEqualTo(5);
  }

  @Test
  public void writeEstimate() {
    var api = new StandardApi(fileApi, parameterDataReader, parameterDataWriter, standardArrayDataReader);
    api.writeEstimate("dataProduct", "component", 5);

    var expectedParameterData = ImmutableEstimate.builder()
        .internalValue(5)
        .build();
    verify(parameterDataWriter).write(fileChannel, "component", expectedParameterData);
  }

  @Test
  public void readDistribution() {
    when(component.getDistribution()).thenReturn(distribution);
    var api = new StandardApi(fileApi, parameterDataReader, parameterDataWriter, standardArrayDataReader);
    assertThat(api.readDistribution("dataProduct", "component"))
        .isEqualTo(distribution);
  }

  @Test
  public void writeDistribution() {
    var api = new StandardApi(fileApi, parameterDataReader, parameterDataWriter, standardArrayDataReader);
    api.writeDistribution("dataProduct", "component", distribution);
    verify(parameterDataWriter).write(fileChannel, "component", distribution);
  }

  @Test
  public void readSample() {
    when(component.getSample()).thenReturn(5);
    var api = new StandardApi(fileApi, parameterDataReader, parameterDataWriter, standardArrayDataReader);
    assertThat(api.readSample("dataProduct", "component")).isEqualTo(5);
  }

  @Test
  public void writeSamples() {
    var api = new StandardApi(fileApi, parameterDataReader, parameterDataWriter, standardArrayDataReader);
    api.writeSamples("dataProduct", "component", samples);
    verify(parameterDataWriter).write(fileChannel, "component", samples);
  }

  @Test
  @Ignore // TODO additional functionality to implement for future improvement
  public void writeMultipleEstimatesSameKey() {
    var api = new StandardApi(fileApi, parameterDataReader, parameterDataWriter, standardArrayDataReader);
    api.writeEstimate("dataProduct", "component", 1);
    assertThatIllegalStateException()
        .isThrownBy(() -> api.writeEstimate("dataProduct", "component", 2));
  }

  @Test
  @Ignore // TODO additional functionality to implement for future improvement
  public void writeMultipleEstimatesSameValue() {
    var api = new StandardApi(fileApi, parameterDataReader, parameterDataWriter, standardArrayDataReader);
    api.writeEstimate("dataProduct", "component", 1);
    assertThatIllegalStateException()
        .isThrownBy(() -> api.writeEstimate("dataProduct", "component", 1));
  }

  @Test
  public void writeMultipleEstimatesDifferentKey() {
    var api = new StandardApi(fileApi, parameterDataReader, parameterDataWriter, standardArrayDataReader);
    var expectedParameterData1 = ImmutableEstimate.builder()
        .internalValue(1)
        .build();
    var expectedParameterData2 = ImmutableEstimate.builder()
        .internalValue(2)
        .build();
    api.writeEstimate("dataProduct", "component1", 1);
    api.writeEstimate("dataProduct", "component2", 2);
    verify(parameterDataWriter).write(fileChannel, "component1", expectedParameterData1);
    verify(parameterDataWriter).write(fileChannel, "component2", expectedParameterData2);
  }

  @Test
  @Ignore // Not implemented yet
  public void readArray() {
    var api = new StandardApi(fileApi, parameterDataReader, parameterDataWriter, standardArrayDataReader);
    assertThat(api.readArray("dataProduct", "component").as1DArray())
        .containsExactly(1, 2, 3);
  }

  @Test
  @Ignore // Not implemented yet
  public void writeTable() {
    throw new UnsupportedOperationException();

  }

  @Test
  @Ignore // Not implemented yet
  public void readTable() {
    throw new UnsupportedOperationException();

  }

  @Test
  @Ignore // Not implemented yet
  public void writeArray() {
    throw new UnsupportedOperationException();
  }
}