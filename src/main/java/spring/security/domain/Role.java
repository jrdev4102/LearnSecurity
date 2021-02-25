package spring.security.domain;

import lombok.Getter;
import lombok.Setter;
import spring.security.domain.common.BaseTime;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Role extends BaseTime {

    @Id @GeneratedValue
    @Column(name = "role_id")
    private Long id;

    private String authorityId;

    private String description;

    private boolean deleted;

}
