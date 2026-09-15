package com.enviro.assessment.junior.nonhle.service;

import com.enviro.assessment.junior.nonhle.entity.WithdrawalNotice;
import com.enviro.assessment.junior.nonhle.repository.WithdrawalNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Builds CSV withdrawal statements. Written manually (no external CSV
 * library) since the format here is simple and it avoids an extra
 * dependency for a junior-level assessment - each field is wrapped in
 * escapeCsv() to handle any embedded commas/quotes safely.
 */
@Service
@RequiredArgsConstructor
public class CsvExportService {

    private static final String[] HEADERS = {
            "Notice ID", "Portfolio ID", "Amount", "Notice Date",
            "Status", "Rejection Reason", "Resulting Balance"
    };

    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    /**
     * Builds a CSV statement for one investor's withdrawal history,
     * optionally filtered to a date range. If from/to are null, no date
     * filtering is applied (though the underlying query still needs a
     * range, so callers pass a very wide default range in that case -
     * see CsvExportService.DEFAULT_FROM/DEFAULT_TO usage in the controller).
     */
    @Transactional(readOnly = true)
    public String buildWithdrawalStatementCsv(Long investorId, LocalDate from, LocalDate to) {
        List<WithdrawalNotice> notices =
                withdrawalNoticeRepository.findByPortfolioInvestorIdAndNoticeDateBetween(investorId, from, to);

        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",", HEADERS)).append("\n");

        for (WithdrawalNotice notice : notices) {
            csv.append(escapeCsv(notice.getId())).append(",")
               .append(escapeCsv(notice.getPortfolio().getId())).append(",")
               .append(escapeCsv(notice.getAmount())).append(",")
               .append(escapeCsv(notice.getNoticeDate())).append(",")
               .append(escapeCsv(notice.getStatus())).append(",")
               .append(escapeCsv(notice.getRejectionReason())).append(",")
               .append(escapeCsv(notice.getResultingBalance())).append("\n");
        }

        return csv.toString();
    }

    /**
     * Wraps a field in quotes and escapes any internal quotes, only when
     * necessary (contains a comma, quote, or newline) - standard CSV
     * escaping so fields like a rejection reason containing commas don't
     * break the file structure.
     */
    private String escapeCsv(Object value) {
        if (value == null) {
            return "";
        }
        String str = value.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }
}
