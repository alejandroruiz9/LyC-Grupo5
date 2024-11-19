package lyc.compiler.Assembler;
import lyc.compiler.tercetos.*;
import lyc.compiler.tabla_simbolos.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import java_cup.runtime.Symbol;

import java.util.HashMap;


public class ManejadorAssembler {
    private ArrayList<String> asmInstructions;
    private ArrayList<Simbolo> tabla_simbolos;
    private ArrayList<Tercetos> tercetos;
    private HashMap<Integer, String> tercetoLabels;



    public ManejadorAssembler(ArrayList<Simbolo> tabla_simbolos, ArrayList<Tercetos> tercetos) {
        this.tabla_simbolos = tabla_simbolos;
        this.tercetos = tercetos;
        this.asmInstructions = new ArrayList<>();
        this.tercetoLabels = new HashMap<>();
    }


    private void assignLabelsToTercetos() {
        for (int i = 0; i < tercetos.size(); i++) {
            tercetoLabels.put(i + 1, "LABEL_" + (i + 1));
        }
    }

    private void filterBranchLabels() {
        HashMap<Integer, String> branchLabels = new HashMap<>();
        for (int i = 0; i < tercetos.size(); i++) {
            Tercetos terceto = tercetos.get(i);
            String operador = terceto.getOperador();
            if (operador.equals("BLE") || operador.equals("BGE")|| operador.equals("BLT") || operador.equals("BGT") || operador.equals("BNE")|| operador.equals("BEQ")|| operador.equals("BI")) {
                try {
                    int destino = Integer.parseInt(terceto.getOperando1().replace("[", "").replace("]", "").replace("terceto", "").replace("_", ""));
                    branchLabels.put(destino, tercetoLabels.get(destino));
                } catch(Exception e) {
                    continue;
                }
                
            }
        }
        tercetoLabels = branchLabels;
    }

