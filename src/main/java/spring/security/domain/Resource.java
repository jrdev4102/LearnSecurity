package spring.security.domain;

import lombok.*;
import spring.security.domain.common.BaseTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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