package spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Controller;

@Controller
public class ResourceController {

    @Autowired
    private FilterInvocationSecurityMetadataSource metadataSource;

}
