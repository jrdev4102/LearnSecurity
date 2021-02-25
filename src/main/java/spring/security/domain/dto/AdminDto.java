package spring.security.domain.dto;

import lombok.Data;
import spring.security.domain.Role;

import java.time.LocalDateTime;

@Data
public class AdminDto {

    private Long id;

    private Role authorityId;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

}
