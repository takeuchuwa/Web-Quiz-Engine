package engine.quiz.services;

import engine.quiz.entity.User;
import engine.quiz.entity.details.UserDetailsImpl;
import engine.quiz.exceptions.UserNotFoundException;
import engine.quiz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("Not found: " + username);
        }

        return new UserDetailsImpl(user);
    }
}
