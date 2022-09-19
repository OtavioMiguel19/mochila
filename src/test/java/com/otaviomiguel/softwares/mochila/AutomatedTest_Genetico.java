package com.otaviomiguel.softwares.mochila;

import com.otaviomiguel.softwares.mochila.core.gateway.CsvReader;
import com.otaviomiguel.softwares.mochila.genetico.usecase.AlgoritmoGenetico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author otavio.miguel
 * @version 1.0.0
 * @since 2105
 */

@SpringBootTest
class AutomatedTest_Genetico {

  private int[] itens = new int[0];
  File csvOutputFile = new File("AutomatedTest_Genetico_Result.csv");

  @InjectMocks
  private AlgoritmoGenetico algoritmoGenetico;

  @BeforeEach
  void init() throws IOException {
    final File file = new File("src/main/resources/valores.csv");
    final CsvReader csvReader;
    csvReader = new CsvReader(file.getAbsolutePath());
    itens = csvReader.read();
  }

  @Test
  void test() {
    final String header =
      "Tempo de execucao | Iteracoes | Tamanho da entrada | Tamanho Populacao original | Tamanho Populacao no fim | "
        + "Melhor fit | Tamanho da mochila | Quantidade de sorteados para crossover | Quantidade de crossovers | "
        + "Quantidade de mutacoes";
    final String csvHeader =
      "Tempo de execucao, Iteracoes, Tamanho da entrada, Tamanho Populacao original, Tamanho Populacao no fim, "
        + "Melhor fit, Tamanho da mochila, Quantidade de sorteados para crossover, Quantidade de crossovers, "
        + "Quantidade de mutacoes";

    try {
      FileWriter pw = new FileWriter(csvOutputFile.getAbsolutePath(), false);
      pw.append(csvHeader);
      pw.append("\n");
      pw.flush();
      pw.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    System.out.println(header);
    for (int i = 1; i < 300; i++) {
      algoritmoGenetico.initAutomatedTest(10000000, 100, 1,
        1000, true, i, 0.0, 0.3, 0.005,
        csvOutputFile);
      algoritmoGenetico.executar(itens);
    }
  }

  @Test
  void testSingle() {
    final String header =
      "Tempo de execucao | Iteracoes | Tamanho da entrada | Tamanho Populacao original | Tamanho Populacao no fim | "
        + "Melhor fit | Tamanho da mochila | Quantidade de sorteados para crossover | Quantidade de crossovers | "
        + "Quantidade de mutacoes";
    System.out.println(header);
    algoritmoGenetico.initAutomatedTest(10000000, 100, 1,
      1000, true, 100, 0.0, 0.3,
      0.005, null);
    algoritmoGenetico.executar(itens);
  }
}
