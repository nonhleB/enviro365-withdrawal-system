package com.enviro.assessment.junior.nonhle.controller;

import com.enviro.assessment.junior.nonhle.dto.WithdrawalRequest;
import com.enviro.assessment.junior.nonhle.dto.WithdrawalResponse;
import com.enviro.assessment.junior.nonhle.service.CsvExportService;
import com.enviro.assessment.junior.nonhle.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
public class WithdrawalController {

    // Wide default range used when the caller doesn't specify from/to,
    // so "export everything" still works with the same date-range query.
    private static final LocalDate DEFAULT_FROM = LocalDate.of(2000, 1, 1);
    private static final LocalDate DEFAULT_TO = LocalDate.of(2100, 1, 1);

    private final WithdrawalService withdrawalService;
    private final CsvExportService csvExportService;

    /**
     * POST /api/withdrawals
     * @Valid triggers Bean Validation on WithdrawalRequest first (catches
     * missing/malformed fields via GlobalExceptionHandler), THEN business
     * rule validation runs in the service (age > 65, balance, 90% cap).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WithdrawalResponse createWithdrawal(@Valid @RequestBody WithdrawalRequest request) {
        return withdrawalService.createWithdrawal(request);
    }

    /**
     * GET /api/withdrawals/export?investorId=1&from=2026-01-01&to=2026-12-31
     * Powers the "CSV download button" - from/to are optional query params;
     * omitting them exports the investor's full history.
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam Long investorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        LocalDate effectiveFrom = (from != null) ? from : DEFAULT_FROM;
        LocalDate effectiveTo = (to != null) ? to : DEFAULT_TO;

        String csv = csvExportService.buildWithdrawalStatementCsv(investorId, effectiveFrom, effectiveTo);
        byte[] csvBytes = csv.getBytes();

        String filename = "withdrawal-statement-investor-" + investorId + ".csv";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(csvBytes);
    }

    /**
     * GET /api/withdrawals/portfolio/{portfolioId}
     * Powers the "withdrawal history table" on the frontend.
     */
    @GetMapping("/portfolio/{portfolioId}")
    public List<WithdrawalResponse> getHistory(@PathVariable Long portfolioId) {
        return withdrawalService.getHistoryForPortfolio(portfolioId);
    }
}
