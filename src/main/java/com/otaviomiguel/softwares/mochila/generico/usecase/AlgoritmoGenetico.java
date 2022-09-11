package com.otaviomiguel.softwares.mochila.generico.usecase;

import com.otaviomiguel.softwares.mochila.core.domain.MochilaAlgoritmosEnum;
import com.otaviomiguel.softwares.mochila.core.usecase.Algoritmo;
import com.otaviomiguel.softwares.mochila.generico.config.AlgoritmoGenericoConfigs;
import com.otaviomiguel.softwares.mochila.generico.domain.AlgoritmoGeneticoInstancia;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.sqrt;

/**
 * @author otavio.miguel
 * @version 1.0.0
 * @since 1808
 */
@Component
public class AlgoritmoGenetico implements Algoritmo {

  private static final Random random = new Random();
  private static final List<AlgoritmoGeneticoInstancia> populacao = new ArrayList<>();

  @Override
  public int[] executar(final int[] valores) {

    gerarPopulacaoInicial(valores.length);
    analisarTodosFits(valores);

    deveFazerCrossover();
    deveMutar();

    mutar(populacao.get(1));

    return new int[0];
  }

  private static void analisarTodosFits(final int[] valores) {
    for (AlgoritmoGeneticoInstancia instancia : populacao) {
      analisarFit(valores, instancia);
    }
  }

  private static void analisarFit(final int[] valores, final AlgoritmoGeneticoInstancia instancia) {
    int pesoTotal = 0;
    int tamanho = valores.length;

    for (int i = 0; i < tamanho; i++) {
      if (instancia.getInstancia()[i]) {
        pesoTotal += valores[i];
      }
    }

    double fit = Math.abs((double) pesoTotal - AlgoritmoGenericoConfigs.TAMANHO_MOCHILA);
    if (pesoTotal > AlgoritmoGenericoConfigs.TAMANHO_MOCHILA) {
      fit+=10;
    }

    instancia.setFit(fit);
  }

  private static void gerarPopulacaoInicial(final int tamanho) {
    populacao.clear();
    for (int i = 0; i < AlgoritmoGenericoConfigs.TAMANHO_POPULACAO; i++) {
      final boolean[] instancia = new boolean[tamanho];
      for (int j = 0; j < tamanho; j++) {
        instancia[j] = random.nextBoolean();
      }
      populacao.add(new AlgoritmoGeneticoInstancia(instancia));
    }
  }

  private static boolean deveFazerCrossover() {
    return random.nextDouble(1) < AlgoritmoGenericoConfigs.PROBABILIDADE_CROSSOVER;
  }

  private static boolean deveMutar() {
    return random.nextDouble(1) < AlgoritmoGenericoConfigs.PROBABILIDADE_MUTACAO;
  }

  private static void mutar(final AlgoritmoGeneticoInstancia instancia) {
    final int quantidadeMutacoes = random.nextInt(instancia.getInstancia().length / 3);
    for (int i = 0; i < quantidadeMutacoes; i++) {
      inverterBit(instancia.getInstancia(), random.nextInt(instancia.getInstancia().length));
    }
  }

  private static void inverterBit(final boolean[] instancia, final int posicao) {
    instancia[posicao] = !instancia[posicao];
  }

  @Override
  public MochilaAlgoritmosEnum get() {
    return MochilaAlgoritmosEnum.ALGORITMO_GENETICO;
  }
}
