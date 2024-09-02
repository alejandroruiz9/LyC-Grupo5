package lyc.compiler.constants;

public final class Constants {

    public static final int MAX_LENGTH = 30;

    //Int (16 bits)
    public static final int MIN_INT = -32768;
    public static final int MAX_INT = 32767;

    //Float (32 bits): el separador decimal será el punto “.”
    public static final float MIN_FLOAT = -Float.MAX_VALUE;
    public static final float MAX_FLOAT = Float.MAX_VALUE;

    //String: constantes de 40 caracteres alfanuméricos como máximo, limitada por comillas (“ “) ,de la forma “XXXX”
    public static final int MAX_LENGTH_STRING = 40;

    private Constants(){}

}
