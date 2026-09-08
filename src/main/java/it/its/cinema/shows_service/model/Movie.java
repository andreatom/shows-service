package it.its.cinema.shows_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, name = "minutes")
    private int durationMinutes;

    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public Movie(String title, int durationMinutes) {
        this.title = title;
        this.durationMinutes = durationMinutes;
    }

    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public Movie(Long id, String title, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.durationMinutes = durationMinutes;
    }

}
