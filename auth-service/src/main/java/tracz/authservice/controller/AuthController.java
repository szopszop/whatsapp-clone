package tracz.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tracz.authservice.config.ApiPaths;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPaths.AUTH_API)
public class AuthController {

    //private final AuthService authService;


}
