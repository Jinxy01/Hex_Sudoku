
package sudokuhex;


public class Estatistica {
    
    private String tempo;
    private String nome;
    private String dificuldade;

    public Estatistica(String tempo, String nome, String dificuldade) {
        this.tempo = tempo;
        this.nome = nome;
        this.dificuldade = dificuldade;
    }

    public String getTempo() {
        return tempo;
    }

    public String getNome() {
        return nome;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }

    @Override
    public String toString() {
        return "Estatistica{" + "tempo=" + tempo + ", nome=" + nome + ", dificuldade=" + dificuldade + '}';
    }
    
    
    
    
}
