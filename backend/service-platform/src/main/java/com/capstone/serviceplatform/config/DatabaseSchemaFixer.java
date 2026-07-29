package com.capstone.serviceplatform.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Hibernate's ddl-auto=update only ADDS tables/columns, it never relaxes an
 * existing NOT NULL constraint. Certains anciens schémas ont été créés avant
 * que des colonnes deviennent optionnelles (ex: avis.reservation_id, qui
 * autorise désormais les avis laissés directement depuis un profil, sans
 * réservation associée). Ce runner corrige ces contraintes obsolètes au
 * démarrage, de façon idempotente et sans casser le démarrage si la
 * modification a déjà été appliquée ou si la table n'existe pas encore.
 */
@Component
public class DatabaseSchemaFixer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaFixer.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        relaxNotNull("avis", "reservation_id", "BIGINT NULL");
        relaxNotNull("avis", "prestataire_id", "BIGINT NULL");
        relaxNotNull("avis", "client_id", "BIGINT NULL");
        backfillDatePaiement();
    }

    /**
     * Les réservations payées AVANT l'ajout de la colonne date_paiement n'ont
     * aucune date d'encaissement : le graphe "Revenus (7 jours)" ne les
     * verrait jamais. On les rattrape une fois : date du rendez-vous si elle
     * est passée, sinon maintenant. Idempotent (WHERE date_paiement IS NULL).
     */
    private void backfillDatePaiement() {
        try {
            int updated = jdbcTemplate.update(
                "UPDATE reservation SET date_paiement = " +
                "CASE WHEN date_heure IS NOT NULL AND date_heure < NOW() THEN date_heure ELSE NOW() END " +
                "WHERE date_paiement IS NULL AND statut IN ('PAYEE', 'EN_COURS', 'TERMINEE')"
            );
            if (updated > 0) {
                log.info("Backfill: date_paiement renseignée pour {} réservation(s) payée(s).", updated);
            }
        } catch (Exception e) {
            log.debug("Backfill date_paiement ignoré : {}", e.getMessage());
        }
    }

    private void relaxNotNull(String table, String column, String newDefinition) {
        try {
            jdbcTemplate.execute("ALTER TABLE " + table + " MODIFY COLUMN " + column + " " + newDefinition);
            log.info("Schema check: {}.{} is nullable.", table, column);
        } catch (Exception e) {
            log.debug("Schema check skipped for {}.{}: {}", table, column, e.getMessage());
        }
    }
}
