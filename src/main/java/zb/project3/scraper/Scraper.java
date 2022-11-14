package zb.project3.scraper;

import zb.project3.model.Company;
import zb.project3.model.ScrapedResult;


public interface Scraper {

    Company scrapCompanyByTicker(String ticker);

    ScrapedResult scrap(Company company);


}
