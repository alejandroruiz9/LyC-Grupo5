package lyc.compiler.tercetos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManejadorTercetos {
    private ArrayList<Tercetos> tercetos;
    private Map<String, Integer> indiceTerminales = new HashMap<>();


    // Constructor
    public ManejadorTercetos() {
        this.tercetos = new ArrayList<>();
    }


    public void agregarTerceto(Tercetos terceto) {
        this.tercetos.add(terceto);
    }


    public Tercetos obtenerTerceto(int indice) {
        for (Tercetos t : tercetos) {
            if (t.getIndice() == indice) {
                return t;
            }
        }
        return null; 
    }

    public void imprimirTercetos() {
        for (Tercetos t : tercetos) {
            System.out.println(t.toString());
        }
    }

    public void agregarIndice(String noTerminal, int numeroTerceto) {
        indiceTerminales.put(noTerminal, numeroTerceto);
    }

    public int obtenerIndice(String noTerminal) {
        return indiceTerminales.getOrDefault(noTerminal, -1); // Devuelve -1 si no se encuentra el no terminal
    }

    public ArrayList<Tercetos> getIntermediateCode() {
        return tercetos;
    }

    public void mostrarIndicesTercetos() {
        System.out.println("Índices de tercetos:");
        for (Map.Entry<String, Integer> entry : indiceTerminales.entrySet()) {
            String noTerminal = entry.getKey();
            int numeroTerceto = entry.getValue();
            System.out.println(noTerminal + " => Terceto " + numeroTerceto);
        }
    }

    public void apuntarAOtroIndice(String noTerminal1, String noTerminal2) {

        Integer indice = indiceTerminales.get(noTerminal2);
        
        if (indice != null) {
            // Asignar el índice de noTerminal2 a noTerminal1
            indiceTerminales.put(noTerminal1, indice);
        }
    }


    public void crearNuevoTerceto(int index, String operador, String elemento1, String elemento2) {
        Tercetos nuevoTerceto = new Tercetos(index, operador, elemento1, elemento2);
        
        if (index >= tercetos.size()) {
            tercetos.add(nuevoTerceto);
        } else {
            tercetos.add(index, nuevoTerceto);
        }
        
    }

    public void modificarTerceto(int indiceTerceto, int campo, String nuevoValor) {
        Tercetos terceto = obtenerTerceto(indiceTerceto);  // Obtener el terceto desde su índice
        if (terceto == null) {
            throw new RuntimeException("Terceto no encontrado en el índice: " + indiceTerceto);
        }
    
        switch (campo) {
            case 2:
                terceto.setOperando1(nuevoValor);
                break;
            case 3:
                terceto.setOperando2(nuevoValor);
                break;
            default:
                throw new IllegalArgumentException("Campo inválido. Sólo se pueden modificar el campo 2 o 3 del terceto.");
        }
    }
}
