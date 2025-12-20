package az.qrfood.backend.alive.controller;

import az.qrfood.backend.alive.dto.Alive;
import az.qrfood.backend.constant.ApiRoutes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Controller
@Tag(name = "Application Status", description = "API endpoints for checking application liveness and version.")
public class AliveController {

    @Value("${app.version}")
    private String version;
    @Value("${spring.application.name}")
    private String applicationName;
    private final GitProperties gitProperties;
    private final Map<String, Object> m = new HashMap<>();

    public AliveController(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    /**
     * AliveController request.

     * @return alive
     */
    @RequestMapping(value = ApiRoutes.ALIVE, method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    //[[test]]
    public Alive test() {
        log.trace("Test TRACE log");
        log.info("Test INFO log");
        log.warn("Test WARN log");
        log.error("Test ERROR log");

        Object commitTime = p("\"git.commit.time\"");
        if(commitTime == null) {commitTime = "";}
        String out = "I am still alive:-)";
        return new Alive(applicationName, version, out, commitTime.toString(), LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private Object p(String key) {
        if(m.isEmpty()) {
            gitProperties.forEach(p -> m.put(p.getKey(), p.getValue())); // copies all 27 key-values
        }
        return m.get(key);
    }

}