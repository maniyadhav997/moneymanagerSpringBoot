package in.manikanta.moneymanager.service;

import in.manikanta.moneymanager.entity.ProfileEntity;
import in.manikanta.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {


    private final ProfileRepository profileRepository;

    //Responsible Loading profiles from the database
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        ProfileEntity existingProfile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        return User.builder()
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }

}
