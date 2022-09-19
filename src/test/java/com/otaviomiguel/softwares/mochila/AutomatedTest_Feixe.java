package com.otaviomiguel.softwares.mochila;

import com.otaviomiguel.softwares.mochila.core.gateway.CsvReader;
import com.otaviomiguel.softwares.mochila.feixe.usecase.BuscaEmFeixe;
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
class AutomatedTest_Feixe {

  private int[] itens = new int[0];
  File csvOutputFile = new File("AutomatedTest_Feixe_Result.csv");

  @InjectMocks
  private BuscaEmFeixe buscaEmFeixe;

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
      "Tempo de execucao | Iteracoes | Tamanho da entrada | Tamanho K | Tamanho Mochila | Melhor fit";
    final String csvHeader =
      "Tempo de execucao, Iteracoes, Tamanho da entrada, Tamanho K, Tamanho Mochila, Melhor fit";

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
      buscaEmFeixe.initAutomatedTest(1000, 1, i, 1000, csvOutputFile);
      buscaEmFeixe.executar(itens);
    }
    csvOutputFile.exists();
  }

  @Test
  void testSingle() {
    final String header =
      "Tempo de execucao | Iteracoes | Tamanho da entrada | Tamanho K | Tamanho Mochila | Melhor " + "fit";
    System.out.println(header);
    for (int i = 1; i < 30; i++) {
      buscaEmFeixe.initAutomatedTest(1000, 1, 10, 1000, null);
      buscaEmFeixe.executar(itens);
    }
  }
}
