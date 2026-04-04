package in.manikanta.moneymanager.service;

import in.manikanta.moneymanager.dto.AuthDTO;
import in.manikanta.moneymanager.dto.ProfileDTO;
import in.manikanta.moneymanager.entity.ProfileEntity;
import in.manikanta.moneymanager.repository.ProfileRepository;
import in.manikanta.moneymanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    private  final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Value("${app.activation.url:http://localhost:8080}")
    private String activationURL;


    public ProfileDTO registerProfile(ProfileDTO profileDTO){

        ProfileEntity newProfile=toEntity(profileDTO);

        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        //send activaton link

        String activationaLink = activationURL+"/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject="Activate your Money Manager account";

        String body = "Click on the following link to activate your account:" + activationaLink;

        emailService.sendEmail(newProfile.getEmail(), subject, body);


        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullname(profileDTO.getFullname())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullname(profileEntity.getFullname())
                .email(profileEntity.getEmail())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity:: getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

       return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not Found with email: " + authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser;
        if(email == null){
            currentUser = getCurrentProfile();
        }
        else{
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        }
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullname(currentUser.getFullname())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        ProfileEntity profile = profileRepository.findByEmail(authDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or Password"));

        try {
            boolean passwordMatches = passwordEncoder.matches(authDTO.getPassword(), profile.getPassword());

            // Allow existing legacy plain-text passwords once, then upgrade them to BCrypt.
            if (!passwordMatches && authDTO.getPassword().equals(profile.getPassword())) {
                profile.setPassword(passwordEncoder.encode(authDTO.getPassword()));
                profileRepository.save(profile);
                passwordMatches = true;
            }

            if (!passwordMatches) {
                throw new RuntimeException("Invalid email or Password");
            }

            String token = jwtUtil.generateToken(profile.getEmail());

            return Map.of(
                    "token", token,
                    "user", toDTO(profile)
            );

        }
        catch (Exception e){
            throw new RuntimeException("Invalid email or Password");
        }
    }
}
