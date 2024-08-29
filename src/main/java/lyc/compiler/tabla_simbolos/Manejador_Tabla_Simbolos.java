package lyc.compiler.tabla_simbolos;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Manejador_Tabla_Simbolos {
 
    private ArrayList<Simbolo> tablaSimbolos = new ArrayList<>();

    public void addSymbol(Simbolo symbol) {
        tablaSimbolos.add(symbol);
    }

    public void addVariablesFromArrayList(ArrayList<Simbolo> simbolosAAñadir, String tipoDato) {


        for (Simbolo elemento : simbolosAAñadir) {
            try {
                if (!tablaSimbolos.contains(elemento)){
                    elemento.setTipoDato(tipoDato);
                    tablaSimbolos.add(elemento);
                }

            } catch (Exception e) {
                System.out.println("Error al procesar el símbolo: " + e.getMessage());
            }
        }

    }

    public void writeToFile(FileWriter fileWriter) throws IOException {
        for (Simbolo symbol : tablaSimbolos) {
            fileWriter.write(symbol.getNombre() + " | " + symbol.getTipoDato() + " | " + symbol.getValor() + " | " + symbol.getLongitud() + "\n");
        }
    }

     public ArrayList<Simbolo> gettablaSimbolos() {
        return tablaSimbolos;
    }

    public boolean containsSymbol(String nombreSimbolo) {
        for (Simbolo symbol : tablaSimbolos) {
            if (symbol.getNombre().equals(nombreSimbolo)) {
                return true;
            }
        }
        return false;
    }


}