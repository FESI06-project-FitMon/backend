package site.fitmon.fitmon.common.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/test")
    public String hello() {
        return "스웨거 테스트";
    }
}
