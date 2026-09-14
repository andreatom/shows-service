package it.its.cinema.shows_service.catalog;

import feign.FeignException;
import it.its.cinema.shows_service.client.CatalogClient;
import it.its.cinema.shows_service.client.MovieJson;
import it.its.cinema.shows_service.model.CatalogProviderUnavailableException;
import it.its.cinema.shows_service.model.Movie;
import it.its.cinema.shows_service.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoteCatalogImporter {

    private final CatalogClient catalogClient;

    private final MovieRepository movieRepository;

    @Transactional
    public EsitoImport importaDalFornitore(){

        List<MovieJson> catalogoRemoto;
        try {
            catalogoRemoto = catalogClient.scaricaCatalogo();
        }catch (FeignException e){
            log.warn("Il fornitore del catalogo non ha risposto: {}", e.getMessage());
            throw new CatalogProviderUnavailableException(
                    "Il fornitore del catalogo non ha risposto. Riprova più tardi.", e
            );
        }
        if (catalogoRemoto == null || catalogoRemoto.isEmpty()) {
            log.info("Il fornitore ha risposto ma il catalogo è vuoto");
            return new EsitoImport(0, 0, 0);
        }

        Set<String> visti = movieRepository.findAll().stream()
                .map(Movie::getTitle)
                .collect(Collectors.toCollection(HashSet::new));

        List<Movie> nuovi = new ArrayList<>();
        for (MovieJson film : catalogoRemoto) {
            if (visti.add(film.title())){
                nuovi.add(new Movie(film.title(), film.durationMinutes()));
            }
        }

        int scartati = catalogoRemoto.size() - nuovi.size();

        if (nuovi.isEmpty()) {
            log.info("catalogo del fornitore già allineato: {} film, nessuno nuovo", catalogoRemoto.size());
            return new EsitoImport(0, scartati, catalogoRemoto.size());
        }

        movieRepository.saveAll(nuovi);
        log.info("importati {} film nuovi dal fornitore esterno ({} erano già in catalogo)",
                nuovi.size(), scartati);
        return new EsitoImport(nuovi.size(), scartati, catalogoRemoto.size());
    }


}
