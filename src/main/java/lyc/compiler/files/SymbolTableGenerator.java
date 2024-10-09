package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import lyc.compiler.tabla_simbolos.Simbolo;

public class SymbolTableGenerator implements FileGenerator{

    private final ArrayList<Simbolo> symbolTable;

    public SymbolTableGenerator(ArrayList<Simbolo> tablaSimbolos) {
        this.symbolTable = tablaSimbolos;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for (Simbolo symbol : symbolTable) {
            fileWriter.write(symbol.getNombre() + " | " + symbol.getTipoDato() + " | " + symbol.getValor() + " | " + symbol.getLongitud() + "\n");
        }
    }
}
