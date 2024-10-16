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

    public void actualizar_branch_terceto(int terceto_a_reemplazar, String valor) {
        // Busco el terceto a actualizar
        for (Tercetos terceto : tercetos) {
            if (terceto.getIndice() == terceto_a_reemplazar) {
                terceto.setOperando1(valor);
                break; 
            }
        }
    }


    // esta función asigna el valor del indice de un no terminal al indice de otro no terminal.
    // puede ser util en reglas como termino := factor ya que Tind = Find 
    public void apuntarAOtroIndice(String noTerminal1, String noTerminal2) {

        Integer indice = indiceTerminales.get(noTerminal2);
        
        if (indice != null) {
            // Asignar el índice de noTerminal2 a noTerminal1
            indiceTerminales.put(noTerminal1, indice);
        }
    }

    public void quitarUltimoGoTo() {
        for (int i = tercetos.size() - 1; i >= 0; i--) {
            Tercetos terceto = tercetos.get(i);
            if (terceto.getOperador().equals("go_to")) {
                tercetos.remove(i);
                for (int j = i; j < tercetos.size(); j++) {
                    Tercetos t = tercetos.get(j);
                    t.setIndice(j + 1);
                }
                break;
            }
        }
    }

    public void modificarUltimoGoTo(String nuevo_elemento1) {
        for (int i = tercetos.size() - 1; i >= 0; i--) {
            Tercetos terceto = tercetos.get(i);
            if (terceto.getOperador().equals("go_to")) {
                System.out.println("El ultimo terceto es: \n" + tercetos.get(i).toString());
                terceto.setOperando1(nuevo_elemento1);
                break;
            }
        }
    }

    public void agregarTipoDeVariableInit(String cadena) {
        for (Tercetos terceto : tercetos) {
            if (terceto.getOperador().equals("INIT") && terceto.getOperando2().equals("_")) {
                terceto.setOperando2(cadena);
            }
        }
    }

    public void reemplazarOperadorUltimoIfTrue(String nuevoOperador) {
        int lastIndex = -1;
        for (int i = tercetos.size() - 1; i >= 0; i--) {
            Tercetos terceto = tercetos.get(i);
            if (terceto.getOperador().equals("if_true")) {
                lastIndex = i;
                break;
            }
        }
        if (lastIndex != -1) {
            tercetos.get(lastIndex).setOperador(nuevoOperador);
        }
    }

    public void saltarAlTercetoDesapilado(int numeroDeTerceto, int valor) {
        
        for (Tercetos terceto : tercetos) {
            if (terceto.getIndice() == numeroDeTerceto) {
                terceto.setOperando1("[" + Integer.toString(valor) + "]");
                return; 
            }
        }
    }

    public void crearNuevoTerceto(int index, String operador, String elemento1, String elemento2) {
        Tercetos nuevoTerceto = new Tercetos(index, operador, elemento1, elemento2);
        
        if (index >= tercetos.size()) {
            tercetos.add(nuevoTerceto);
        } else {
            tercetos.add(index, nuevoTerceto);
        }
        
        reorganizarIndices();
    }

    private void reorganizarIndices() {
        for (int i = 0; i < tercetos.size(); i++) {
            tercetos.get(i).setIndice(i + 1); 
        }
    }

    public String getOperadorPorIndice(int indiceTerceto) {
        for (Tercetos terceto : tercetos) {
            if (terceto.getIndice() == indiceTerceto) {
                return terceto.getOperador();
            }
        }
        return null; 
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
