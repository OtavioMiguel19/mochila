package com.otaviomiguel.softwares.mochila.generico.domain;

public class AlgoritmoGeneticoInstancia {

    private boolean[] instancia;
    private int fit;

    public AlgoritmoGeneticoInstancia(final boolean[] instancia) {
        this.instancia = instancia;
    }

    public boolean[] getInstancia() {
        return instancia;
    }

    public void setInstancia(final boolean[] instancia) {
        this.instancia = instancia;
    }

    public int getFit() {
        return fit;
    }

    public void setFit(final int fit) {
        this.fit = fit;
    }
}
