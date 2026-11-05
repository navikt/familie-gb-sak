# AGENTS.md

gravferd-sak: saksbehandling av gravferdsstønad. Kotlin, Spring Boot 4, Java 25, Maven, Postgres, Nais (GCP).

## Kommandoer

Kjør alltid Maven med `-Dkotlin.compiler.daemon=false`.

- Bygg og test: `mvn verify -Dkotlin.compiler.daemon=false`
- Lint: `mvn antrun:run@ktlint -Dkotlin.compiler.daemon=false`. Fiks med `mvn antrun:run@ktlint-format -Dkotlin.compiler.daemon=false`. Kjør ktlint før commit.
- Lokal kjøring: `src/test/lokal/kotlin/no/nav/gravferd/sak/DevLauncherPostgresPreprod.kt`

## Arkitektur

Ny forretningskode skal følge heksagonal arkitektur (porter og adaptere). Pakkestiene under er relative til `src/main/kotlin/no/nav/gravferd/sak/`.

- `domene/`: forretningslogikk og domenemodell. Ren Kotlin, ingen avhengighet til Spring, database, HTTP eller Jackson.
- `domene/port/`: grensesnitt domenet eier. Inngående (use cases) og utgående (repository, klienter).
- `adapter/inn/`: REST-controllere og schedulerte jobber. Kaller kun inngående porter.
- `adapter/ut/`: database, HTTP-klienter og Unleash. Implementerer utgående porter.
- `config/`: Spring-oppsett som kobler adaptere til porter.

Avhengigheter peker alltid innover mot domenet. Domenet importerer aldri fra `adapter` eller `config`. Ved tvil: lag en port.

## Tester

Testene er delt i fire kildemapper:

- `src/test/enhetstester`: raske tester uten Spring-kontekst
- `src/test/integrasjonstester`: Spring-kontekst og database
- `src/test/felles`: fakes og testoppsett som deles
- `src/test/lokal`: launchere for lokal kjøring, ikke tester

Alle tester skal kunne kjøres parallelt. Det betyr:

- Ingen delt, muterbar tilstand mellom tester (statiske felt, singleton-fakes, felles databaserader).
- Unike testdata per test, for eksempel egen ID eller eget fødselsnummer generert per test.
- Tid via `ClockProvider`, aldri systemklokka.

Hvis en test ikke kan parallelliseres, marker den, og velg i denne rekkefølgen:

1. `@ResourceLock("<ressurs>")` når testen deler én bestemt ressurs med andre tester.
2. `@Execution(ExecutionMode.SAME_THREAD)` når testene i klassen må kjøre sekvensielt.
3. `@Isolated` bare når testen ikke tåler at noe annet kjører samtidig.

Skriv en kommentar ved annotasjonen om hvorfor testen ikke kan parallelliseres.

## Konvensjoner

- Domenenavn på norsk, tekniske Spring-begreper på engelsk.
- Bruk `familie-felles`-bibliotekene (sikkerhet, log, leader, unleash) før du skriver noe eget.
- Databasemigreringer ligger i `src/main/resources/db/migration` som `V<n>__beskrivelse.sql`. Endre aldri en migrering som er merget.

## Sikkerhet

- Tilgang styres av rolle (`Rolle`, `RolleConfig`) og populasjonstilgangskontroll (tilgangsmaskinen).
- Personopplysninger (fødselsnummer, navn, adresse, aktørId) skal aldri i vanlige logger, kun i secureLogger.

## Commits og PR

- Commit-meldinger på norsk i imperativ: «Legg til …», «Fjern …».
