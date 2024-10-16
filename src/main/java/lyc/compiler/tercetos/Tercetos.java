package lyc.compiler.tercetos;


public class Tercetos {
    private int indice;
    private String operador;
    private String operando1;
    private String operando2;

    // Constructor
    public Tercetos(int indice, String operador, String operando1, String operando2) {
        this.indice = indice;
        this.operador = operador;
        this.operando1 = operando1;
        this.operando2 = operando2;
    }

    // Getters y setters
    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getOperando1() {
        return operando1;
    }

    public void setOperando1(String operando1) {
        this.operando1 = operando1;
    }

    public String getOperando2() {
        return operando2;
    }

    public void setOperando2(String operando2) {
        this.operando2 = operando2;
    }

    @Override
    public String toString() {

        String operando1Str=this.operando1;
        String operando2Str=this.operando2;

        if (operando1.startsWith("terceto_")) {
            operando1Str = operando1.substring(8);
            operando1Str = "[" + operando1Str + "]";
        }

        if (operando2.startsWith("terceto_")) {
            operando2Str = operando2.substring(8);
            operando2Str = "[" + operando2Str + "]";
        }
        
        return "[" + indice + "] (" + this.operador + " , " + operando1Str + " , " + operando2Str + ")";
    }
}
