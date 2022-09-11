package com.otaviomiguel.softwares.mochila.generico.usecase;

import com.otaviomiguel.softwares.mochila.core.domain.MochilaAlgoritmosEnum;
import com.otaviomiguel.softwares.mochila.core.usecase.Algoritmo;
import com.otaviomiguel.softwares.mochila.generico.config.AlgoritmoGenericoConfigs;
import com.otaviomiguel.softwares.mochila.generico.domain.AlgoritmoGeneticoInstancia;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

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

    AlgoritmoGeneticoInstancia melhorValor = null;

    gerarPopulacaoInicial(valores.length);
    analisarTodosFits(valores);


    for (int i = 0; i < 100000; i++) {
      ordenarPorFit();
      melhorValor = populacao.get(0);

      if (fitEsperado(melhorValor)) {
        break;
      }

      Set<AlgoritmoGeneticoInstancia> sorteados = sortearInstancia(2);
      if (deveFazerCrossover()) {
        final boolean[] crossover = fazerCrossover((AlgoritmoGeneticoInstancia) sorteados.toArray()[0], (AlgoritmoGeneticoInstancia) sorteados.toArray()[1]);
        final AlgoritmoGeneticoInstancia novaInstancia = new AlgoritmoGeneticoInstancia(crossover);
        analisarFit(valores, novaInstancia);
        if (deveMutar()) {
          mutar(novaInstancia);
          analisarFit(valores, novaInstancia);
        }
        analisarFit(valores, novaInstancia);
        populacao.remove(populacao.size() - 1);
        populacao.add(novaInstancia);
      }
    }

    System.out.println("Melhor valor no fim: " + Arrays.toString(melhorValor.getInstancia()));
    System.out.println("Fit do Melhor valor no fim: " + melhorValor.getFit());



    return new int[0];
  }

  private static boolean fitEsperado(final AlgoritmoGeneticoInstancia instancia) {
    return instancia.getFit() < AlgoritmoGenericoConfigs.FIT_ESPERADO;
  }

  private static boolean[] fazerCrossover(final AlgoritmoGeneticoInstancia instancia1, final AlgoritmoGeneticoInstancia instancia2) {
    final int divisor = random.nextInt(instancia1.getInstancia().length);

    final boolean[] gerado = new boolean[instancia1.getInstancia().length];

    for (int i = 0; i < divisor; i++) {
      gerado[i] = instancia1.getInstancia()[i];
    }
    for (int i = divisor; i < instancia2.getInstancia().length; i++) {
      gerado[i] = instancia2.getInstancia()[i];
    }

    return gerado;
  }

  private static void ordenarPorFit() {
    populacao.sort(Comparator.comparing(AlgoritmoGeneticoInstancia::getFit));
  }

  private static Set<AlgoritmoGeneticoInstancia> sortearInstancia(final int quantidadeDeSorteios) {
    final List<AlgoritmoGeneticoInstancia> aSortear = new ArrayList<>();
    final Set<AlgoritmoGeneticoInstancia> sorteados = new HashSet<>();
    final double maiorFit = populacao.get(populacao.size() - 1).getFit();

    for (final AlgoritmoGeneticoInstancia instancia : populacao) {
      for (int i = 0; i < maiorFit - instancia.getFit(); i++) {
        aSortear.add(instancia);
      }
    }

    while (sorteados.size() < quantidadeDeSorteios) {
      if (CollectionUtils.isEmpty(aSortear)) {
        sorteados.add(populacao.get(random.nextInt(populacao.size())));
      } else {
        sorteados.add(aSortear.get(random.nextInt(aSortear.size())));
      }
    }

    return sorteados;
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
