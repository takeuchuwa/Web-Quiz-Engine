package engine.quiz.controlers;

import engine.quiz.entity.User;
import engine.quiz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserProcessor {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(path = "/api/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User account) {
        if (userRepository.findUserByEmail(account.getEmail()).isPresent()) {
            return new ResponseEntity<>(account, HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setEmail(account.getEmail());
        user.setPassword(passwordEncoder.encode(account.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