    public void generateAssembler() {
        assignLabelsToTercetos();
        filterBranchLabels();
        insertHeaders();
        insertSymbolTable();
        generateTercetosCode();
    }
    private void insertHeaders() {
        asmInstructions.add("include macros2.asm");
        asmInstructions.add("include number.asm\n");
        asmInstructions.add(".MODEL  LARGE");
        asmInstructions.add(".386");
        asmInstructions.add(".STACK 200h");
    }
    private void insertSymbolTable() {
        asmInstructions.add("");
        asmInstructions.add("");
        asmInstructions.add(".DATA");
        for (Simbolo symbol : tabla_simbolos) {
            try {
                
                if(symbol.getTipoDato().equals("Float") && symbol.getNombre().contains(".")) {
                    String valor = symbol.getValor();
                    
                    if (valor.startsWith(".")) {
                        valor = "0" + valor;  
                    } else if (valor.endsWith(".")) {
                        valor = valor + "00";  
                    }
                    String nuevoNombre = valor.replace('.','x');
                    symbol.setNombre("_" + nuevoNombre);
                    symbol.setValor(valor);
                }
                if(symbol.getValor().isEmpty()) { // es una variable
                    asmInstructions.add("\t" + symbol.getNombre() + "\t\tdd ?" );
                } else { // es una cte
                    if(symbol.getTipoDato().equals("String")) { // el nombre del string no puede tener " "
                        String nombre_string = symbol.getNombre().replace("\"", "");
                        nombre_string = nombre_string.replace(' ', '_');
                        asmInstructions.add("\t" + nombre_string + "\t\tdb\t\t" + symbol.getValor() +", '$'");
                    }
                    else if(symbol.getTipoDato().equals("Int")){
                        asmInstructions.add("\t" + symbol.getNombre() + "\t\tdd\t\t" + symbol.getValor()+ ".0");
                    }
                    else {
                        asmInstructions.add("\t" + symbol.getNombre() + "\t\tdd\t\t" + symbol.getValor());
                    }
                }
                if(symbol.getValor().contains("@")){
                    asmInstructions.add("\t" + symbol.getNombre() + "\t\tdd\t\t" + symbol.getValor());
                }
            } catch(Exception e){
                continue;
            }
        }
        asmInstructions.add("truncMode DW 0F7FFH ");
        asmInstructions.add("saltoLinea                  db 0Dh, 0Ah, '$'");
        
        asmInstructions.add("");
    }
    private void generateTercetosCode() {
        Stack<String> operandos  = new Stack<String>();
        asmInstructions.add(".CODE");
        if(Manejador_Tabla_Simbolos.getSymbolTypeStatic("@remainder", tabla_simbolos)!="null"){
            asmInstructions.add("MODULO PROC");             
            asmInstructions.add("\tFSTP @divisor");             
            asmInstructions.add("\tFSTP @remainder");             
            asmInstructions.add("\tMODULO_LOOP:");      
            asmInstructions.add("\t\tFLD @remainder");             
            asmInstructions.add("\t\tFLD @divisor");               
            asmInstructions.add("\t\tFXCH");                      
            asmInstructions.add("\t\tFCOMP");                     
            asmInstructions.add("\t\tFSTSW AX");                  
            asmInstructions.add("\t\tSAHF");                      
            asmInstructions.add("\t\tJB MODULO_DONE");            
            asmInstructions.add("\t\tFLD @remainder");             
            asmInstructions.add("\t\tFLD @divisor");               
            asmInstructions.add("\t\tFSUB");                      
            asmInstructions.add("\t\tFSTP @remainder");            
            asmInstructions.add("\t\tJMP MODULO_LOOP");           
    
            asmInstructions.add("MODULO_DONE:");                
            asmInstructions.add("\tFLD @remainder");
            asmInstructions.add("\tRET");                       
            asmInstructions.add("MODULO ENDP");
        }

 

        asmInstructions.add("START:");
        asmInstructions.add("\tmov AX, @DATA");
        asmInstructions.add("\tmov DS, AX");
        asmInstructions.add("fldcw truncMode");
        asmInstructions.add("");
        int tercetoIndex = 1;
        for (Tercetos terceto : tercetos) {
            try {
                String destino = tercetoLabels.get(terceto.getIndice());
                if (destino == null) {
                    destino = "FINAL_LABEL";
                }
                String operador = terceto.getOperador();
                String operando1 = terceto.getOperando1();
                
                if(operador.contains(".")) {
                    String valor = operador;
                    // Formatear el valor
                    if (valor.startsWith(".")) {
                        valor = "0" + valor;  // Cambia ".9999" a "0.9999"
                    } else if (valor.endsWith(".")) {
                        valor = valor + "00";  // Cambia "99." a "99.00"
                    }
                    valor = valor.replace(".", "x");
                    valor = "_" + valor;
                    operador = valor;
                }
                if(operador.contains("\"")) { // el nombre del string no puede tener " "
                        String nombre_string = operador.replace("\"", "");
                        nombre_string = nombre_string.replace(' ', '_');
                        operador = "_" + nombre_string;
                }
                String operando2 = terceto.getOperando2();

                // Si el terceto destino tiene una etiqueta, la agregamos en el asm
                if (tercetoLabels.containsKey(tercetoIndex)) {
                    asmInstructions.add(tercetoLabels.get(tercetoIndex) + ":");
                }
                switch (operador) {
                    case ":=":
                        
                        if(!operando1.contains("[")){
                            if(operando1.contains("@"))
                                asmInstructions.add("\tFLD " + operando1);
                            else
                            asmInstructions.add("\tFLD _" + operando1);
                        }
                        else{ 
                            int indiceTarget= Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                            if(terceto.getIndice()- indiceTarget > 1){
                                if (tercetos.get(indiceTarget -1 ).getOperador().matches("[0-9]+(\\.[0-9]+)?")) {
                                    asmInstructions.add("\tFLD _" + tercetos.get(indiceTarget -1 ).getOperador().replace(".", "x"));
                                } else if(!terceto.getOperador().contains("\"") && !terceto.getOperador().contains(".")){
                                    asmInstructions.add("\tFLD " + tercetos.get(indiceTarget -1 ).getOperador());
                                } 

                            }
                            
                        }
                        
                        asmInstructions.add("\tFSTP " + operando2);
                        asmInstructions.add("");
                        break;
                    case "CMP":

                    if(!operando1.contains("[")){
                        if(operando1.contains("@"))
                            asmInstructions.add("\tFLD " + operando1);
                        else
                        asmInstructions.add("\tFLD _" + operando1);
                    }
                    if(!operando2.contains("[")){
                        if(operando2.contains("@"))
                            asmInstructions.add("\tFLD " + operando2);
                        else
                        asmInstructions.add("\tFLD _" + operando2);
                    }

                        asmInstructions.add("");

                        asmInstructions.add("\tFXCH");
                        asmInstructions.add("\tFCOMP");
                        asmInstructions.add("\tFSTSW ax");
                        asmInstructions.add("\tFFREE");
                        asmInstructions.add("\tSAHF");
                        break;
                    case "BLE":
                        int destinoBLE = Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                        if (tercetoLabels.get(destinoBLE) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJBE FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }
                        asmInstructions.add("\tJBE " + tercetoLabels.get(destinoBLE));
                        asmInstructions.add("");
                        break;
                    case "BI":
                        int destinoBI = Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                        if (tercetoLabels.get(destinoBI) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJMP FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }
                        asmInstructions.add("\tJMP " + tercetoLabels.get(destinoBI));
                        asmInstructions.add("");
                        break;
                    case "BGE":
                        int destinoBGE = Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                        if (tercetoLabels.get(destinoBGE) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJAE FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }
                        asmInstructions.add("\tJAE " + tercetoLabels.get(destinoBGE));
                        asmInstructions.add("");
                        break;
                    case "BLT":
                        int destinoBLT = Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                    
                        if (tercetoLabels.get(destinoBLT) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJB FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }
                        asmInstructions.add("\tJB " + tercetoLabels.get(destinoBLT));
                        asmInstructions.add("");
                        break;
                    case "BEQ":
                        int destinoBEQ = Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                        if (tercetoLabels.get(destinoBEQ) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJE FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }
                        asmInstructions.add("\tJE " + tercetoLabels.get(destinoBEQ));
                        asmInstructions.add("");
                        break;
                    case "BNE":
                        int destinoBNE = Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                        
                        if (tercetoLabels.get(destinoBNE) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJNE FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }
                        asmInstructions.add("\tJNE " + tercetoLabels.get(destinoBNE));
                        asmInstructions.add("");
                        break;
                    case "BGT":
                        int destinoBGT = Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                        
                        if (tercetoLabels.get(destinoBGT) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJA FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }
                        asmInstructions.add("\tJA " + tercetoLabels.get(destinoBGT));
                        asmInstructions.add("");
                        break;
                    case "BE":
                        int destinoBE = Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                        
                        if (tercetoLabels.get(destinoBE) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJE FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }
                        asmInstructions.add("\tJE " + tercetoLabels.get(destinoBE));
                        asmInstructions.add("");
                        break;
                    case "*":
                        asmInstructions.add("\tFMUL ");
                        break;
                    case "+":
                        asmInstructions.add("\tFADD ");

                        break;
                    case "-":
                        asmInstructions.add("\tFSUB ");

                        break;
                    case "/":
                    if(!operando1.contains("[")){
                        if(operando1.contains("@"))
                            asmInstructions.add("\tFLD " + operando1);
                        else
                        asmInstructions.add("\tFLD _" + operando1);
                    }
                    if(!operando2.contains("[")){
                        if(operando2.contains("@"))
                            asmInstructions.add("\tFLD " + operando2);
                        else
                        asmInstructions.add("\tFLD _" + operando2);
                    }
                        asmInstructions.add("\tFDIV ");
                        
                        asmInstructions.add("\tfrndint ");
                        break;
                    case "%":
                        if(!operando1.contains("[")){
                            if(operando1.contains("@"))
                                asmInstructions.add("\tFLD " + operando1);
                            else
                            asmInstructions.add("\tFLD _" + operando1);
                        }
                        if(!operando2.contains("[")){
                            if(operando2.contains("@"))
                                asmInstructions.add("\tFLD " + operando2);
                            else
                            asmInstructions.add("\tFLD _" + operando2);
                        }
                        asmInstructions.add("\tCALL MODULO");


                    break;
                    case "READ":
                        if(Manejador_Tabla_Simbolos.getSymbolTypeStatic(operando1, tabla_simbolos)== "String"){
                            asmInstructions.add("\tgetString " +operando1);
                            asmInstructions.add("\tmov dx, OFFSET saltoLinea ");
                            asmInstructions.add("\tmov ah, 9 ");
                            asmInstructions.add("\tint 21h ");
                        }
                        else{
                            asmInstructions.add("\tGetFloat " + operando1);
                            asmInstructions.add("\tmov dx, OFFSET saltoLinea ");
                            asmInstructions.add("\tmov ah, 9 ");
                            asmInstructions.add("\tint 21h ");
                            
                        }
                        break;
                    case "WRITE":
                            if(Manejador_Tabla_Simbolos.getSymbolTypeStatic("_"+operando1, tabla_simbolos)== "String")
                                asmInstructions.add("\tDisplayString " + "_"+operando1);
                            else if (Manejador_Tabla_Simbolos.getSymbolTypeStatic(operando1, tabla_simbolos)== "String")
                                asmInstructions.add("\tDisplayString "+operando1);
                            else if(Manejador_Tabla_Simbolos.getSymbolTypeStatic("_"+operando1, tabla_simbolos)== "Float" ||
                            Manejador_Tabla_Simbolos.getSymbolTypeStatic("_"+operando1, tabla_simbolos)== "Int" )
                                asmInstructions.add("\tDisplayFloat _" + operando1+ ",2");
                            else
                                asmInstructions.add("\tDisplayFloat " + operando1+ ",2");

                        break;
                    case "WRITELN":
                        if(Manejador_Tabla_Simbolos.getSymbolTypeStatic("_"+operando1, tabla_simbolos)== "String")
                            asmInstructions.add("\tDisplayString " + "_"+operando1);
                        else if (Manejador_Tabla_Simbolos.getSymbolTypeStatic(operando1, tabla_simbolos)== "String")
                            asmInstructions.add("\tDisplayString "+operando1);
                        else if(Manejador_Tabla_Simbolos.getSymbolTypeStatic("_"+operando1, tabla_simbolos)== "Float" ||
                        Manejador_Tabla_Simbolos.getSymbolTypeStatic("_"+operando1, tabla_simbolos)== "Int" )
                            asmInstructions.add("\tDisplayFloat _" + operando1+ ",2");
                        else
                            asmInstructions.add("\tDisplayFloat " + operando1+ ",2");
                        asmInstructions.add("\tmov dx, OFFSET saltoLinea ");
                        asmInstructions.add("\tmov ah, 9 ");
                        asmInstructions.add("\tint 21h ");
                    break;
                    // Otros operadores
                    default:
                        if (terceto.getOperador().matches("[0-9]+(\\.[0-9]+)?")) {
                            asmInstructions.add("\tFLD _" + terceto.getOperador().replace(".", "x"));
                        } else if(!terceto.getOperador().contains("\"") && !terceto.getOperador().contains(".")){
                            asmInstructions.add("\tFLD " + terceto.getOperador());
                        }
                        operandos.add(operador);
                    }
                    tercetoIndex++;
                } catch(Exception e) {
                    continue;
                }
            
        }

        asmInstructions.add("\tFINAL_LABEL:");
        asmInstructions.add("\tMOV AX, 4C00h");
        asmInstructions.add("\tINT 21h");
        asmInstructions.add("END START");

    }

