package zb.project3.scheduler;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zb.project3.model.Company;
import zb.project3.model.ScrapedResult;
import zb.project3.persist.CompanyRepository;
import zb.project3.persist.DividendRepository;
import zb.project3.persist.entity.CompanyEntity;
import zb.project3.persist.entity.DividendEntity;
import zb.project3.scraper.Scraper;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;


    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");

        List<CompanyEntity> companies = this.companyRepository.findAll();


        for (var company : companies) {
            log.info("scraping scheduler is started -> " + company.getName()); //로그 남기기

            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()
                    .name(company.getName())
                    .ticker(company.getTicker()).build());


            scrapedResult.getDividends().stream()

                    .map(e -> new DividendEntity(company.getId(), e))

                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(),
                                e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                        }
                    });


            try {
                Thread.sleep(3000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

            }
        }


    }

}
