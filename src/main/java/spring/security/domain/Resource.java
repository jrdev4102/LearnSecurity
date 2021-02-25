package spring.security.domain;

import lombok.Getter;
import lombok.Setter;
import spring.security.domain.common.BaseTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Resource extends BaseTime {

    @Id @GeneratedValue
    @Column(name = "resource_id")
    private Long id;

    private String url;

    private String method;

    private boolean deleted;

}