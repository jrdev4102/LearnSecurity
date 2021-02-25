package spring.security.domain;

import lombok.Getter;
import lombok.Setter;
import spring.security.domain.common.BaseTime;

import javax.persistence.*;

@Entity
@Getter @Setter
public class RoleResource extends BaseTime {

    @Id @GeneratedValue
    @Column(name = "role_resource_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private boolean deleted;

}
