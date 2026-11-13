
package fr.esgi.banque.business;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CalculFraisTest {

    // F1 - Le titulaire doit avoir 18 ans ou plus.

    @ParameterizedTest
    @CsvSource({
            "-1",
            "0",
            "17"
    })
    void fraisMensuels_titulaireMineur_leveIllegalArgumentException(int age) {

        // Arrange
        CalculFrais calculFrais = new CalculFrais();
        BigDecimal soldeMoyen = new BigDecimal("500");

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculFrais.fraisMensuels(age, soldeMoyen, false));

        // Assert
        assertEquals("Le titulaire doit être majeur", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "18",
            "19",
            "26",
            "27",
            "65"
    })
    void fraisMensuels_titulaireMajeur_neLevePasException(int age) {

        // Arrange
        CalculFrais calculFrais = new CalculFrais();
        BigDecimal soldeMoyen = new BigDecimal("500");

        // Act
        BigDecimal resultat = assertDoesNotThrow(
                () -> calculFrais.fraisMensuels(age, soldeMoyen, false));

        // Assert
        assertNotNull(resultat);
    }

    // F1 - Le solde moyen est obligatoire.

    @Test
    void fraisMensuels_soldeMoyenNull_leveIllegalArgumentException() {

        // Arrange
        CalculFrais calculFrais = new CalculFrais();
        int age = 30;

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculFrais.fraisMensuels(age, null, false));

        // Assert
        assertEquals("Le solde moyen est obligatoire", exception.getMessage());
    }

    // F2 - Les frais de tenue de compte sont de 2,00 € par mois.

    @ParameterizedTest
    @CsvSource({
            "30, 500",
            "40, 100",
            "50, 999",
            "60, 0"
    })
    void fraisMensuels_conditionsNormales_retourneDeuxEuros(
            int age, String solde) {

        // Arrange
        CalculFrais calculFrais = new CalculFrais();
        BigDecimal soldeMoyen = new BigDecimal(solde);

        // Act
        BigDecimal resultat = calculFrais.fraisMensuels(
                age, soldeMoyen, false);

        // Assert
        assertEquals(0, new BigDecimal("2.00").compareTo(resultat));
    }

    // F3 - Les frais de tenue sont offerts aux titulaires de moins de 26 ans.

    @ParameterizedTest
    @CsvSource({
            "18",
            "20",
            "24",
            "25"
    })
    void fraisMensuels_titulaireMoinsDe26Ans_fraisOfferts(int age) {

        // Arrange
        CalculFrais calculFrais = new CalculFrais();
        BigDecimal soldeMoyen = new BigDecimal("500");

        // Act
        BigDecimal resultat = calculFrais.fraisMensuels(
                age, soldeMoyen, false);

        // Assert
        assertEquals(0, BigDecimal.ZERO.compareTo(resultat));
    }

    @ParameterizedTest
    @CsvSource({
            "26",
            "27",
            "30",
            "50"
    })
    void fraisMensuels_titulaireDe26AnsOuPlus_fraisNonOfferts(int age) {

        // Arrange
        CalculFrais calculFrais = new CalculFrais();
        BigDecimal soldeMoyen = new BigDecimal("500");

        // Act
        BigDecimal resultat = calculFrais.fraisMensuels(
                age, soldeMoyen, false);

        // Assert
        assertEquals(0, new BigDecimal("2.00").compareTo(resultat));
    }

    // F4 - Les frais de tenue sont offerts si le solde moyen atteint 1 000 € ou
    // plus.

    @ParameterizedTest
    @CsvSource({
            "999.99, 2.00",
            "1000.00, 0.00",
            "1000.01, 0.00",
            "1500.00, 0.00"
    })
    void fraisMensuels_selonSoldeMoyen_appliqueFraisAttendus(
            String solde, String fraisAttendus) {

        // Arrange
        CalculFrais calculFrais = new CalculFrais();
        BigDecimal soldeMoyen = new BigDecimal(solde);
        BigDecimal attendu = new BigDecimal(fraisAttendus);

        // Act
        BigDecimal resultat = calculFrais.fraisMensuels(
                30, soldeMoyen, false);

        // Assert
        assertEquals(0, attendu.compareTo(resultat));
    }
}
