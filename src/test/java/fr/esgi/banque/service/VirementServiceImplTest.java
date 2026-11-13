package fr.esgi.banque.service.impl;

import fr.esgi.banque.business.Virement;
import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class VirementServiceImplTest {

	@Mock
	private CompteRepository repository;

	@Mock
	private NotificationService notifications;

	private VirementServiceImpl service;

	@BeforeEach
	void setUp() {
		Clock horloge = Clock.fixed(Instant.parse("2025-01-15T10:00:00Z"), ZoneId.of("Europe/Paris"));
		service = new VirementServiceImpl(repository, notifications, horloge);
	}

	@ParameterizedTest(name = "Montant refusé : {0}")
	@NullSource
	@ValueSource(strings = {"0", "0.00", "-1", "-0.01"})
	void refuseUnMontantNullNulOuNegatif(BigDecimal montant) {
		assertThrows(IllegalArgumentException.class,
				() -> service.virer("FR001", "FR002", montant));

		verifyNoInteractions(repository, notifications);
	}
}