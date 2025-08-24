package az.qrfood.backend.alive.controller;

import az.qrfood.backend.alive.dto.Alive;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
@Controller
@Tag(name = "Application Status", description = "API endpoints for checking application liveness and version.")
public class AliveController {

    @Value("${app.version}")
    private String version;
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * AliveController request.

     * @return alive
     */
    @RequestMapping(value = "/api/alive", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    //[[test]]
    public Alive test() {
        log.trace("Test TRACE log");
        log.info("Test INFO log");
        log.warn("Test WARN log");
        log.error("Test ERROR log");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String out = "I am still alive:-)";
        return new Alive(applicationName, version, out, dtf.format(now));
    }

}