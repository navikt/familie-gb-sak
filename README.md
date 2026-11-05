# gravferd-sak

[![Bygg og deploy](https://github.com/navikt/gravferd-sak/actions/workflows/main.yaml/badge.svg?branch=main)](https://github.com/navikt/gravferd-sak/actions/workflows/main.yaml)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_gravferd-sak&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=navikt_gravferd-sak)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_gravferd-sak&metric=coverage)](https://sonarcloud.io/summary/new_code?id=navikt_gravferd-sak)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_gravferd-sak&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_gravferd-sak)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=navikt_gravferd-sak&metric=bugs)](https://sonarcloud.io/dashboard?id=navikt_gravferd-sak)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=navikt_gravferd-sak&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=navikt_gravferd-sak)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_gravferd-sak&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=navikt_gravferd-sak)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_gravferd-sak&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=navikt_gravferd-sak)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=navikt_gravferd-sak&metric=sqale_index)](https://sonarcloud.io/dashboard?id=navikt_gravferd-sak)

Backend for saksbehandling av gravferdsstønad. Saksbehandlere bruker løsningen gjennom [gravferd-sak-frontend](https://github.com/navikt/gravferd-sak-frontend).

## Miljøer

| Miljø | URL | Swagger |
|-------|-----|---------|
| dev | https://gravferd-sak.intern.dev.nav.no | [Swagger](https://gravferd-sak.intern.dev.nav.no/swagger-ui/index.html) |
| prod | https://gravferd-sak.intern.nav.no | [Swagger](https://gravferd-sak.intern.nav.no/swagger-ui/index.html) |
| lokalt | http://localhost:8091 | [Swagger](http://localhost:8091/swagger-ui/index.html) |

## Kom i gang

### Bygge og teste

```shell
mvn test
```

Integrasjonstestene bruker Testcontainers, så Docker må kjøre.

### Kjøre lokalt

Lokalt kjører appen mot en lokal database, men bruker Entra ID, Unleash og tilgangsmaskinen i dev.

Forutsetninger:

- Java 25 og Maven
- Docker eller Colima
- [Naisdevice](https://doc.nais.io/operate/naisdevice/) tilkoblet
- [nais-cli](https://doc.nais.io/operate/cli/), innlogget med `nais login`
- `jq`
- Tilgang til team gravferd i Nais

1. Start Postgres:

   ```shell
   docker run -d --name gravferd-sak-db -p 5432:5432 \
     -e POSTGRES_PASSWORD=test -e POSTGRES_DB=gravferd-sak postgres:18
   ```

2. Kjør [`DevLauncherPostgresPreprod`](src/test/lokal/kotlin/no/nav/gravferd/sak/DevLauncherPostgresPreprod.kt).

Appen starter på http://localhost:8091.

## Deploy

Push til `main` deployer til dev og prod. Det finnes også workflows for manuell deploy til dev og prod.

## Henvendelser

Spørsmål om koden eller løsningen kan stilles som issues her på GitHub.

### For Nav-ansatte

Interne henvendelser kan sendes på Slack i kanalen #team-gravferd.
