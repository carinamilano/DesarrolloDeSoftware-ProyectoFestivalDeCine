package servicios;

/* CORRECCION I11: genera los códigos correlativos y, al cargar datos guardados,
sigue la numeración después del último código usado para no repetir.*/
class Contador {
    private int ultimo;

    String siguiente(String prefijo) {
        ultimo++;
        return prefijo + ultimo;
    }

    void registrarUsado(String codigo) {
        int guion = codigo.lastIndexOf('-');
        try {
            int numero = Integer.parseInt(codigo.substring(guion + 1));
            if (numero > ultimo) ultimo = numero;
        } catch (NumberFormatException e) {
        }
    }
}
