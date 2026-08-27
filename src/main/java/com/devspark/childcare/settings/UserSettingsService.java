package com.devspark.childcare.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsDto getSettings(String email) {
        UserSettings settings = userSettingsRepository.findByEmail(email)
                .orElseGet(() -> createDefaultSettings(email));
        
        return UserSettingsDto.builder()
                .theme(settings.getTheme())
                .language(settings.getLanguage())
                .currency(settings.getCurrency())
                .timezone(settings.getTimezone())
                .build();
    }

    @Transactional
    public UserSettingsDto updateSettings(String email, UserSettingsDto dto) {
        UserSettings settings = userSettingsRepository.findByEmail(email)
                .orElseGet(() -> createDefaultSettings(email));

        if (dto.getTheme() != null) settings.setTheme(dto.getTheme());
        if (dto.getLanguage() != null) settings.setLanguage(dto.getLanguage());
        if (dto.getCurrency() != null) settings.setCurrency(dto.getCurrency());
        if (dto.getTimezone() != null) settings.setTimezone(dto.getTimezone());

        UserSettings saved = userSettingsRepository.save(settings);

        return UserSettingsDto.builder()
                .theme(saved.getTheme())
                .language(saved.getLanguage())
                .currency(saved.getCurrency())
                .timezone(saved.getTimezone())
                .build();
    }

    private UserSettings createDefaultSettings(String email) {
        return UserSettings.builder()
                .email(email)
                .theme("light")
                .language("en-US")
                .currency("LKR")
                .timezone("Asia/Colombo")
                .build();
    }
}
