package cl.vetnova.facturacion.util;

public final class RutValidator {

    private RutValidator() {
    }

    public static boolean esValido(String rut) {
        if (rut == null) {
            return false;
        }
        String limpio = rut.replace(".", "").replace("-", "").trim().toUpperCase();
        if (limpio.length() < 2) {
            return false;
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);
        if (!cuerpo.matches("\\d+")) {
            return false;
        }
        int suma = 0;
        int mult = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += (cuerpo.charAt(i) - '0') * mult;
            mult = mult == 7 ? 2 : mult + 1;
        }
        int resto = 11 - (suma % 11);
        char dvEsperado = resto == 11 ? '0' : resto == 10 ? 'K' : (char) ('0' + resto);
        return dv == dvEsperado;
    }
}
