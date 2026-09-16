package com.enviro.assessment.junior.nonhle.service;

import com.enviro.assessment.junior.nonhle.dto.WithdrawalRequest;
import com.enviro.assessment.junior.nonhle.dto.WithdrawalResponse;
import com.enviro.assessment.junior.nonhle.entity.*;
import com.enviro.assessment.junior.nonhle.exception.InvalidWithdrawalException;
import com.enviro.assessment.junior.nonhle.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.nonhle.repository.PortfolioRepository;
import com.enviro.assessment.junior.nonhle.repository.WithdrawalNoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the withdrawal business rules - the highest-value thing to
 * test in this codebase, since this is where the assessment's core
 * requirements (age > 65, balance checks, 90% cap) actually live.
 *
 * Repositories are mocked so these tests run in-memory with no database,
 * isolating exactly the logic in WithdrawalService itself.
 */
@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private WithdrawalService withdrawalService;

    private Investor youngInvestor;
    private Investor retiredInvestor;
    private Product retirementProduct;
    private Product unitTrustProduct;

    @BeforeEach
    void setUp() {
        youngInvestor = Investor.builder()
                .id(1L)
                .firstName("Sarah")
                .lastName("Young")
                .dateOfBirth(LocalDate.now().minusYears(36))
                .email("sarah@example.com")
                .build();

        retiredInvestor = Investor.builder()
                .id(2L)
                .firstName("John")
                .lastName("Retiree")
                .dateOfBirth(LocalDate.now().minusYears(71))
                .email("john@example.com")
                .build();

        retirementProduct = Product.builder()
                .id(1L).name("Retirement Annuity").type(ProductType.RETIREMENT_ANNUITY).build();

        unitTrustProduct = Product.builder()
                .id(2L).name("Unit Trust").type(ProductType.UNIT_TRUST).build();
    }

    private Portfolio portfolioOf(Investor investor, Product product, BigDecimal balance) {
        return Portfolio.builder()
                .id(10L)
                .investor(investor)
                .product(product)
                .balance(balance)
                .build();
    }

    @Test
    void approvesAValidWithdrawal() {
        Portfolio portfolio = portfolioOf(youngInvestor, unitTrustProduct, new BigDecimal("100000.00"));
        when(portfolioRepository.findById(10L)).thenReturn(Optional.of(portfolio));
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalResponse response = withdrawalService.createWithdrawal(
                new WithdrawalRequest(10L, new BigDecimal("5000.00")));

        assertThat(response.getStatus()).isEqualTo(WithdrawalStatus.APPROVED);
        assertThat(response.getResultingBalance()).isEqualByComparingTo("95000.00");
        verify(portfolioRepository).save(portfolio);
    }

    @Test
    void rejectsRetirementWithdrawalWhenInvestorIsNotOver65() {
        Portfolio portfolio = portfolioOf(youngInvestor, retirementProduct, new BigDecimal("300000.00"));
        when(portfolioRepository.findById(10L)).thenReturn(Optional.of(portfolio));

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(
                new WithdrawalRequest(10L, new BigDecimal("1000.00"))))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessageContaining("over age 65");

        verify(withdrawalNoticeRepository, never()).save(any());
    }

    @Test
    void allowsRetirementWithdrawalWhenInvestorIsOver65() {
        Portfolio portfolio = portfolioOf(retiredInvestor, retirementProduct, new BigDecimal("300000.00"));
        when(portfolioRepository.findById(10L)).thenReturn(Optional.of(portfolio));
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalResponse response = withdrawalService.createWithdrawal(
                new WithdrawalRequest(10L, new BigDecimal("10000.00")));

        assertThat(response.getStatus()).isEqualTo(WithdrawalStatus.APPROVED);
    }

    @Test
    void rejectsWithdrawalExceedingBalance() {
        Portfolio portfolio = portfolioOf(youngInvestor, unitTrustProduct, new BigDecimal("10000.00"));
        when(portfolioRepository.findById(10L)).thenReturn(Optional.of(portfolio));

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(
                new WithdrawalRequest(10L, new BigDecimal("15000.00"))))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessageContaining("exceeds available balance");
    }

    @Test
    void rejectsWithdrawalExceedingNinetyPercentCap() {
        // Balance 10000, 90% cap = 9000. Requesting 9500 is within balance
        // but breaks the cap - this proves rule 3 fires independently of rule 2.
        Portfolio portfolio = portfolioOf(youngInvestor, unitTrustProduct, new BigDecimal("10000.00"));
        when(portfolioRepository.findById(10L)).thenReturn(Optional.of(portfolio));

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(
                new WithdrawalRequest(10L, new BigDecimal("9500.00"))))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessageContaining("90% of balance");
    }

    @Test
    void rejectsZeroOrNegativeAmount() {
        Portfolio portfolio = portfolioOf(youngInvestor, unitTrustProduct, new BigDecimal("10000.00"));
        when(portfolioRepository.findById(10L)).thenReturn(Optional.of(portfolio));

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(
                new WithdrawalRequest(10L, BigDecimal.ZERO)))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void throwsWhenPortfolioDoesNotExist() {
        when(portfolioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(
                new WithdrawalRequest(999L, new BigDecimal("1000.00"))))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
