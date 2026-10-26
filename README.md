# Gateway

## Ansvar

Gateway är en ingångspunkt mellan frontend och mikrotjänsterna i Blackmarqet. Den tar emot all anrop från frontend och
vidarbefordrar dem till rätt mikrotjänst.

## Tekniker

- Java 21
- Spring boot v4
- Spring security
- Maven

## Databas

Gateway använder ingen databas.

## Miljövariabler

Miljövariabler måste vara satta för att tjänsten ska fungera korrekt.

ALLOWED_ORIGINS - Tillåtna origins för cors, kommaseparera om flera.
SERVER_PORT - 4 siffrigt tal som applikationen ska köras på.
AUTH_SERVICE_URL - URL till auth service.
PRODUCT_SERVICE_URL - URL till product service.
ORDER_SERVICE_URL - URL till order service.

Mappning i `application.properties`:

```
app.cors.allowed-origins=${ALLOWED_ORIGINS}
server.port=${SERVER_PORT}
app.auth-service-url=${AUTH_SERVICE_URL}
app.product-service-url=${PRODUCT_SERVICE_URL}
app.order-service-url=${ORDER_SERVICE_URL}
```

## Starta applikationen lokalt

Kräver Java 21 och Maven.

1. Klona projektet
2. Sätt miljövariablerna
3. Starta applikationen:

**MacOS/Linux**

```
./mvnw spring-boot:run
```

**Windows:**

```
mvnw.cmd spring-boot:run
```

Tjänsten är tillgänglig på `http://localhost:<Din valda port>`.

## Relaterade repositories

1. Product Service - https://github.com/MartinGruter/product-service-projekt2
2. Auth Service - https://github.com/MartinGruter/auth-server-projekt2
3. Order Service - https://github.com/MartinGruter/order-service-projekt2
4. Email Service - https://github.com/MartinGruter/email-service-projekt2
5. Frontend - https://github.com/MartinGruter/micro-services