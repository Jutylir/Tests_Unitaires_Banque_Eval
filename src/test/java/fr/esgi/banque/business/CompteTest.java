package fr.esgi.banque.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test d'exemple fourni pour vérifier que le projet est bien configuré.
 * À compléter (partie A).
 */
class CompteTest {

	@Test
	void shouldIncreaseBalanceWhenCrediting() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act
		compte.crediter(new BigDecimal("50.00"));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("150.00");
	}

	@ParameterizedTest(name = "Numero : {0} ; Titulaire : {1} ; Message : {2}")
	@CsvSource(value = {
			"null, Alice, Le numéro de compte est obligatoire",
			"FR001, null, Le titulaire est obligatoire"
	}, nullValues = "null")
	void throwErrorIfNumeroOrTitulaireAreNull(String numero, String titulaire, String expectedMessage) {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new Compte(numero, titulaire, new BigDecimal("100.00"), new BigDecimal("200")));

		assertEquals(expectedMessage, exception.getMessage());
	}

	@Test
	void constructeur_soldeInitialNull_leveIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> new Compte("123", "Theo", null, new BigDecimal("100")));
	}

	@Test
	void constructeur_decouvertAutoriseNull_leveIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> new Compte("123", "Theo", new BigDecimal("100"), null));
	}

	@Test
	void constructeur_decouvertNegatif_leveIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class,
				() -> new Compte("123", "Theo", new BigDecimal("100"), new BigDecimal("-1")));
	}

	@Test
	void crediter_montantNull_leveIllegalArgumentException() {
		Compte compte = new Compte("123", "Theo", new BigDecimal("100"), new BigDecimal("50"));

		assertThrows(IllegalArgumentException.class, () -> compte.crediter(null));
	}

	@Test
	void crediter_montantNegatif_leveIllegalArgumentException() {
		Compte compte = new Compte("123", "Theo", new BigDecimal("100"), new BigDecimal("50"));

		assertThrows(IllegalArgumentException.class, () -> compte.crediter(new BigDecimal("-10")));
	}

	@Test
	void debiter_montantNull_leveIllegalArgumentException() {
		Compte compte = new Compte("123", "Theo", new BigDecimal("100"), new BigDecimal("50"));

		assertThrows(IllegalArgumentException.class, () -> compte.debiter(null));
	}

	@Test
	void debiter_montantNegatif_leveIllegalArgumentException() {
		Compte compte = new Compte("123", "Theo", new BigDecimal("100"), new BigDecimal("50"));

		assertThrows(IllegalArgumentException.class, () -> compte.debiter(new BigDecimal("-10")));
	}

	@Test
	void debiter_montantZero_leveIllegalArgumentException() {
		Compte compte = new Compte("123", "Theo", new BigDecimal("100"), new BigDecimal("50"));

		assertThrows(IllegalArgumentException.class, () -> compte.debiter(BigDecimal.ZERO));
	}

	@Test
	void debiter_soldeSuperieurALaLimite_debitAccepte() {
		Compte compte = new Compte("123", "Theo", new BigDecimal("100"), new BigDecimal("50"));

		compte.debiter(new BigDecimal("120"));

		assertEquals(0, new BigDecimal("-20").compareTo(compte.getSolde()));
	}

	@Test
	void debiter_soldeEgalALaLimite_debitAccepte() {
		Compte compte = new Compte("123", "Theo", new BigDecimal("100"), new BigDecimal("50"));

		compte.debiter(new BigDecimal("150"));

		assertEquals(0, new BigDecimal("-50").compareTo(compte.getSolde()));
	}

	@Test
	void debiter_soldeInferieurALaLimite_leveSoldeInsuffisantException() {
		Compte compte = new Compte("123", "Theo", new BigDecimal("100"), new BigDecimal("50"));

		assertThrows(SoldeInsuffisantException.class, () -> compte.debiter(new BigDecimal("151")));
	}

	@Test
	void debiter_soldeInsuffisant_soldeInchange() {
		Compte compte = new Compte("123", "Theo", new BigDecimal("100"), new BigDecimal("50"));

		BigDecimal soldeInitial = compte.getSolde();

		assertThrows(SoldeInsuffisantException.class, () -> compte.debiter(new BigDecimal("151")));

		assertEquals(0, soldeInitial.compareTo(compte.getSolde()));
	}
}
