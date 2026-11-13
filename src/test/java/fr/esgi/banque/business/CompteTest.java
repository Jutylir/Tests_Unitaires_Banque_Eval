
package fr.esgi.banque.business;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CompteTest {

    // C1 - Le numéro et le titulaire sont obligatoires

    @ParameterizedTest
    @CsvSource(value = {
        "null, Alice, Le numéro de compte est obligatoire",
        "FR001, null, Le titulaire est obligatoire",
        "'', Alice, Le numéro de compte est obligatoire",
        "FR001, '', Le titulaire est obligatoire",
        "'   ', Alice, Le numéro de compte est obligatoire",
        "FR001, '   ', Le titulaire est obligatoire"
    }, nullValues = "null")
    void constructeur_numeroOuTitulaireInvalide_leveIllegalArgumentException(
            String numero, String titulaire, String messageAttendu) {

        // Arrange
        BigDecimal solde = new BigDecimal("100");
        BigDecimal decouvert = new BigDecimal("200");

        // Act
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Compte(numero, titulaire, solde, decouvert)
        );

        // Assert
        assertEquals(messageAttendu, exception.getMessage());
    }


    // C2 - Le solde initial et le découvert autorisé sont obligatoires.
    // Le découvert doit être positif ou nul.

    @ParameterizedTest
    @CsvSource(value = {
        "null, 100",
        "100, null"
    }, nullValues = "null")
    void constructeur_soldeOuDecouvertNull_leveIllegalArgumentException(
            String solde, String decouvert) {

        // Arrange
        BigDecimal soldeInitial = solde == null ? null : new BigDecimal(solde);
        BigDecimal decouvertAutorise = decouvert == null ? null : new BigDecimal(decouvert);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            new Compte("123", "Theo", soldeInitial, decouvertAutorise)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "-100",
        "-0.01"
    })
    void constructeur_decouvertNegatif_leveIllegalArgumentException(String decouvert) {

        // Arrange
        BigDecimal solde = new BigDecimal("100");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            new Compte("123", "Theo", solde, new BigDecimal(decouvert))
        );
    }

    @ParameterizedTest
    @CsvSource({
        "0",
        "0.00",
        "0.01",
        "100"
    })
    void constructeur_decouvertPositifOuNul_constructionReussie(String decouvert) {

        // Arrange
        BigDecimal solde = new BigDecimal("100");

        // Act
        Compte compte = new Compte(
            "123", "Theo", solde, new BigDecimal(decouvert)
        );

        // Assert
        assertNotNull(compte);
    }


    // C3 - Crediter n'accepte qu'un montant strictement positif.

    @ParameterizedTest
    @CsvSource(value = {
        "null",
        "0",
        "0.00",
        "-1",
        "-10"
    }, nullValues = "null")
    void crediter_montantNulNegatifOuNull_leveIllegalArgumentException(String montant) {

        // Arrange
        Compte compte = new Compte("123", "Theo",
            new BigDecimal("100"), new BigDecimal("50"));
        BigDecimal montantAcrediter = montant == null ? null : new BigDecimal(montant);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            compte.crediter(montantAcrediter)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "1, 101",
        "25, 125",
        "100, 200"
    })
    void crediter_montantPositif_metAJourLeSolde(String montant, String soldeAttendu) {

        // Arrange
        Compte compte = new Compte("123", "Theo",
            new BigDecimal("100"), new BigDecimal("50"));

        // Act
        compte.crediter(new BigDecimal(montant));

        // Assert
        assertEquals(0, new BigDecimal(soldeAttendu).compareTo(compte.getSolde()));
    }


    // C4 - Debiter n'accepte qu'un montant strictement positif.

    @ParameterizedTest
    @CsvSource(value = {
        "null",
        "0",
        "0.00",
        "-1",
        "-10"
    }, nullValues = "null")
    void debiter_montantNulNegatifOuNull_leveIllegalArgumentException(String montant) {

        // Arrange
        Compte compte = new Compte("123", "Theo",
            new BigDecimal("100"), new BigDecimal("50"));
        BigDecimal montantADebiter = montant == null ? null : new BigDecimal(montant);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            compte.debiter(montantADebiter)
        );
    }


    // C5 - Un débit ne peut pas dépasser le découvert autorisé.
    // Atteindre exactement la limite est autorisé.

    @ParameterizedTest
    @CsvSource({
        "1, 99",
        "120, -20",
        "150, -50"
    })
    void debiter_soldeSuperieurOuEgalALaLimite_debitAccepte(
            String montant, String soldeAttendu) {

        // Arrange
        Compte compte = new Compte("123", "Theo",
            new BigDecimal("100"), new BigDecimal("50"));

        // Act
        compte.debiter(new BigDecimal(montant));

        // Assert
        assertEquals(0, new BigDecimal(soldeAttendu).compareTo(compte.getSolde()));
    }

    @ParameterizedTest
    @CsvSource({
        "150.01",
        "151",
        "200"
    })
    void debiter_soldeSousLaLimite_leveSoldeInsuffisantExceptionEtNeModifiePasLeSolde(
            String montant) {

        // Arrange
        Compte compte = new Compte("123", "Theo",
            new BigDecimal("100"), new BigDecimal("50"));
        BigDecimal soldeInitial = compte.getSolde();

        // Act
        assertThrows(SoldeInsuffisantException.class, () ->
            compte.debiter(new BigDecimal(montant))
        );

        // Assert
        assertEquals(0, soldeInitial.compareTo(compte.getSolde()));
    }


    // C6 - EstVide retourne true lorsque le solde vaut zéro,
    // quelle que soit son échelle.

    @ParameterizedTest
    @CsvSource({
        "0",
        "0.0",
        "0.00",
        "0.000",
        "0.000000"
    })
    void estVide_soldeZeroRetourneTrueQuelleQueSoitSonEchelle(String solde) {

        // Arrange
        Compte compte = new Compte("123", "Theo",
            new BigDecimal(solde), new BigDecimal("50"));

        // Act
        boolean resultat = compte.estVide();

        // Assert
        assertTrue(resultat);
    }

    @ParameterizedTest
    @CsvSource({
        "0.01",
        "100",
        "-0.01",
        "-100"
    })
    void estVide_soldeNonNulRetourneFalse(String solde) {

        // Arrange
        Compte compte = new Compte("123", "Theo",
            new BigDecimal(solde), new BigDecimal("50"));

        // Act
        boolean resultat = compte.estVide();

        // Assert
        assertFalse(resultat);
    }


    // C7 - EstADecouvert retourne true lorsque le solde est strictement négatif.

    @ParameterizedTest
    @CsvSource({
        "-0.01",
        "-1",
        "-10",
        "-100.50"
    })
    void estADecouvert_soldeNegatifRetourneTrue(String solde) {

        // Arrange
        Compte compte = new Compte("123", "Theo",
            new BigDecimal(solde), new BigDecimal("50"));

        // Act
        boolean resultat = compte.estADecouvert();

        // Assert
        assertTrue(resultat);
    }

    @ParameterizedTest
    @CsvSource({
        "0",
        "0.00",
        "0.01",
        "100"
    })
    void estADecouvert_soldePositifOuNulRetourneFalse(String solde) {

        // Arrange
        Compte compte = new Compte("123", "Theo",
            new BigDecimal(solde), new BigDecimal("50"));

        // Act
        boolean resultat = compte.estADecouvert();

        // Assert
        assertFalse(resultat);
    }
}
