package com.example.stock.scraper;

import com.example.stock.model.Company;
import com.example.stock.model.Dividend;
import com.example.stock.model.ScrapedResult;
import com.example.stock.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String URL = "https://ca.finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final long START_TIME = 86400;
    private static final String SUMMARY_URL = "https://ca.finance.yahoo.com/quote/%s?p=%s";

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);
        try{
            long now = System.currentTimeMillis() / 1000;
            String url = String.format(URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements elements = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element ele = elements.get(0);

            Element tbody = ele.children().get(1);
            List<Dividend> dividends = new ArrayList<>();
            for(Element e : tbody.children()) {
                String txt = e.text();
                if(!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0 ,0))
                        .dividend(dividend)
                        .build());
            }
            scrapResult.setDividendEntities(dividends);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);
        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[1].trim();

            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
