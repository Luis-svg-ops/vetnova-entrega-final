package cl.vetnova.auth.util;

/**
 * Valida RUT chileno: formato y dígito verificador (módulo 11).
 */
public final class RutValidator {

    private RutValidator() {
    }

    public static boolean esValido(String rut) {
        if (rut == null) {
            return false;
        }
        String limpio = rut.replace(".", "").replace("-", "").trim().toUpperCase();
        if (!limpio.matches("\\d{7,8}[0-9K]")) {
            return false;
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dvIngresado = limpio.charAt(limpio.length() - 1);
        int suma = 0;
        int multiplo = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplo;
            multiplo = (multiplo == 7) ? 2 : multiplo + 1;
        }
        int resto = 11 - (suma % 11);
        char dvCalculado;
        if (resto == 11) {
            dvCalculado = '0';
        } else if (resto == 10) {
            dvCalculado = 'K';
        } else {
            dvCalculado = (char) ('0' + resto);
        }
        return dvIngresado == dvCalculado;
    }
}
