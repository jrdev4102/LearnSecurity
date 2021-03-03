package spring.security.domain;

import lombok.*;
import spring.security.domain.common.BaseTime;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Role extends BaseTime implements Serializable {

    @Id @GeneratedValue
    @Column(name = "role_id")
    private Long id;
    
    private String authorityId;
    
    private String description;
    
    private boolean deleted;

}
