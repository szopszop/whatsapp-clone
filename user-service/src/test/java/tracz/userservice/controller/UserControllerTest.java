package tracz.userservice.controller;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@ActiveProfiles("unit-test")
class UserControllerTest {

//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @MockitoBean
//    private UserService userService;
//
//    private UserController userController;
//
//    static final String EMAIL = "email";
//    static final String TEST_EMAIL = "test@test.com";
//    static final String TEST_PASSWORD = "PasswordPassword123!";
//    static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
//            jwt().jwt(jwt -> jwt.notBefore(Instant.now().minusSeconds(5)));
//
//    UUID userId;
//    UserDTO userDTO;
//    UserCreationRequestDTO request;
//
//    @BeforeEach
//    void setUp() {
//        userId = UUID.randomUUID();
//        userDTO = UserDTO.builder()
//                .id(userId)
//                .email(TEST_EMAIL)
//                .roles(new HashSet<>(List.of("USER")))
//                .build();
//        request = new UserCreationRequestDTO(TEST_EMAIL, TEST_PASSWORD);
//        userController = new UserController(userService);
//    }
//
//    @Test
//    void getUserByIdTest() throws Exception {
//        when(userService.findById(userId)).thenReturn(userDTO);
//
//        mockMvc.perform(get(ApiPaths.USER_API_BY_ID, userId)
//                        .with(jwtRequestPostProcessor))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(userId.toString()))
//                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
//                .andExpect(jsonPath("$.id", is(userId.toString())));
//    }
//
//    @Test
//    void getUserByEmailTest() throws Exception {
//        when(userService.findByEmail(TEST_EMAIL)).thenReturn(userDTO);
//
//        mockMvc.perform(get(ApiPaths.USER_API_BY_EMAIL)
//                        .with(jwtRequestPostProcessor)
//                        .queryParam(EMAIL, TEST_EMAIL))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(userId.toString()));
//    }
//
//    @Test
//    void getUserByIdNotFoundTest() throws Exception {
//        when(userService.findById(any(UUID.class)))
//                .thenThrow(new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));
//
//        mockMvc.perform(get(ApiPaths.USER_API_BY_ID, UUID.randomUUID())
//                        .with(jwtRequestPostProcessor))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value(ExceptionMessages.USER_NOT_FOUND))
//                .andExpect(jsonPath("$.error").value(ExceptionMessages.NOT_FOUND))
//                .andExpect(jsonPath("$.status").value(404));
//    }
//
//    @Test
//    void getUserByEmailNotFoundTest() throws Exception {
//        when(userService.findByEmail(any(String.class)))
//                .thenThrow(new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));
//
//        mockMvc.perform(get(ApiPaths.USER_API_BY_EMAIL)
//                        .with(jwtRequestPostProcessor)
//                        .queryParam(EMAIL, TEST_EMAIL))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value(ExceptionMessages.USER_NOT_FOUND))
//                .andExpect(jsonPath("$.error").value(ExceptionMessages.NOT_FOUND))
//                .andExpect(jsonPath("$.status").value(404));
//    }
//
//    @Test
//    void getUserByIdUnauthenticatedTest() throws Exception {
//        mockMvc.perform(get(ApiPaths.USER_API_BY_ID, userId))
//                .andExpect(status().isUnauthorized())
//                .andExpect(header().string("WWW-Authenticate", "Basic realm=\"Realm\""));
//
//    }
//
//    @Test
//    void getUserByEmailUnauthenticatedTest() throws Exception {
//        mockMvc.perform(get(ApiPaths.USER_API_BY_EMAIL)
//                        .queryParam(EMAIL, TEST_EMAIL))
//                .andExpect(status().isUnauthorized())
//                .andExpect(header().string("WWW-Authenticate", "Basic realm=\"Realm\""));
//    }
//
//
//
//    @Test
//    void testExistsByEmail() throws Exception {
//        when(userService.existsByEmail(any(String.class))).thenReturn(true);
//        mockMvc.perform(get(ApiPaths.USER_API_EXISTS_BY_EMAIL)
//                        .queryParam(EMAIL, TEST_EMAIL)
//                        .with(jwtRequestPostProcessor)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string("true"));
//    }
}