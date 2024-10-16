package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import lyc.compiler.tabla_simbolos.Simbolo;

public class SymbolTableGenerator implements FileGenerator{

    private final ArrayList<Simbolo> symbolTable;

    public SymbolTableGenerator(ArrayList<Simbolo> tablaSimbolos) {
        this.symbolTable = tablaSimbolos;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("NOMBRE | TIPODATO | VALOR | LONGITUD \n");
        for (Simbolo symbol : symbolTable) {
            fileWriter.write(symbol.getNombre() + " | " + symbol.getTipoDato() + " | " + symbol.getValor() + " | " + Objects.toString(symbol.getLongitud(), "") + "\n");
        }
    }
}
