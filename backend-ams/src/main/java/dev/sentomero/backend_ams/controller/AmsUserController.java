package dev.sentomero.backend_ams.controller;

import dev.sentomero.backend_ams.dto.AmsUserDto;
import dev.sentomero.backend_ams.dto.AuthResponse;
import dev.sentomero.backend_ams.dto.ErrorResponse;
import dev.sentomero.backend_ams.service.AmsUserService;
import dev.sentomero.backend_ams.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/users")
public class AmsUserController {

    private final AmsUserService amsUserService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AmsUserController(AmsUserService amsUserService,AuthenticationManager authenticationManager) {
        this.amsUserService = amsUserService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody AmsUserDto loginRequest, HttpSession session) {
        try {
            System.out.println("Login attempt for user: " + loginRequest.getAmsUsername()); // Log the username

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getAmsUsername(),
                            loginRequest.getAmsPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Generate the token
            String token = jwtTokenUtil.generateToken(loginRequest.getAmsUsername());

            // Return the token and user details
            AmsUserDto userDetails = amsUserService.getUserByUsername(loginRequest.getAmsUsername());
            AuthResponse response = new AuthResponse(token, userDetails);

            // Explicitly set the content type to application/json
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON) // Explicitly set the content type
                    .body(new ErrorResponse("Invalid username or password"));
        }
    }

    @PostMapping("/createUser")
public ResponseEntity<AmsUserDto> createUser(@RequestBody AmsUserDto amsUserDto) {
    AmsUserDto savedUser = amsUserService.savedUser(amsUserDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
}

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<AmsUserDto> updateUser(@PathVariable("id") int id, @RequestBody AmsUserDto amsUserDto) {
        AmsUserDto updatedUser = amsUserService.updateUser(id, amsUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int id) {
        amsUserService.deleteUser(id);
        return ResponseEntity.ok("User with ID " + id + " has been deleted successfully.");
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<AmsUserDto> getUserById(@PathVariable("id") int id) {
        AmsUserDto user = amsUserService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/AllAmsUsers")
    public ResponseEntity<List<AmsUserDto>> getAllUsers() {
        List<AmsUserDto> users = amsUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

}
