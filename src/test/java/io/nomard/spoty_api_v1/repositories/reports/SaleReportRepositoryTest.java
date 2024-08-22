package io.nomard.spoty_api_v1.repositories.reports;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class SaleReportRepositoryTest {
	@Autowired
	private SaleReportRepository saleReportRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void findAllByTenantIdAndCreatedAtBetween() {
		Long id = 1L;
		LocalDateTime startDate = LocalDateTime.of(2024, 7, 1, 12, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 9, 1, 12, 0, 0);
		Boolean expected = true;
		Boolean actual = !saleReportRepository.findAllByTenantIdAndCreatedAtBetween(id, startDate, endDate).isEmpty();

		assertEquals(expected, actual);
	}
}
