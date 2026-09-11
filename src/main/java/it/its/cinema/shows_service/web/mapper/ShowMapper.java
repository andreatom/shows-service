package it.its.cinema.shows_service.web.mapper;

import it.its.cinema.shows_service.model.Show;
import it.its.cinema.shows_service.web.dto.ShowResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShowMapper {

    public ShowResponse toResponse(Show show) {
        return new ShowResponse(
                show.getId(),
                show.getMovie().getId(),
                show.getMovie().getTitle(),
                show.getStartTime(),
                show.getBasePrice(),
                show.getTotalSeats(),
                show.getAvailableSeats(),
                show.isEveningShow(),
                show.getVersion()
        );
    }

    public List<ShowResponse> toResponse(List<Show> shows) {
        return shows.stream()
                .map(this::toResponse)
                .toList();
    }

    public Page<ShowResponse> toResponse(Page<Show> shows) {
        return shows.map(this::toResponse);
    }
}
