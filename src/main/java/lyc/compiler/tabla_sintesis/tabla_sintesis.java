package lyc.compiler.tabla_sintesis;

import java.util.HashMap;
import java.util.Map;

public class tabla_sintesis {
    private static final Map<String, Map<String, Boolean>> assignmentTable = new HashMap<>();

    static {

        Map<String, Boolean> intRow = new HashMap<>();
        intRow.put("Int", true); 
        intRow.put("Float", true); // Requiere cast implícito de FLOAT a INT
        intRow.put("String", false); // No se permite asignar STRING a INT
        assignmentTable.put("Int", intRow);


        Map<String, Boolean> floatRow = new HashMap<>();
        floatRow.put("Int", true); // Requiere cast implícito de INT a FLOAT
        floatRow.put("Float", true); // Asignación directa
        floatRow.put("String", false); // No se permite asignar STRING a FLOAT
        assignmentTable.put("Float", floatRow);


        Map<String, Boolean> stringRow = new HashMap<>();
        stringRow.put("Int", false); // No se permite asignar INT a STRING
        stringRow.put("Float", false); // No se permite asignar FLOAT a STRING
        stringRow.put("String", true); // Asignación directa
        assignmentTable.put("String", stringRow);
    }

    
    public static boolean canOperate(String fromType, String toType) {
        return assignmentTable.getOrDefault(toType, new HashMap<>()).getOrDefault(fromType, false);
    }
  
}
