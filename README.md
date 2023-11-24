# Start container & backend

``docker build -t tuplaus . && docker run --rm -d -p 8080:8080 --name tuplaus tuplaus``

# Run tests

``docker exec -it tuplaus ./gradlew test``

# API documentation

Api documentation is available at `http://localhost:8080/swagger-ui/index.html`

## Payload examples
### Game start

``
{
"playerId": 1,
"bet": 10,
"pick": "SMALL"
}
``

### Cash out

``
{
"playerId": 1,
"gameId": 1
}
``

### Double

``
{
"playerId": 1,
"gameId": 1,
"pick": "SMALL"
}
``



# Database schema

Database creation sql available in generated file `create.sql`
