package az.qrfood.backend.common.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for health check and liveness probes.
 * <p>
 * This controller provides a simple endpoint to check if the application is running and responsive.
 * It's typically used by load balancers or orchestration systems to determine application health.
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/ui/alive")
public class Alive {


    /**
     * Health check endpoint.
     * <p>
     * Returns an "Ok" response with HTTP status 200 if the application is alive.
     * </p>
     *
     * @return A {@link ResponseEntity} with a "Ok" string and HTTP status 200.
     */
    @GetMapping()
    @ResponseBody
    public ResponseEntity<String> alive() {
        return ResponseEntity.ok("Ok");
    }

}
