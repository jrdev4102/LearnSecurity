package spring.security.security.service.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import spring.security.domain.Admin;

import java.util.Collection;

@Getter
public class AdminContext extends User {

    private final Admin admin;

    public AdminContext(Admin admin, Collection<? extends GrantedAuthority> authorities) {
        super(admin.getUsername(), admin.getPassword(), authorities);
        this.admin = admin;
    }

}
