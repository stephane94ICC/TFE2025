package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;

import be.loisirs.tfe2025.plateforme_loisirs.dto.ActivityLogDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.service.ActivityLogService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@RestController
@RequestMapping("/api/admin/activity-logs")
public class AdminActivityLogApiController {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private final ActivityLogService activityLogService;

    public AdminActivityLogApiController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public Page<ActivityLogDTO> search(
            @RequestParam(required = false) ActivityEventType eventType,
            @RequestParam(required = false) String email,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {

        LocalDateTime fromDateTime = (from == null) ? null : from.atStartOfDay();
        LocalDateTime toDateTime   = (to == null)   ? null : to.atTime(LocalTime.MAX);

        return activityLogService.search(eventType, email, fromDateTime, toDateTime, page, size);
    }

    @GetMapping("/event-types")
    public List<ActivityEventType> getEventTypes() {
        return List.of(ActivityEventType.values());
    }
}