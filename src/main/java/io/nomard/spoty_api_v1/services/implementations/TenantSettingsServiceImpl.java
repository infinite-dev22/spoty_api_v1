package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.entities.TenantSettings;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.TenantSettingsDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.mappers.TenantSettingsMapper;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.repositories.ApproverRepository;
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
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

@Service
@Log
public class TenantSettingsServiceImpl implements TenantSettingsService {
    @Autowired
    private TenantSettingsRepository settingsRepo;
    @Autowired
    private ApproverRepository approverRepo;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private TenantSettingsMapper tenantSettingsMapper;

    @Override
    public TenantSettingsDTO getSettings() {
        return tenantSettingsMapper.toDTO(settingsRepo.findByTenantId(authService.authUser().getTenant().getId()));
    }

    @Override
    public TenantSettings getSettingsInternal() {
        return settingsRepo.findByTenantId(authService.authUser().getTenant().getId());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(TenantSettings settings) {
        var opt = Optional.ofNullable(settingsRepo.findByTenantId(authService.authUser().getTenant().getId()));
        if (opt.isEmpty()) {
            try {
                // for (int i = 0; i < settings.getApprovers().size(); i++) {
                //     settings.getApprovers().get(i).setTenant(authService.authUser().getTenant());
                //     settings.getApprovers().get(i).setBranch(authService.authUser().getBranch());
                // }

                settings.setReviewers(settings.getReviewers().stream().peek(approver -> {
                    approver.setTenant(authService.authUser().getTenant());
                    approver.setBranch(authService.authUser().getBranch());
                }).toList());

                settings.setTenant(authService.authUser().getTenant());
                settings.setCreatedAt(LocalDateTime.now());
                settings.setUpdatedAt(LocalDateTime.now());
                settings.setTenant(authService.authUser().getTenant());
                settings.getReviewers().forEach(approver -> approver.setTenant(authService.authUser().getTenant()));
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
    public ResponseEntity<ObjectNode> update(TenantSettings settings) {
        var opt = Optional.ofNullable(settingsRepo.findByTenantId(authService.authUser().getTenant().getId()));
        if (opt.isPresent()) {
            var tenantSettings = opt.get();

            if (!Objects.equals(tenantSettings.getName(), settings.getName()) &&
                    Objects.nonNull(settings.getName()) && !settings.getName().isEmpty()) {
                tenantSettings.setName(settings.getName());
            }

            if (!Objects.equals(tenantSettings.getWebsiteLink(), settings.getWebsiteLink()) &&
                    Objects.nonNull(settings.getWebsiteLink()) && !settings.getWebsiteLink().isEmpty()) {
                tenantSettings.setWebsiteLink(settings.getWebsiteLink());
            }

            if (!Objects.equals(tenantSettings.getPhoneNumber(), settings.getPhoneNumber()) &&
                    Objects.nonNull(settings.getPhoneNumber()) && !settings.getPhoneNumber().isEmpty()) {
                tenantSettings.setPhoneNumber(settings.getPhoneNumber());
            }

            if (!Objects.equals(tenantSettings.getEmail(), settings.getEmail()) &&
                    Objects.nonNull(settings.getEmail()) && !settings.getEmail().isEmpty()) {
                tenantSettings.setEmail(settings.getEmail());
            }

            if (!Objects.equals(tenantSettings.getSupportEmail(), settings.getSupportEmail()) &&
                    Objects.nonNull(settings.getSupportEmail()) && !settings.getSupportEmail().isEmpty()) {
                tenantSettings.setSupportEmail(settings.getSupportEmail());
            }

            if (!Objects.equals(tenantSettings.getInfoEmail(), settings.getInfoEmail()) &&
                    Objects.nonNull(settings.getInfoEmail()) && !settings.getInfoEmail().isEmpty()) {
                tenantSettings.setInfoEmail(settings.getInfoEmail());
            }

            if (!Objects.equals(tenantSettings.getHrEmail(), settings.getHrEmail()) &&
                    Objects.nonNull(settings.getHrEmail()) && !settings.getHrEmail().isEmpty()) {
                tenantSettings.setHrEmail(settings.getHrEmail());
            }

            if (!Objects.equals(tenantSettings.getSalesEmail(), settings.getSalesEmail()) &&
                    Objects.nonNull(settings.getSalesEmail()) && !settings.getSalesEmail().isEmpty()) {
                tenantSettings.setSalesEmail(settings.getSalesEmail());
            }

            if (!Objects.equals(tenantSettings.getPostalAddress(), settings.getPostalAddress()) &&
                    Objects.nonNull(settings.getPostalAddress()) && !settings.getPostalAddress().isEmpty()) {
                tenantSettings.setPostalAddress(settings.getPostalAddress());
            }

            if (!Objects.equals(tenantSettings.getPhysicalAddress(), settings.getPhysicalAddress()) &&
                    Objects.nonNull(settings.getPhysicalAddress()) && !settings.getPhysicalAddress().isEmpty()) {
                tenantSettings.setPhysicalAddress(settings.getPhysicalAddress());
            }

            if (!Objects.equals(tenantSettings.getTagLine(), settings.getTagLine()) &&
                    Objects.nonNull(settings.getTagLine()) && !settings.getTagLine().isEmpty()) {
                tenantSettings.setTagLine(settings.getTagLine());
            }

            if (!Objects.equals(tenantSettings.getReportLogo(), settings.getReportLogo()) &&
                    Objects.nonNull(settings.getReportLogo())) {
                tenantSettings.setReportLogo(settings.getReportLogo());
            }

            if (!Objects.equals(tenantSettings.getEmailLogo(), settings.getEmailLogo()) &&
                    Objects.nonNull(settings.getEmailLogo())) {
                tenantSettings.setEmailLogo(settings.getEmailLogo());
            }

            if (!Objects.equals(tenantSettings.getReceiptLogo(), settings.getReceiptLogo()) &&
                    Objects.nonNull(settings.getReceiptLogo())) {
                tenantSettings.setReceiptLogo(settings.getReceiptLogo());
            }

            if (!Objects.equals(tenantSettings.getTwitter(), settings.getTwitter()) &&
                    Objects.nonNull(settings.getTwitter()) && !settings.getTwitter().isEmpty()) {
                tenantSettings.setTwitter(settings.getTwitter());
            }

            if (!Objects.equals(tenantSettings.getFacebook(), settings.getFacebook()) &&
                    Objects.nonNull(settings.getFacebook()) && !settings.getFacebook().isEmpty()) {
                tenantSettings.setFacebook(settings.getFacebook());
            }

            if (!Objects.equals(tenantSettings.getLinkedIn(), settings.getLinkedIn()) &&
                    Objects.nonNull(settings.getLinkedIn()) && !settings.getLinkedIn().isEmpty()) {
                tenantSettings.setLinkedIn(settings.getLinkedIn());
            }

            if (!Objects.equals(tenantSettings.getApproveAdjustments(), settings.getApproveAdjustments()) &&
                    Objects.nonNull(settings.getApproveAdjustments())) {
                tenantSettings.setApproveAdjustments(settings.getApproveAdjustments());
            }

            if (!Objects.equals(tenantSettings.getApproveRequisitions(), settings.getApproveRequisitions()) &&
                    Objects.nonNull(settings.getApproveRequisitions())) {
                tenantSettings.setApproveRequisitions(settings.getApproveRequisitions());
            }

            if (!Objects.equals(tenantSettings.getApproveTransfers(), settings.getApproveTransfers()) &&
                    Objects.nonNull(settings.getApproveTransfers())) {
                tenantSettings.setApproveTransfers(settings.getApproveTransfers());
            }

            if (!Objects.equals(tenantSettings.getApproveStockIns(), settings.getApproveStockIns()) &&
                    Objects.nonNull(settings.getApproveStockIns())) {
                tenantSettings.setApproveStockIns(settings.getApproveStockIns());
            }

            if (!Objects.equals(tenantSettings.getApproveQuotations(), settings.getApproveQuotations()) &&
                    Objects.nonNull(settings.getApproveQuotations())) {
                tenantSettings.setApproveQuotations(settings.getApproveQuotations());
            }

            if (!Objects.equals(tenantSettings.getApprovePurchases(), settings.getApprovePurchases()) &&
                    Objects.nonNull(settings.getApprovePurchases())) {
                tenantSettings.setApprovePurchases(settings.getApprovePurchases());
            }

            if (!Objects.equals(tenantSettings.getApproveSaleReturns(), settings.getApproveSaleReturns()) &&
                    Objects.nonNull(settings.getApproveSaleReturns())) {
                tenantSettings.setApproveSaleReturns(settings.getApproveSaleReturns());
            }

            if (!Objects.equals(tenantSettings.getApprovePurchaseReturns(), settings.getApprovePurchaseReturns()) &&
                    Objects.nonNull(settings.getApprovePurchaseReturns())) {
                tenantSettings.setApprovePurchaseReturns(settings.getApprovePurchaseReturns());
            }

            if (!Objects.equals(tenantSettings.getApprovalLevels(), settings.getApprovalLevels()) &&
                    Objects.nonNull(settings.getApprovalLevels())) {
                tenantSettings.setApprovalLevels(settings.getApprovalLevels());
            }

            if (!Objects.equals(tenantSettings.getReviewers(), settings.getReviewers()) &&
                    Objects.nonNull(settings.getReviewers()) && !settings.getReviewers().isEmpty()) {
                tenantSettings.setReviewers(settings.getReviewers().stream().peek(approver -> {
                    approver.setTenant(authService.authUser().getTenant());
                    approver.setBranch(authService.authUser().getBranch());
                }).toList());
            }

            if (!Objects.equals(tenantSettings.getDefaultCurrency(), settings.getDefaultCurrency()) &&
                    Objects.nonNull(settings.getDefaultCurrency())) {
                tenantSettings.setDefaultCurrency(settings.getDefaultCurrency());
            }

            if (!Objects.equals(tenantSettings.getLogo(), settings.getLogo()) &&
                    Objects.nonNull(settings.getLogo()) && !settings.getLogo().isEmpty()) {
                tenantSettings.setLogo(settings.getLogo());
            }

            tenantSettings.setUpdatedAt(LocalDateTime.now());
            tenantSettings.setTenant(authService.authUser().getTenant());
            try {
                settingsRepo.save(tenantSettings);
                return spotyResponseImpl.created();
            } catch (Exception e) {
                log.log(Level.ALL, e.getMessage(), e);
                return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            }
        } else {
            return spotyResponseImpl.custom(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> addReviewer(Reviewer reviewer) {
        var opt = Optional.ofNullable(settingsRepo.findByTenantId(authService.authUser().getTenant().getId()));
        if (opt.isPresent()) {
            var tenantSettings = opt.get();

            if (Objects.nonNull(reviewer)) {
                reviewer.setTenant(authService.authUser().getTenant());
                reviewer.setBranch(authService.authUser().getBranch());
                tenantSettings.getReviewers().add(reviewer);
            }

            tenantSettings.setUpdatedAt(LocalDateTime.now());
            try {
                settingsRepo.save(tenantSettings);
                return spotyResponseImpl.created();
            } catch (Exception e) {
                log.log(Level.ALL, e.getMessage(), e);
                return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            }
        } else {
            return spotyResponseImpl.custom(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> removeReviewer(FindModel findModel) {
        var opt1 = Optional.ofNullable(settingsRepo.findByTenantId(authService.authUser().getTenant().getId()));
        if (opt1.isPresent()) {
            var tenantSettings = opt1.get();

            var opt2 = approverRepo.findById(findModel.getId());
            if (opt2.isPresent()) {
                var approver = opt2.get();
                tenantSettings.getReviewers().remove(approver);
                tenantSettings.setUpdatedAt(LocalDateTime.now());
                try {
                    settingsRepo.save(tenantSettings);
                    return spotyResponseImpl.created();
                } catch (Exception e) {
                    log.log(Level.ALL, e.getMessage(), e);
                    return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
                }
            } else {
                return spotyResponseImpl.custom(HttpStatus.NOT_FOUND, "Reviewer Not Found");
            }
        } else {
            return spotyResponseImpl.custom(HttpStatus.NOT_FOUND, "Tenant Settings Not Found");
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
