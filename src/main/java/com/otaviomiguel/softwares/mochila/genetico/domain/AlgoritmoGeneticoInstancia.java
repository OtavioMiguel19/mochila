package com.otaviomiguel.softwares.mochila.genetico.domain;

import java.util.Arrays;

public class AlgoritmoGeneticoInstancia {

  private boolean[] instancia;
  private int fit;

  public AlgoritmoGeneticoInstancia(final boolean[] instancia) {
    this.instancia = instancia;
  }

  public boolean[] getInstancia() {
    return instancia;
  }

  public void setInstancia(final boolean[] instancia) {
    this.instancia = instancia;
  }

  public int getFit() {
    return fit;
  }

  public void setFit(final int fit) {
    this.fit = fit;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof AlgoritmoGeneticoInstancia && Arrays.equals(((AlgoritmoGeneticoInstancia) obj).instancia,
      this.instancia);
  }
}
