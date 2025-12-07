## Diagram

![Alt text](/architecture/architecture.jpg)

## Overzicht

SpringCloudPXL is een content management systeem gebouwd met een microservices architectuur. Het stelt redacteurs in staat om posts aan te maken, te beoordelen en te publiceren, met een commentaarsysteem waarmee gebruikers gepubliceerde content kunnen bespreken.

---

## Overzicht van de Services

| Service               | Poort | Database   | Beschrijving                               |
| --------------------- | ----- | ---------- | ------------------------------------------ |
| **Config Service**    | 8888  | -          | Gecentraliseerd configuratiebeheer         |
| **Discovery Service** | 8061  | -          | Eureka server voor service registratie     |
| **Gateway Service**   | 8084  | -          | API Gateway voor het routeren van requests |
| **Post Service**      | 8081  | post_db    | Beheert posts (CRUD, status)               |
| **Review Service**    | 8082  | review_db  | Behandelt goedkeuring/afwijzing van posts  |
| **Comment Service**   | 8083  | comment_db | Beheert reacties op posts                  |
| **Messaging Service** | 8085  | -          | Ontvangt notificaties van RabbitMQ         |

---

## Infrastructuurcomponenten

### Spring Cloud Config Server

- Gecentraliseerde configuratie voor alle microservices
- Native profiel met lokale bestandsopslag
- Services halen configuratie op bij opstarten

### Eureka Discovery Service

- Service registratie en ontdekking
- Load balancing via `lb://service-name`
- Gezondheidsmonitoring

### API Gateway (Spring Cloud Gateway)

- Enkel toegangspunt voor alle requests
- Route mapping naar microservices
- WebFlux-gebaseerd (reactive)

### RabbitMQ Message Broker

- Asynchrone messaging tussen services
- Ontkoppelt producers van consumers
- Queue: `notificationQueue`

---

## Database Per Service Patroon

Elke microservice heeft zijn eigen dedicated database:

| Service         | Database   | Tabellen |
| --------------- | ---------- | -------- |
| Post Service    | post_db    | posts    |
| Review Service  | review_db  | reviews  |
| Comment Service | comment_db | comments |

Dit patroon zorgt voor:

- **Onafhankelijkheid**: Elke service beheert zijn eigen data
- **Schaalbaarheid**: Kan één database schalen zonder anderen te beïnvloeden
- **Isolatie**: Een service crash corrumpeert geen andere data

---

## Logging met LogBack

Alle services gebruiken SLF4J met LogBack voor logging:

- **Console Appender**: Real-time log output
- **File Appender**: Rolling file logs (`logs/<service-name>.log`)
- **Log Levels**: DEBUG voor applicatiecode, INFO voor root

Configuratiebestand: `logback-spring.xml` in de resources map van elke microservice.

---

## Opstartvolgorde

1. **Config Service** (8888) - Moet als eerste starten
2. **Discovery Service** (8061) - Services moeten zich registreren
3. **Messaging Service** (8085) - RabbitMQ consumer
4. **Business Services** (8081, 8082, 8083)
5. **Gateway Service** (8084) - Routeert naar business services

---

---
