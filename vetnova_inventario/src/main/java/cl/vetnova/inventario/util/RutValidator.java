package cl.vetnova.inventario.util;

/**
 * Valida RUT chileno con dígito verificador (módulo 11).
 */
public final class RutValidator {

    private RutValidator() {
    }

    public static boolean esValido(String rut) {
        if (rut == null) {
            return false;
        }
        String limpio = rut.replace(".", "").replace("-", "").toUpperCase();
        if (limpio.length() < 2) {
            return false;
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);
        if (!cuerpo.matches("\\d+")) {
            return false;
        }
        int suma = 0;
        int multiplo = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplo;
            multiplo = multiplo == 7 ? 2 : multiplo + 1;
        }
        int resto = 11 - (suma % 11);
        char dvEsperado;
        if (resto == 11) {
            dvEsperado = '0';
        } else if (resto == 10) {
            dvEsperado = 'K';
        } else {
            dvEsperado = Character.forDigit(resto, 10);
        }
        return dv == dvEsperado;
    }
}
