package com.example.stock.scheduler;

import com.example.stock.model.Company;
import com.example.stock.model.ScrapedResult;
import com.example.stock.persist.CompanyRepository;
import com.example.stock.persist.DividendRepository;
import com.example.stock.persist.entity.CompanyEntity;
import com.example.stock.persist.entity.DividendEntity;
import com.example.stock.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class TestScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

//    @Scheduled(cron = "0/5 * * * * *")
//    public void test() {
//        System.out.println("now -> " + System.currentTimeMillis());
//    }

    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // search saved company
        List<CompanyEntity> companies =  this.companyRepository.findAll();
        // scrap company data
        for (var company : companies) {
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()
                    .name(company.getName())
                    .ticker(company.getTicker())
                    .build());

            // scraped data save the ones that are not saved before
            scrapedResult.getDividendEntities().stream()
                    .map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(),e.getDate());
                        if(!exists) {
                            this.dividendRepository.save(e);
                        }
                    });

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();            }
        }
    }
}
