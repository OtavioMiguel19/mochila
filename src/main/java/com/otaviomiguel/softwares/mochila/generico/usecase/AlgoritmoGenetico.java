package com.otaviomiguel.softwares.mochila.generico.usecase;

import com.otaviomiguel.softwares.mochila.core.domain.MochilaAlgoritmosEnum;
import com.otaviomiguel.softwares.mochila.core.usecase.Algoritmo;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author otavio.miguel
 * @version 1.0.0
 * @since 1808
 */
@Component
public class AlgoritmoGenetico implements Algoritmo {

  @Override
  public int[] executar(final int[] valores) {
    return new int[0];
  }

  @Override
  public MochilaAlgoritmosEnum get() {
    return MochilaAlgoritmosEnum.ALGORITMO_GENETICO;
  }
}
