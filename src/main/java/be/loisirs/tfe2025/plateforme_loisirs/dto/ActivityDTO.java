package be.loisirs.tfe2025.plateforme_loisirs.dto;

import java.math.BigDecimal;

public class ActivityDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private Long partnerId;

    public ActivityDTO() {
    }

    public ActivityDTO(Long id, String title, String description, BigDecimal price, Integer durationMinutes, Long partnerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.partnerId = partnerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }
}