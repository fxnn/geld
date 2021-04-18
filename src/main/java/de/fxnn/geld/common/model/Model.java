package de.fxnn.geld.common.model;

public interface Model {

  /** Re-calculate the values of all transient properties of this class. */
  void updateTransientProperties();
}
