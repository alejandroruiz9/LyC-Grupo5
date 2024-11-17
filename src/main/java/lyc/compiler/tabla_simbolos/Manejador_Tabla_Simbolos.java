package lyc.compiler.tabla_simbolos;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import lyc.compiler.tercetos.*;

public class Manejador_Tabla_Simbolos {
 
    private ArrayList<Simbolo> tablaSimbolos = new ArrayList<>();

    public void addSymbol(Simbolo symbol) {
        tablaSimbolos.add(symbol);
    }

    public void addVariablesFromArrayList(ArrayList<Simbolo> simbolos, String tipoDato) {


        for (Simbolo elemento : simbolos) {
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

    public ArrayList<Simbolo> getTablaSimbolos() {
        return tablaSimbolos;
    }

    public boolean containsSymbol(String nombreSimbolo) {
        
        for (Simbolo symbol : tablaSimbolos) {
            //System.out.println("Comparando: " + symbol.getNombre() + " con " + nombreSimbolo);
            if (symbol.getNombre().equals(nombreSimbolo)) {
                return true;
            }
        }
        return false;
    }
    public Simbolo getSymbol(String nombreSimbolo) {
        for (Simbolo symbol : tablaSimbolos) {
            if (symbol.getNombre().equals(nombreSimbolo)) {
                return symbol;
            }
        }
        return null;
    }

    public static Simbolo getSymbolStatic(String nombreSimbolo, ArrayList<Simbolo> tablaSimbolos) {
        for (Simbolo symbol : tablaSimbolos) {
            if (symbol.getNombre().equals(nombreSimbolo)) {
                return symbol;
            }
        }
        return null;
    }
    public static String getSymbolTypeStatic(String nombreSimbolo, ArrayList<Simbolo> tablaSimbolos) {
        for (Simbolo symbol : tablaSimbolos) {
            if (symbol.getNombre().equals(nombreSimbolo)) {
                return symbol.getTipoDato();
            }
        }
        return "null";
    }


    
    public void removeSymbol(String nombreSimbolo) {
        tablaSimbolos.removeIf(symbol -> symbol.getNombre().equals(nombreSimbolo));
    }




}
