Beachfront GeoJSON -> GPKG Converter
=====

Requirements:

* Java JDK 1.8+
* Maven 3.5

Launching
----

`bf-geojson-geopkg-converter` has no mandatory environment setup in order
to run. One of its endpoints does, however, depend on an environment variable:

* `PIAZZA_URL` - The base URL the converter should use to contact the main
  Piazza endpoint. This should contain scheme, domain, and port, and not
  have a slash suffix (e.g. `https://example.com:1234`)

Commands
----

Command | Explanation
--------|-------------
`mvn clean install` | Install dependencies, build project, and run tests
`mvn package` | Build project and bundle into JAR in `target/`
`mvn clean install spring-boot:run` | Build project and run it
`mvn spring-boot:run` | Run project without building
`mvn javadoc:javadoc` | Generate Java Docs, found in `target/site/apidocs/`

Usage
----

The converter has two endpoints for converting GeoJSON to GPKG:

### `POST /convert`

This endpoint converts raw GeoJSON to GPKG.

* Input (body): `application/json`, more specifically, a GeoJSON FeatureCollection
* Input (headers): 
  * **Required:** `Content-Type: application/json`
* Output (body): `application/x-sqlite3`, more specifically, a GeoPackage archive
* Authorization: none

### `GET /convert/<piazza_file_id>?pzKey=<key>`

This endpoint converts data residing in Piazza to GPKG, and presents it in a downloadable format.

* Input (URL):
  * **Required:** an ID for a GeoJSON file or object in Piazza, as part of the path
  * **Required:** the API key to use when contacting Piazza, as the `pzKey` query parameter
* Output (body): `application/x-sqlite3`, more specifically, a GeoPackage archive
* Output (headers):
  * `Content-Disposition` contains the proper `attachment; filename=...` data for "download as file" functionality
* Authorization: none
#Testing webhook
