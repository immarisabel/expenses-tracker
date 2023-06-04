package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ExpenseUploadResult {
    private int nonDuplicates = 0;
    private int duplicates = 0;

    public int getNonDuplicates() {
        log.info("GET " + nonDuplicates);
        return nonDuplicates;
    }

    public void setNonDuplicates(int nonDuplicates) {
        log.info("SET " + nonDuplicates);
        this.nonDuplicates = nonDuplicates;
    }

    public int getDuplicates() {
        log.info("GET " + duplicates);
        return duplicates;
    }

    public void setDuplicates(int duplicates) {
        log.info("SET " + duplicates);
        this.duplicates = duplicates;
    }
}