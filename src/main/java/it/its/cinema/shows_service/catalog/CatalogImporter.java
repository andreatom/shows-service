package it.its.cinema.shows_service.catalog;

import it.its.cinema.shows_service.model.Movie;
import it.its.cinema.shows_service.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;


@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogImporter implements ApplicationRunner {

    /** L'ObjectMapper di Jackson 3, quello che Boot ha gia' configurato. */
    private final ObjectMapper objectMapper;

    private final MovieRepository movieRepository;

    /**
     * Il percorso e' configurabile, e il valore di default e' il file nel jar.
     *
     * "classpath:catalog.json" in sviluppo, "file:/etc/cinema/catalog.json" in
     * un container: cambia una riga di application.yaml o una variabile
     * d'ambiente, non il codice. Resource astrae le due cose, ed e' il motivo
     * per cui il tipo non e' String ne' Path.
     */
    @Value("${cinema.catalog-file:classpath:catalog.json}")
    private Resource catalogo;

    /**
     * L'IMPORT DEVE ESSERE IDEMPOTENTE: al secondo avvio non duplica niente.
     *
     * Non e' un dettaglio di eleganza. Questo metodo gira a OGNI avvio, e un
     * servizio in produzione riparte a ogni deploy, a ogni riavvio del nodo, a
     * ogni scalata di replica. Senza il controllo, il catalogo raddoppierebbe
     * ogni volta — o meglio, ci proverebbe: sbatterebbe contro il vincolo
     * uk_movies_title e impedirebbe l'avvio.
     *
     * Il controllo si fa con UNA query che carica i titoli gia' presenti, non
     * con existsByTitle dentro al ciclo: quello sarebbe un N+1 scritto a mano,
     * ed e' esattamente il problema del passo 3.3.
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        if (!catalogo.exists()) {
            // Un catalogo assente non e' un errore: in un ambiente dove i film
            // arrivano da altrove, il file semplicemente non c'e'.
            log.info("nessun catalogo da importare: {} non esiste", catalogo);
            return;
        }

        List<FilmDelCatalogo> daImportare;
        try (InputStream stream = catalogo.getInputStream()) {
            daImportare = objectMapper.readValue(stream, new TypeReference<List<FilmDelCatalogo>>() {});
        }

        Set<String> giaPresenti = movieRepository.findAll().stream()
                .map(Movie::getTitle)
                .collect(Collectors.toSet());

        List<Movie> nuovi = daImportare.stream()
                .filter(f -> !giaPresenti.contains(f.title()))
                .map(f -> new Movie(f.title(), f.durationMinutes()))
                .toList();

        if (nuovi.isEmpty()) {
            log.info("catalogo gia' allineato: {} film nel file, nessuno nuovo", daImportare.size());
            return;
        }

        movieRepository.saveAll(nuovi);
        log.info("importati {} film nuovi da {} ({} erano gia' in catalogo)",
                nuovi.size(), catalogo.getFilename(), daImportare.size() - nuovi.size());
    }

    record FilmDelCatalogo(String title, int durationMinutes) {
    }
}
