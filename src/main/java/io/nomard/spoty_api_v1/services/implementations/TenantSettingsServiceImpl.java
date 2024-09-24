package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.TenantSettings;
import io.nomard.spoty_api_v1.repositories.TenantSettingsRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.TenantSettingsService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;

@Service
@Log
public class TenantSettingsServiceImpl implements TenantSettingsService {
    @Autowired
    private TenantSettingsRepository settingsRepo;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private AuthServiceImpl authService;

    @Override
    public TenantSettings getSettings() {
        return settingsRepo.findByTenantId(authService.authUser().getTenant().getId());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(TenantSettings settings) {
        var opt = Optional.ofNullable(settingsRepo.findByTenantId(authService.authUser().getTenant().getId()));
        if (opt.isEmpty()) {
            try {
                for (int i = 0; i < settings.getApprovers().size(); i++) {
                    settings.getApprovers().get(i).setTenant(authService.authUser().getTenant());
                    settings.getApprovers().get(i).setBranch(authService.authUser().getBranch());
                }
                settings.setTenant(authService.authUser().getTenant());
                settings.setCreatedAt(LocalDateTime.now());
                settings.setUpdatedAt(LocalDateTime.now());
                settings.setTenant(authService.authUser().getTenant());
                settingsRepo.save(settings);
                return spotyResponseImpl.created();
            } catch (Exception e) {
                log.log(Level.ALL, e.getMessage(), e);
                return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            }
        } else {
            return spotyResponseImpl.custom(HttpStatus.CONFLICT, HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            settingsRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
