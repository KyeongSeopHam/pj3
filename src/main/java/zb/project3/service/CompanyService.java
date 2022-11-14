package zb.project3.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import zb.project3.model.Company;
import zb.project3.model.ScrapedResult;
import zb.project3.persist.CompanyRepository;
import zb.project3.persist.DividendRepository;
import zb.project3.persist.entity.CompanyEntity;
import zb.project3.persist.entity.DividendEntity;
import zb.project3.scraper.Scraper;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) {

        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }


        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);


        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);
        return company;
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    public void addAutocompleteKeyWord(String keyword) { //trie 에 넣기
        this.trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) { //trie 에서 단어를 찾아오는 로직
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()

                .collect(Collectors.toList());

    }

    public void deleteAutocompleteKeyword(String keyword) { //trie 에서 데이터 삭제
        this.trie.remove(keyword);
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(
                keyword, limit);
        return companyEntities.stream().map(e -> e.getName())
                .collect(Collectors.toList());
    }
}
