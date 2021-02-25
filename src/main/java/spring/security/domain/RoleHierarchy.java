package spring.security.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class RoleHierarchy {

    @Id @GeneratedValue
    @Column(name = "role_hierarchy_id")
    private Long id;

    private String parent;

    private String child;

}
