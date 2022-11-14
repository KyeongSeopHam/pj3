package zb.project3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Company {

    private String ticker;
    private String name;

    public Company() {
    }

}
