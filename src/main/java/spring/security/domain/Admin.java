package spring.security.domain;

import lombok.Getter;
import lombok.Setter;
import spring.security.domain.common.BaseTime;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Admin extends BaseTime {

    @Id @GeneratedValue
    @Column(name = "admin_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id", referencedColumnName = "authorityId")
    private Role authorityId;

}
