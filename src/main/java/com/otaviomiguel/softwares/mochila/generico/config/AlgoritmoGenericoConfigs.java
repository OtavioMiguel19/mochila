package com.otaviomiguel.softwares.mochila.generico.config;

import com.otaviomiguel.softwares.mochila.core.domain.MochilaAlgoritmosEnum;

/**
 * @author otavio.miguel
 * @version 1.0.0
 * @since 1808
 */
public class AlgoritmoGenericoConfigs {

  public static final MochilaAlgoritmosEnum TIPO_ALGORITMO = MochilaAlgoritmosEnum.ALGORITMO_GENETICO;

  public static final boolean USAR_POPULACAO_FIXA = true;
  public static final int TAMANHO_POPULACAO = 10;
  public static final int TAMANHO_MOCHILA = 100;
  public static final double PROPORCAO_POPULACAO = 0.1;
  public static final double PROBABILIDADE_CROSSOVER = 0.3;
  public static final double PROBABILIDADE_MUTACAO = 0.003;
  public static final int FIT_ESPERADO = 10;

}
