package az.qrfood.backend.common.controller;

import az.qrfood.backend.category.dto.DishCategoryDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/ui/alive")
public class Alive {


    @GetMapping()
    @ResponseBody
    public ResponseEntity<String> alive() {
        return ResponseEntity.ok("Ok");
    }

}