package com.otaviomiguel.softwares.mochila.feixe.domain;

import java.util.Arrays;

/**
 * @author otavio.miguel
 * @version 1.0.0
 * @since 2105
 */
public class BuscaEmFeixeInstancia {

  private boolean[] instancia;
  private int fit;

  public BuscaEmFeixeInstancia(final boolean[] instancia) {
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
    return obj instanceof BuscaEmFeixeInstancia && Arrays.equals(((BuscaEmFeixeInstancia) obj).instancia,
      this.instancia);
  }
}
