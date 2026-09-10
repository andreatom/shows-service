package it.its.cinema.shows_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
public class Show {

    public static final int PRIMA_ORA_SERALE = 20;

    @Version
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "base_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;



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
