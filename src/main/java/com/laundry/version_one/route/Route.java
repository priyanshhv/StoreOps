package com.laundry.version_one.route;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
@RequiredArgsConstructor
@Tag(name = "Test")
public class Route {

    @GetMapping
    public ResponseEntity<String> fun(){
        return ResponseEntity.ok("hello");
    }
}
