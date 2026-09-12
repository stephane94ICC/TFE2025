package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.PublicActivityResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySessionStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivityResponseMapper;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivitySessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PublicActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityService activityService;
    private final ActivitySessionRepository activitySessionRepository;
    private final ActivityResponseMapper activityResponseMapper;

    public PublicActivityService(
            ActivityRepository activityRepository,
            ActivityService activityService,
            ActivitySessionRepository activitySessionRepository,
            ActivityResponseMapper activityResponseMapper
    ) {
        this.activityRepository = activityRepository;
        this.activityService = activityService;
        this.activitySessionRepository = activitySessionRepository;
        this.activityResponseMapper = activityResponseMapper;
    }

    @Transactional(readOnly = true)
    public List<PublicActivityResponseDTO> getApprovedActivities() {

        List<Activity> activities = activityRepository
                .findAllByStatusOrderByCreatedAtDesc(ActivityStatus.APPROVED);

        if (activities.isEmpty()) {
            return List.of();
        }

        List<Long> activityIds = activities.stream()
                .map(Activity::getId)
                .toList();

        Map<Long, List<String>> citiesByActivity =
                getCitiesByActivity(activityIds);

        Set<Long> availableActivityIds =
                getAvailableActivityIds(activityIds);

        return activities.stream()
                .map(activity ->
                        activityResponseMapper.toPublicDTO(
                                activity,
                                citiesByActivity.getOrDefault(
                                        activity.getId(),
                                        List.of()
                                ),
                                availableActivityIds.contains(activity.getId())
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicActivityResponseDTO getApprovedActivity(Long id) {

        Activity activity = activityService.findApprovedById(id);

        List<Long> activityIds = List.of(activity.getId());

        Map<Long, List<String>> citiesByActivity =
                getCitiesByActivity(activityIds);

        Set<Long> availableActivityIds =
                getAvailableActivityIds(activityIds);

        return activityResponseMapper.toPublicDTO(
                activity,
                citiesByActivity.getOrDefault(
                        activity.getId(),
                        List.of()
                ),
                availableActivityIds.contains(activity.getId())
        );
    }

    private Map<Long, List<String>> getCitiesByActivity(
            List<Long> activityIds
    ) {
        Map<Long, List<String>> citiesByActivity = new HashMap<>();

        List<Object[]> rows =
                activitySessionRepository
                        .findDistinctCitiesByActivityIds(activityIds);

        for (Object[] row : rows) {

            Long activityId = (Long) row[0];
            String city = (String) row[1];

            citiesByActivity
                    .computeIfAbsent(
                            activityId,
                            key -> new ArrayList<>()
                    )
                    .add(city);
        }

        return citiesByActivity;
    }

    private Set<Long> getAvailableActivityIds(
            List<Long> activityIds
    ) {
        LocalDateTime now = LocalDateTime.now();

        List<Long> availableIds =
                activitySessionRepository
                        .findActivityIdsWithAvailableSession(
                                activityIds,
                                ActivitySessionStatus.SCHEDULED,
                                List.of(
                                        ReservationStatus.PENDING,
                                        ReservationStatus.CONFIRMED
                                ),
                                now
                        );

        return new HashSet<>(availableIds);
    }
}