package spring.security.domain;

import lombok.*;
import spring.security.domain.common.BaseTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Resource extends BaseTime implements Serializable {

    @Id @GeneratedValue
    @Column(name = "resource_id")
    private Long id;
    
    private String url;
    
    private String method;
    
    private boolean deleted;

}