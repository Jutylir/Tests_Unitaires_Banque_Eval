
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
            () -> calculFrais.fraisMensuels(age, soldeMoyen, false)
        );

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
            () -> calculFrais.fraisMensuels(age, soldeMoyen, false)
        );

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
            () -> calculFrais.fraisMensuels(age, null, false)
        );

        // Assert
        assertEquals("Le solde moyen est obligatoire", exception.getMessage());
    }
}
