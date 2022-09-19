package com.otaviomiguel.softwares.mochila.feixe.usecase;

import com.otaviomiguel.softwares.mochila.core.config.MochilaCoreConfigs;
import com.otaviomiguel.softwares.mochila.core.domain.MochilaAlgoritmosEnum;
import com.otaviomiguel.softwares.mochila.core.usecase.Algoritmo;
import com.otaviomiguel.softwares.mochila.feixe.configs.BuscaEmFeixeConfigs;
import com.otaviomiguel.softwares.mochila.feixe.domain.BuscaEmFeixeInstancia;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author otavio.miguel
 * @version 1.0.0
 * @since 2105
 */
@Component
public class BuscaEmFeixe implements Algoritmo {

  private static final Random random = new Random();
  private static final List<BuscaEmFeixeInstancia> estados = new ArrayList<>();

  private static boolean initied = false;
  private static boolean showHeader = true;
  private static int TAMANHO_MOCHILA;
  private static int FIT_ESPERADO;
  private static int NUMERO_ESTADOS_K;
  private static int QUANTIDADE_MAXIMA_EXECUCOES;
  private static File csvTestResultFile;

  public void initUsual() {
    TAMANHO_MOCHILA = MochilaCoreConfigs.TAMANHO_MOCHILA;
    FIT_ESPERADO = MochilaCoreConfigs.FIT_ESPERADO;
    NUMERO_ESTADOS_K = BuscaEmFeixeConfigs.NUMERO_ESTADOS_K;
    QUANTIDADE_MAXIMA_EXECUCOES = BuscaEmFeixeConfigs.QUANTIDADE_MAXIMA_EXECUCOES;

    csvTestResultFile = null;

    initied = true;
  }

  public void initAutomatedTest(int tamanhoMochila, int fitEsperado, int numeroEstadosK, int quantidadeMacimaExecucoes,
    File csvTestResultFile) {
    TAMANHO_MOCHILA = tamanhoMochila;
    FIT_ESPERADO = fitEsperado;
    NUMERO_ESTADOS_K = numeroEstadosK;
    QUANTIDADE_MAXIMA_EXECUCOES = quantidadeMacimaExecucoes;

    this.csvTestResultFile = csvTestResultFile;

    initied = true;
    showHeader = false;
  }

  @Override
  public int[] executar(int[] valores) {

    final long initialDate = System.currentTimeMillis();

    if (!initied) {
      initUsual();
    }

    BuscaEmFeixeInstancia melhorFit = null;

    gerarEstadosIniciais(valores.length);
    analisarTodosFits(valores);

    int i;
    for (i = 0; i < QUANTIDADE_MAXIMA_EXECUCOES; i++) {
      ordenarPorFit(estados);
      melhorFit = estados.get(0);

      if (fitEsperado(melhorFit)) {
        break;
      }

      gerarSucessores(valores);
      removerPiores(estados);
    }

    if (Objects.isNull(melhorFit)) {
      melhorFit = new BuscaEmFeixeInstancia(new boolean[valores.length]);
    }

    final long finalDate = System.currentTimeMillis();
    final int seconds = (int) ((finalDate - initialDate) / 1000F);
    //    System.out.println("Tempo de execucao - " + seconds + " segundos.");

    final String header =
      "Tempo de execucao | Iteracoes | Tamanho da entrada | Tamanho K | Tamanho Mochila | Melhor " + "fit";
    final String body =
      seconds + " s | " + i + " | " + valores.length + " | " + NUMERO_ESTADOS_K + " | " + TAMANHO_MOCHILA + " | "
        + melhorFit.getFit();
    final String csvbody =
      seconds + " s, " + i + ", " + valores.length + ", " + NUMERO_ESTADOS_K + ", " + TAMANHO_MOCHILA + ", "
        + melhorFit.getFit();

    //    System.out.println();
    //    System.out.println();
    //    System.out.println();
    if (showHeader) {
      System.out.println(header);
    }
    System.out.println(body);

    if (Objects.nonNull(csvTestResultFile)) {
      try {
        FileWriter pw = new FileWriter(csvTestResultFile.getAbsolutePath(), true);
        pw.append(csvbody);
        pw.append("\n");
        pw.flush();
        pw.close();
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    //    System.out.println("Busca em feixe - Melhor valor no fim: " + Arrays.toString(melhorFit.getInstancia()));
    //    System.out.println("Busca em feixe - Fit do Melhor valor no fim: " + melhorFit.getFit());
    //    System.out.println("Busca em feixe - Quantidade de execucoes necessarias: " + i);
    //    System.out.println("Busca em feixe - Quantidade de itens de entrada: " + valores.length);
    //    System.out.println();
    //    System.out.println();
    //    System.out.println();

    return converterParaValores(melhorFit.getInstancia(), valores);
  }

  private static void removerPiores(final List<BuscaEmFeixeInstancia> list) {
    ordenarPorFit(list);
    while (list.size() > NUMERO_ESTADOS_K) {
      list.remove(list.size() - 1);
    }
  }

  private static boolean fitEsperado(final BuscaEmFeixeInstancia instancia) {
    return instancia.getFit() < FIT_ESPERADO;
  }

  private static void gerarSucessores(int[] valores) {
    final List<BuscaEmFeixeInstancia> todosSucessores = new ArrayList<>();
    estados.parallelStream().forEach(estado -> {
      final List<BuscaEmFeixeInstancia> sucessores = new ArrayList<>();
      for (int i = 0; i < valores.length; i++) {
        boolean[] sucessor = Arrays.copyOf(estado.getInstancia(), estado.getInstancia().length);
        inverterBit(sucessor, i);
        BuscaEmFeixeInstancia instancia = new BuscaEmFeixeInstancia(sucessor);
        analisarFit(valores, instancia);
        if (!sucessores.contains(instancia)) {
          sucessores.add(instancia);
        }
      }
      ordenarPorFit(sucessores);
      todosSucessores.add(sucessores.get(0));
    });
    estados.addAll(todosSucessores.parallelStream().filter(e -> !estados.contains(e)).toList());
  }

  private static void inverterBit(final boolean[] instancia, final int posicao) {
    instancia[posicao] = !instancia[posicao];
  }

  private static void ordenarPorFit(final List<BuscaEmFeixeInstancia> list) {
    list.sort(Comparator.comparing(BuscaEmFeixeInstancia::getFit));
  }

  private static void gerarEstadosIniciais(final int tamanhoEntrada) {
    estados.clear();
    for (int i = 0; i < NUMERO_ESTADOS_K; i++) {
      final boolean[] instancia = new boolean[tamanhoEntrada];
      for (int j = 0; j < tamanhoEntrada; j++) {
        instancia[j] = random.nextBoolean();
      }
      estados.add(new BuscaEmFeixeInstancia(instancia));
    }
  }

  private static void analisarTodosFits(final int[] valores) {
    estados.parallelStream().forEach(instancia -> analisarFit(valores, instancia));
  }

  private static void analisarFit(final int[] valores, final BuscaEmFeixeInstancia instancia) {
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

  private static int[] converterParaValores(final boolean[] instancia, final int[] valores) {
    final int[] retorno = new int[instancia.length];
    for (int i = 0; i < instancia.length; i++) {
      if (instancia[i]) {
        retorno[i] = valores[i];
      }
    }

    return retorno;
  }

  @Override
  public MochilaAlgoritmosEnum get() {
    return MochilaAlgoritmosEnum.BUSCA_EM_FEIXE;
  }
}
