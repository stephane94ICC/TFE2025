package be.loisirs.tfe2025.plateforme_loisirs.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public final class ClientIp {

    private ClientIp() {
        // Classe utilitaire : pas d'instance.
    }

    /** @return l'adresse IP de la requête en cours, ou null hors requête HTTP (tâche planifiée, webhook système). */
    public static String current() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        return attributes.getRequest().getRemoteAddr();
    }
}