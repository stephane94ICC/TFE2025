package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLog;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityLogRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import be.loisirs.tfe2025.plateforme_loisirs.dto.ActivityLogDTO;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivityLogMapper;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Service
public class ActivityLogService {

    private static final int DETAILS_MAX_LENGTH = 500;
    private static final int MAX_PAGE_SIZE = 200;
    private static final Logger logger = LoggerFactory.getLogger(ActivityLogService.class);

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository,
                              UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(ActivityEventType eventType,
                    String targetType,
                    Long targetId,
                    String details) {

        String email = currentUserEmail();
        Long userId = null;

        if (email != null) {
            userId = userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElse(null);
        }

        write(eventType, userId, email, targetType, targetId, details, currentIpAddress());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logForEmail(ActivityEventType eventType,
                            Long userId,
                            String email,
                            String details) {

        write(eventType, userId, email, null, null, details, currentIpAddress());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSystem(ActivityEventType eventType,
                          Long userId,
                          String email,
                          String targetType,
                          Long targetId,
                          String details) {

        write(eventType, userId, email, targetType, targetId, details, null);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> search(ActivityEventType eventType,
                                       String email,
                                       LocalDateTime from,
                                       LocalDateTime to,
                                       int page,
                                       int size) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        String normalizedEmail = (email == null || email.isBlank()) ? null : email.trim();

        return activityLogRepository
                .search(eventType, normalizedEmail, from, to, pageable)
                .map(ActivityLogMapper::toDTO);
    }

    private void write(ActivityEventType eventType,
                       Long userId,
                       String email,
                       String targetType,
                       Long targetId,
                       String details,
                       String ipAddress) {
        try {
            ActivityLog entry = new ActivityLog();
            entry.setEventType(eventType);
            entry.setUserId(userId);
            entry.setUserEmail(email);
            entry.setTargetType(targetType);
            entry.setTargetId(targetId);
            entry.setDetails(truncate(details));
            entry.setIpAddress(ipAddress);

            activityLogRepository.save(entry);

        } catch (Exception exception) {
            logger.error("Échec d'écriture dans le journal d'activité", exception);
        }
    }

    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        return auth.getName();
    }


    private String currentIpAddress() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String truncate(String details) {
        if (details == null) {
            return null;
        }
        return details.length() <= DETAILS_MAX_LENGTH
                ? details
                : details.substring(0, DETAILS_MAX_LENGTH);
    }
}
