package zb.project3.service;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import zb.project3.model.Company;
import zb.project3.model.Dividend;
import zb.project3.model.ScrapedResult;
import zb.project3.persist.CompanyRepository;
import zb.project3.persist.DividendRepository;
import zb.project3.persist.entity.CompanyEntity;
import zb.project3.persist.entity.DividendEntity;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {

        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));


        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());


        List<Dividend> dividends = new ArrayList<>(); //엔티티를 모델 클래스로 매핑하기
        for (var entity : dividendEntities) {
            dividends.add(Dividend.builder()
                    .date(entity.getDate())
                    .dividend(entity.getDividend())
                    .build());
        }

        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker()).
                name(company.getName()).build(),
                dividends);


    }


}
