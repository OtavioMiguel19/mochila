package com.otaviomiguel.softwares.mochila.genetico.usecase;

import com.otaviomiguel.softwares.mochila.core.config.MochilaCoreConfigs;
import com.otaviomiguel.softwares.mochila.core.domain.MochilaAlgoritmosEnum;
import com.otaviomiguel.softwares.mochila.core.usecase.Algoritmo;
import com.otaviomiguel.softwares.mochila.genetico.config.AlgoritmoGenericoConfigs;
import com.otaviomiguel.softwares.mochila.genetico.domain.AlgoritmoGeneticoInstancia;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

  private static boolean initied = false;
  private static boolean showHeader = true;
  private static int QUANTIDADE_MAXIMA_EXECUCOES;
  private static int QUANTIDADE_SORTEIOS_CROSSOVER;
  private static int FIT_ESPERADO;
  private static int TAMANHO_MOCHILA;
  private static boolean USAR_POPULACAO_FIXA;
  private static int TAMANHO_POPULACAO;
  private static double PROPORCAO_POPULACAO;
  private static double PROBABILIDADE_CROSSOVER;
  private static double PROBABILIDADE_MUTACAO;
  private static File csvTestResultFile;


  public void initUsual() {
    QUANTIDADE_MAXIMA_EXECUCOES = AlgoritmoGenericoConfigs.QUANTIDADE_MAXIMA_EXECUCOES;
    QUANTIDADE_SORTEIOS_CROSSOVER = AlgoritmoGenericoConfigs.QUANTIDADE_SORTEIOS_CROSSOVER;
    FIT_ESPERADO = MochilaCoreConfigs.FIT_ESPERADO;
    TAMANHO_MOCHILA = MochilaCoreConfigs.TAMANHO_MOCHILA;
    USAR_POPULACAO_FIXA = AlgoritmoGenericoConfigs.USAR_POPULACAO_FIXA;
    TAMANHO_POPULACAO = AlgoritmoGenericoConfigs.TAMANHO_POPULACAO;
    PROPORCAO_POPULACAO = AlgoritmoGenericoConfigs.PROPORCAO_POPULACAO;
    PROBABILIDADE_CROSSOVER = AlgoritmoGenericoConfigs.PROBABILIDADE_CROSSOVER;
    PROBABILIDADE_MUTACAO = AlgoritmoGenericoConfigs.PROBABILIDADE_MUTACAO;

    csvTestResultFile = null;

    initied = true;
  }

  public void initAutomatedTest(int quantidadeMaximaExecucoes, int quantidadeSorteiosCrossover, int fitEsperado,
    int tamanhoMochila, boolean usarPopulacaoFixa, int tamanhoPopulacao, double proporcaoPopulacao,
    double probabilidadeCrossOver, double probabilidadeMutacao, File csvTestResultFile) {
    QUANTIDADE_MAXIMA_EXECUCOES = quantidadeMaximaExecucoes;
    QUANTIDADE_SORTEIOS_CROSSOVER = quantidadeSorteiosCrossover;
    FIT_ESPERADO = fitEsperado;
    TAMANHO_MOCHILA = tamanhoMochila;
    USAR_POPULACAO_FIXA = usarPopulacaoFixa;
    TAMANHO_POPULACAO = tamanhoPopulacao;
    PROPORCAO_POPULACAO = proporcaoPopulacao;
    PROBABILIDADE_CROSSOVER = probabilidadeCrossOver;
    PROBABILIDADE_MUTACAO = probabilidadeMutacao;

    this.csvTestResultFile = csvTestResultFile;

    initied = true;
    showHeader = false;
  }

  @Override
  public int[] executar(final int[] valores) {

    final long initialDate = System.currentTimeMillis();

    if (!initied) {
      initUsual();
    }

    crossoversFeitos = 0;
    mutacoesFeitas = 0;
    tamanhoPopulacao = 0;
    populacao.clear();

    AlgoritmoGeneticoInstancia melhorValor = null;

    pegarTamanhoPopulacao(valores);
    gerarPopulacaoInicial(valores.length);
    analisarTodosFits(valores);

    int i;
    for (i = 0; i < QUANTIDADE_MAXIMA_EXECUCOES; i++) {
      ordenarPorFit();
      melhorValor = populacao.get(0);

      //      System.out.println(
      //        "Algoritmo Genetico - Execucao numero " + i + " - Melhor fit por enquanto: " + melhorValor.getFit());

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
          if (!populacao.contains(novaInstancia)) {
            populacao.add(novaInstancia);
          }
        });
      }

      matarMaisFracos();
    }

    if (Objects.isNull(melhorValor)) {
      melhorValor = new AlgoritmoGeneticoInstancia(new boolean[valores.length]);
    }

    final long finalDate = System.currentTimeMillis();
    final String seconds = ((int) ((finalDate - initialDate) / 1000F)) + " s";

    final String header =
      "Tempo de execucao | Iteracoes | Tamanho da entrada | Tamanho Populacao original | Tamanho Populacao no fim | "
        + "Melhor fit | Tamanho da mochila | Quantidade de sorteados para crossover | Quantidade de crossovers | "
        + "Quantidade de mutacoes";

    if (showHeader) {
      System.out.println(header);
    }

    final String divider = " | ";
    System.out.println(
      seconds + divider + i + divider + valores.length + divider + tamanhoPopulacao + divider + populacao.size()
        + divider + melhorValor.getFit() + divider + TAMANHO_MOCHILA + divider + QUANTIDADE_SORTEIOS_CROSSOVER + divider
        + crossoversFeitos + divider + mutacoesFeitas);

    final String csvDivider = ", ";
    final String csvBody = seconds + csvDivider + i + csvDivider + valores.length + csvDivider + tamanhoPopulacao + csvDivider + populacao.size()
      + csvDivider + melhorValor.getFit() + csvDivider + TAMANHO_MOCHILA + csvDivider + QUANTIDADE_SORTEIOS_CROSSOVER + csvDivider
      + crossoversFeitos + csvDivider + mutacoesFeitas;

    if (Objects.nonNull(csvTestResultFile)) {
      try {
        FileWriter pw = new FileWriter(csvTestResultFile.getAbsolutePath(), true);
        pw.append(csvBody);
        pw.append("\n");
        pw.flush();
        pw.close();
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    //    System.out.println();
    //    System.out.println();
    //    System.out.println();
    //    System.out.println("Algoritmo Genetico - Melhor valor no fim: " + Arrays.toString(melhorValor.getInstancia()));
    //    System.out.println("Algoritmo Genetico - Fit do Melhor valor no fim: " + melhorValor.getFit());
    //    System.out.println("Algoritmo Genetico - Quantidade de execucoes necessarias: " + i);
    //    System.out.println("Algoritmo Genetico - Quantidade de itens de entrada: " + valores.length);
    //    System.out.println("Algoritmo Genetico - Tamanho da populacao original: " + tamanhoPopulacao);
    //    System.out.println("Algoritmo Genetico - Tamanho da populacao no fim: " + populacao.size());
    //    System.out.println("Algoritmo Genetico - Quantidade de sorteados para crossovers (quando ha algum): "
    //      + QUANTIDADE_SORTEIOS_CROSSOVER);
    //    System.out.println("Algoritmo Genetico - Crossovers feitos: " + crossoversFeitos);
    //    System.out.println("Algoritmo Genetico - Mutacoes feitas: " + mutacoesFeitas);
    //    System.out.println();
    //    System.out.println();
    //    System.out.println();

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
    return instancia.getFit() < FIT_ESPERADO;
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

    while (sorteados.size() < QUANTIDADE_SORTEIOS_CROSSOVER) {
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

    int fit = Math.abs(pesoTotal - TAMANHO_MOCHILA);
    if (pesoTotal > TAMANHO_MOCHILA) {
      fit += 10;
    }

    instancia.setFit(fit);
  }

  private static void pegarTamanhoPopulacao(final int[] valores) {

    if (USAR_POPULACAO_FIXA) {
      tamanhoPopulacao = TAMANHO_POPULACAO;
    } else {
      tamanhoPopulacao = (int) (valores.length * PROPORCAO_POPULACAO);
    }
  }

  private static void gerarPopulacaoInicial(final int tamanhoEntrada) {
    populacao.clear();
    for (int i = 0; i < tamanhoPopulacao; i++) {
      final boolean[] instancia = new boolean[tamanhoEntrada];
      for (int j = 0; j < tamanhoEntrada; j++) {
        instancia[j] = random.nextBoolean();
      }
      populacao.add(new AlgoritmoGeneticoInstancia(instancia));
    }
  }

  private static boolean deveFazerCrossover() {
    return random.nextDouble(1) < PROBABILIDADE_CROSSOVER;
  }

  private static boolean deveMutar() {
    return random.nextDouble(1) < PROBABILIDADE_MUTACAO;
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
