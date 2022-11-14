package zb.project3.model;

import java.util.List;

import lombok.Data;
import zb.project3.persist.entity.MemberEntity;

public class Auth {

    @Data
    public static class SignIn {
        private String username;
        private String password;

    }

    @Data
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles;

        public MemberEntity toEntity() {
            return MemberEntity.builder()
                    .name(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }

    }

}
