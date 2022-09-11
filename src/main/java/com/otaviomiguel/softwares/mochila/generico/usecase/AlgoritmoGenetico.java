package com.otaviomiguel.softwares.mochila.generico.usecase;

import com.otaviomiguel.softwares.mochila.core.domain.MochilaAlgoritmosEnum;
import com.otaviomiguel.softwares.mochila.core.usecase.Algoritmo;
import com.otaviomiguel.softwares.mochila.generico.config.AlgoritmoGenericoConfigs;
import com.otaviomiguel.softwares.mochila.generico.domain.AlgoritmoGeneticoInstancia;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author otavio.miguel
 * @version 1.0.0
 * @since 1808
 */
@Component
public class AlgoritmoGenetico implements Algoritmo {

  private static final Random random = new Random();
  private static final List<AlgoritmoGeneticoInstancia> populacao = new ArrayList<>();

  private static int tamanhoPopulacao = 0;
  private static int crossoversFeitos = 0;
  private static int mutacoesFeitas = 0;

  @Override
  public int[] executar(final int[] valores) {

    AlgoritmoGeneticoInstancia melhorValor = null;

    pegarTamanhoPopulacao(valores);
    gerarPopulacaoInicial(valores.length);
    analisarTodosFits(valores);

    int i;
    for (i = 0; i < AlgoritmoGenericoConfigs.QUANTIDADE_MAXIMA_EXECUCOES; i++) {
      ordenarPorFit();
      melhorValor = populacao.get(0);

      System.out.println("Execucao numero " + i + " - Melhor fit por enquanto: " + melhorValor.getFit());

      if (fitEsperado(melhorValor)) {
        break;
      }

      if (deveFazerCrossover()) {
        final Set<boolean[]> crossovers = fazerCrossoverAleatorio(sortearInstancia());
        crossovers.parallelStream().forEach(crossover -> {
          final AlgoritmoGeneticoInstancia novaInstancia = new AlgoritmoGeneticoInstancia(crossover);
          if (deveMutar()) {
            mutar(novaInstancia);
          }
          analisarFit(valores, novaInstancia);
          populacao.add(novaInstancia);
        });
      }

      matarMaisFracos();
    }

    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println("Melhor valor no fim: " + Arrays.toString(melhorValor.getInstancia()));
    System.out.println("Fit do Melhor valor no fim: " + melhorValor.getFit());
    System.out.println("Quantidade de execucoes necessarias: " + i);
    System.out.println("Quantidade de itens de entrada: " + valores.length);
    System.out.println("Tamanho da populacao original: " + tamanhoPopulacao);
    System.out.println("Tamanho da populacao no fim: " + populacao.size());
    System.out.println("Quantidade de sorteados para crossovers (quando ha algum): " + AlgoritmoGenericoConfigs.QUANTIDADE_SORTEIOS_CROSSOVER);
    System.out.println("Crossovers feitos: " + crossoversFeitos);
    System.out.println("Mutacoes feitas: " + mutacoesFeitas);
    System.out.println();
    System.out.println();
    System.out.println();

    return converterParaValores(melhorValor.getInstancia(), valores);
  }

  private static void matarMaisFracos() {
    ordenarPorFit();
    while (populacao.size() > tamanhoPopulacao * 2) {
      populacao.remove(populacao.size() - 1);
    }
  }

  private static Set<boolean[]> fazerCrossoverAleatorio(final List<AlgoritmoGeneticoInstancia> opcoes) {
    final int quantidadeDeCrossovers = random.nextInt(opcoes.size());
    final Set<boolean[]> crossovers = new HashSet<>();

    for (int i = 0; i < quantidadeDeCrossovers; i++) {
      int pai = random.nextInt(opcoes.size());
      int mae = random.nextInt(opcoes.size());
      while (mae == pai) {
        mae = random.nextInt(opcoes.size());
      }
      final boolean[] crossover = fazerCrossover((AlgoritmoGeneticoInstancia) opcoes.toArray()[mae],
        (AlgoritmoGeneticoInstancia) opcoes.toArray()[pai]);
      crossovers.add(crossover);
    }

    return crossovers;
  }

