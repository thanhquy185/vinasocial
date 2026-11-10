package vn.tuhoc.UserService.customs;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.UserEntity;
import vn.tuhoc.UserService.exceptions.UserNotFoundByUsernameAndPasswordException;
import vn.tuhoc.UserService.repositories.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDetailsServiceCustom implements UserDetailsService {
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundByUsernameAndPasswordException());

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(userEntity.getPassword())
                .authorities("ROLE_" + userEntity.getRole())
                .build();
    }
}