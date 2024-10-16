package lyc.compiler.files;
import lyc.compiler.tercetos.*;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class IntermediateCodeGenerator implements FileGenerator {

    private final ArrayList<Tercetos> intermediateCode;

    public IntermediateCodeGenerator(ArrayList<Tercetos> intermediateCode) {
        this.intermediateCode = intermediateCode;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for (Tercetos terceto : intermediateCode) {
            fileWriter.write(terceto.toString() + "\n");
        }
    }
}
