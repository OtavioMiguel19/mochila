package com.otaviomiguel.softwares.mochila.core.usecase;

import com.otaviomiguel.softwares.mochila.core.domain.MochilaAlgoritmosEnum;

/**
 * @author otavio.miguel
 * @version 1.0.0
 * @since 1808
 */
public interface Algoritmo {
  int[] executar(int[] valores);

  MochilaAlgoritmosEnum get();

}