    public String getSymbolType(String symbolName) {
        for (Simbolo symbol : tabla_simbolos) {
            if (symbol.getNombre().equals(symbolName)) {
                return "Variable";
            }
        }
        for (Simbolo symbol : tabla_simbolos) {
            if (symbol.getNombre().equals("_" + symbolName)) {
                return "Constante";
            }
        }
 
        return "No encontrado";
    }
    public String tipoDeSimboloPorNombre(String nombre) {
        for (Simbolo symbol : tabla_simbolos) {
            if (symbol.getNombre().equals(nombre)) {
                return symbol.getTipoDato();
            } else if (symbol.getNombre().equals("_" + nombre)) {
                return symbol.getTipoDato();
            }
        }
        return null; // Si no se encuentra el símbolo
    }
    // Busca el tipo de un símbolo reconstruyendo su nombre original
    public boolean buscarTipoString(String ladoDerecho) {
        String nombreOriginal = "\"" + ladoDerecho.substring(1).replace('_', ' ') + "\"";
        for (Simbolo symbol : tabla_simbolos) {
            if (symbol.getNombre().equals(nombreOriginal) && symbol.getTipoDato().equals("String")) {
                return true;
            }
        }
        return false;
    }
    public ArrayList<String> getAsmInstructions() {
        return asmInstructions;
    }
}