  private static int[] converterParaValores(final boolean[] instancia, final int[] valores) {
    final int[] retorno = new int[instancia.length];
    for (int i = 0; i < instancia.length; i++) {
      if (instancia[i]) {
        retorno[i] = valores[i];
      }
    }

    return retorno;
  }

  private static boolean fitEsperado(final AlgoritmoGeneticoInstancia instancia) {
    return instancia.getFit() < AlgoritmoGenericoConfigs.FIT_ESPERADO;
  }

  private static boolean[] fazerCrossover(final AlgoritmoGeneticoInstancia instancia1,
    final AlgoritmoGeneticoInstancia instancia2) {
    final int divisor = random.nextInt(instancia1.getInstancia().length);

    final boolean[] gerado = new boolean[instancia1.getInstancia().length];

    for (int i = 0; i < divisor; i++) {
      gerado[i] = instancia1.getInstancia()[i];
    }
    for (int i = divisor; i < instancia2.getInstancia().length; i++) {
      gerado[i] = instancia2.getInstancia()[i];
    }

    crossoversFeitos++;
    return gerado;
  }

  private static void ordenarPorFit() {
    populacao.sort(Comparator.comparing(AlgoritmoGeneticoInstancia::getFit));
  }

  private static List<AlgoritmoGeneticoInstancia> sortearInstancia() {
    final List<AlgoritmoGeneticoInstancia> aSortear = new ArrayList<>();
    final List<AlgoritmoGeneticoInstancia> sorteados = new ArrayList<>();
    final double maiorFit = populacao.get(populacao.size() - 1).getFit();

    for (int j = 0; j < populacao.size() / 2; j++) {
      final AlgoritmoGeneticoInstancia instancia = populacao.get(j);
      for (int i = 0; i < maiorFit - instancia.getFit(); i++) {
        aSortear.add(instancia);
      }
    }

    while (sorteados.size() < AlgoritmoGenericoConfigs.QUANTIDADE_SORTEIOS_CROSSOVER) {
      if (CollectionUtils.isEmpty(aSortear)) {
        sorteados.add(populacao.get(random.nextInt(populacao.size())));
      } else {
        sorteados.add(aSortear.get(random.nextInt(aSortear.size())));
      }
    }

    return sorteados;
  }

  private static void analisarTodosFits(final int[] valores) {
    populacao.parallelStream().forEach(instancia -> analisarFit(valores, instancia));
  }

  private static void analisarFit(final int[] valores, final AlgoritmoGeneticoInstancia instancia) {
    int pesoTotal = 0;
    int tamanho = valores.length;

    for (int i = 0; i < tamanho; i++) {
      if (instancia.getInstancia()[i]) {
        pesoTotal += valores[i];
      }
    }

    int fit = Math.abs(pesoTotal - AlgoritmoGenericoConfigs.TAMANHO_MOCHILA);
    if (pesoTotal > AlgoritmoGenericoConfigs.TAMANHO_MOCHILA) {
      fit += 10;
    }

    instancia.setFit(fit);
  }

  private static void pegarTamanhoPopulacao(final int[] valores) {

    if (AlgoritmoGenericoConfigs.USAR_POPULACAO_FIXA) {
      tamanhoPopulacao = AlgoritmoGenericoConfigs.TAMANHO_POPULACAO;
    } else {
      tamanhoPopulacao = (int) (valores.length * AlgoritmoGenericoConfigs.PROPORCAO_POPULACAO);
    }
  }

  private static void gerarPopulacaoInicial(final int tamanho) {
    populacao.clear();
    for (int i = 0; i < tamanhoPopulacao; i++) {
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
    final int quantidadeMutacoes = random.nextInt(instancia.getInstancia().length);
    for (int i = 0; i < quantidadeMutacoes; i++) {
      inverterBit(instancia.getInstancia(), random.nextInt(instancia.getInstancia().length));
    }
    mutacoesFeitas++;
  }

  private static void inverterBit(final boolean[] instancia, final int posicao) {
    instancia[posicao] = !instancia[posicao];
  }

  @Override
  public MochilaAlgoritmosEnum get() {
    return MochilaAlgoritmosEnum.ALGORITMO_GENETICO;
  }
}
