package com.otaviomiguel.softwares.mochila.generico.domain;

/**
 * @author otavio.miguel
 * @version 1.0.0
 * @since 1808
 */
public class ModeloGenetico {

  int[] valores;
  int qtyValores;

  int tamanhoMochila;

  int[] resultado;
  int qtyResultado;
  int tamanhoResultado;

  public ModeloGenetico(final int[] valores) {
    this.valores = valores;
    this.qtyValores = valores.length;
  }

}
