package com.otaviomiguel.softwares.mochila;

import com.otaviomiguel.softwares.mochila.core.config.MochilaCoreConfigs;
import com.otaviomiguel.softwares.mochila.core.gateway.CsvReader;
import com.otaviomiguel.softwares.mochila.core.usecase.Algoritmo;
import com.otaviomiguel.softwares.mochila.generico.usecase.AlgoritmoGenetico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Set;

@SpringBootApplication
public class MochilaApplication {

  private static Set<Algoritmo> algoritmos;

  public MochilaApplication(final Set<Algoritmo> algoritmos) {
    MochilaApplication.algoritmos = algoritmos;
  }

  public static void main(String[] args) throws IOException {
    SpringApplication.run(MochilaApplication.class, args);

    final CsvReader csvReader;
    csvReader = new CsvReader("C:\\Projetos\\mochila\\src\\main\\resources\\valores.csv");
    final int[] itens = csvReader.read();

    int[] resultado = algoritmos.stream()
      .filter(algoritmo -> algoritmo.get().equals(MochilaCoreConfigs.ALGORITMO))
      .findFirst()
      .orElseThrow()
      .executar(itens);

    System.out.println(resultado.toString());
  }

}
