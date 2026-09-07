package it.its.cinema.shows_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Show {

    private Long id;
    private Movie movie;
    private LocalDateTime startTime;
    private BigDecimal basePrice;
    private int totalSeats;
    private int availableSeats;

    public static final int PRIMA_ORA_SERALE = 20;

    public Show(Long id, Movie movie, LocalDateTime startTime, BigDecimal basePrice, int totalSeats) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie is required");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time is required");
        }
        if (basePrice == null || basePrice.signum() < 0) {
            throw new IllegalArgumentException("Base price cannot be negative");
        }
        if (totalSeats <= 0) {
            throw new IllegalArgumentException("Total seats must be positive");
        }
        this.id = id;
        this.movie = movie;
        this.startTime = startTime;
        this.basePrice = basePrice;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    public void reserveSeats(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > availableSeats) {
            throw new NotEnoughSeatsException("Not enough seats for show " + id);
        }
        availableSeats -= quantity;
    }

    public void releaseSeats(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        availableSeats = Math.min(totalSeats, availableSeats + quantity);
    }

    public boolean isEveningShow() {
        return startTime.getHour() >= PRIMA_ORA_SERALE;
    }
}
