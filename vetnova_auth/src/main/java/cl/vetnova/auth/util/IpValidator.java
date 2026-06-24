package cl.vetnova.auth.util;

/**
 * Valida direcciones IP en formato IPv4 o IPv6 (forma completa de 8 grupos).
 */
public final class IpValidator {

    private static final String IPV4 =
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
    private static final String IPV6 = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

    private IpValidator() {
    }

    public static boolean esValida(String ip) {
        if (ip == null) {
            return false;
        }
        return ip.matches(IPV4) || ip.matches(IPV6);
    }
}
