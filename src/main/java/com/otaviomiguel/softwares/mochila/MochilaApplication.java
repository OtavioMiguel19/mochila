package com.otaviomiguel.softwares.mochila;

import com.otaviomiguel.softwares.mochila.core.config.MochilaCoreConfigs;
import com.otaviomiguel.softwares.mochila.core.gateway.CsvReader;
import com.otaviomiguel.softwares.mochila.core.usecase.Algoritmo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

@SpringBootApplication
public class MochilaApplication {

  private static Set<Algoritmo> algoritmos;

  public MochilaApplication(final Set<Algoritmo> algoritmos) {
    MochilaApplication.algoritmos = algoritmos;
  }

  public static void main(String[] args) throws IOException {
    SpringApplication.run(MochilaApplication.class, args);

    final File file = new File("src/main/resources/valores.csv");

    final CsvReader csvReader;
    csvReader = new CsvReader(file.getAbsolutePath());
    final int[] itens = csvReader.read();


    int[] resultado = algoritmos.stream()
      .filter(algoritmo -> algoritmo.get().equals(MochilaCoreConfigs.ALGORITMO))
      .findFirst()
      .orElseThrow()
      .executar(itens);

    System.out.println(Arrays.toString(resultado));


  }

}
