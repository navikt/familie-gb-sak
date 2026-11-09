# AGENTS.md

gravferd-sak: saksbehandling av gravferdsstønad. Kotlin, Spring Boot 4, Java 25, Maven, Postgres, Nais (GCP).

## Kommandoer

Kjør alltid Maven med `-Dkotlin.compiler.daemon=false`.

- Bygg og test: `mvn verify -Dkotlin.compiler.daemon=false`
- Lint: `mvn antrun:run@ktlint -Dkotlin.compiler.daemon=false`. Fiks med `mvn antrun:run@ktlint-format -Dkotlin.compiler.daemon=false`. Kjør ktlint før commit.
- Lokal kjøring: `src/test/lokal/kotlin/no/nav/gravferd/sak/DevLauncherPostgresPreprod.kt`

## Arkitektur

Appen er én Maven-modul delt i lag etter pakke. Avhengighetene peker inn mot `kjerne`. `ArkitekturTest` håndhever reglene med ArchUnit. Pakkestiene under er relative til `src/main/kotlin/no/nav/gravferd/sak/`.

| Pakke | Innhold | Får avhenge av |
|---|---|---|
| `kjerne/` | Forretningslogikk, domenemodell og grensesnitt kjernen trenger fra utsiden. Spring er tillatt. | `common` |
| `inn/` | Bare REST-controllere (`inn/rest`), Kafka-lyttere (`inn/kafka`) og schedulerte jobber (`inn/jobb`). Mapper til kjernetyper og kaller kjernen. | `kjerne`, `common` |
| `ut/` | Bare REST-klienter (`ut/rest`), Kafka-produsenter (`ut/kafka`) og database-repositories (`ut/db`). Implementerer grensesnitt fra kjernen. | `kjerne`, `common` |
| `config/` | Spring-oppsett som kobler lagene sammen | alle |
| `config/sikkerhet/` | Autentisering og rollemapping | alle |
| `config/implementasjon/` | `Default*`-implementasjoner av grensesnitt fra `kjerne` og `inn` som ikke er REST, Kafka eller database | alle |
| `common/` | Små hjelpere uten avhengigheter til resten av appen (`ClockProvider`, `secureLogger`) | ingen |

Ingen lag får avhenge av `config`. All produksjonskode må ligge i et av lagene, eller i rotpakken (`Application.kt`).

Regler:

- Kjernetjenester er konkrete klasser. `inn` kaller dem direkte, uten grensesnitt.
- Trenger kjernen noe fra utsiden (HTTP, Kafka, database, Unleash, miljø), eier kjernen grensesnittet. Implementasjonen ligger i `ut` eller `config/implementasjon`.
- `inn` og `ut` inneholder ikke tjenester. `@Service` er forbudt der.
- DTO-er og meldingsformater blir i `inn` og `ut`. Kjernen ser bare egne typer.
- Grensesnitt `inn` trenger, som `LeaderService`, ligger i `inn`.

### Navngiving

- Grensesnitt: `<Domene>Service`, for eksempel `MiljøService`.
- Implementasjon i produksjon: `Default<Domene>Service`.
- Implementasjon i test: `Fake<Domene>Service` i `src/test/felles`.
- Oppsettklasser heter `*Config`. `@ConfigurationProperties`-klasser heter `*Properties`.

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
- Æ, ø og å er lov i navn på klasser, funksjoner og variabler (`erPå`, `MiljøService`). Ikke i pakkenavn: ArchUnit gjenkjenner dem ikke, så pakken havner utenfor lagene. Skriv `miljo`, ikke `miljø`.
- Bruk `familie-felles`-bibliotekene (sikkerhet, log, leader, unleash) før du skriver noe eget.
- Databasemigreringer ligger i `src/main/resources/db/migration` som `V<n>__beskrivelse.sql`. Endre aldri en migrering som er merget.

## Sikkerhet

- Tilgang styres av rolle (`Rolle`, `RolleProperties`) og populasjonstilgangskontroll (tilgangsmaskinen).
- `Rolle` ligger i `kjerne/tilgang`. Mappingen fra Entra ID-grupper til roller ligger i `config/sikkerhet`. Endringer der gjennomgås som sikkerhetsendringer.
- Kjernen leser aldri innlogget bruker selv. `inn` sender roller og ident inn som parametre.
- Personopplysninger (fødselsnummer, navn, adresse, aktørId) skal aldri i vanlige logger, kun i secureLogger.

## Commits og PR

- Commit-meldinger på norsk i imperativ: «Legg til …», «Fjern …».
