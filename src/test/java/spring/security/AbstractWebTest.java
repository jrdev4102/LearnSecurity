package spring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
public abstract class AbstractWebTest {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

}
