package uk.ramp.parameters;

import uk.ramp.distribution.Distribution;

public interface ReadComponent {
  Number getEstimate();

  Number getSample();

  Distribution getDistribution();
}
