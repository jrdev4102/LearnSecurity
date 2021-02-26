package spring.security.domain;

import lombok.*;
import spring.security.domain.common.BaseTime;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "authorityId")
public class Admin extends BaseTime implements Serializable {

    @Id @GeneratedValue
    @Column(name = "admin_id")
    private Long id;

    private String username;

    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id", referencedColumnName = "authorityId")
    private Role authorityId;

}
