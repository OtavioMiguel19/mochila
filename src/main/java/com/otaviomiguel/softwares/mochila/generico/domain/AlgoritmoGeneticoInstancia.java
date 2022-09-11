package com.otaviomiguel.softwares.mochila.generico.domain;

public class AlgoritmoGeneticoInstancia {

    private boolean[] instancia;
    private double fit;

    public AlgoritmoGeneticoInstancia(final boolean[] instancia) {
        this.instancia = instancia;
    }

    public boolean[] getInstancia() {
        return instancia;
    }

    public void setInstancia(final boolean[] instancia) {
        this.instancia = instancia;
    }

    public double getFit() {
        return fit;
    }

    public void setFit(final double fit) {
        this.fit = fit;
    }
}
