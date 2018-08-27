Beachfront GeoJSON -> GPKG Converter
=====

## Requirements:
Before building and running the bf-geojson-geopkg-converter project, please ensure that the following components are available and/or installed, as necessary:
* [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (JDK for building/developing, otherwise JRE is fine)
* [Maven (3.5 or later)](https://maven.apache.org/install.html)
* [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
* A Piazza-provided API Key - Ask the Piazza team if you need help getting one
* Access to Nexus is required to build

Ensure that the base URL the converter should use to contact the main Piazza endpoint is set. This should contain scheme, domain, and port, and not have a slash suffix (e.g. `https://example.com:1234`)

	$ export PIAZZA_URL={Piazza_URL}

>__Note:__ In the above command, replace {Piazza_URL} with the base URL the converter should use to contact the main Piazza endpoint, (e.g. `https://example.com:1234`)

Ensure that the nexus url environment variable `ARTIFACT_STORAGE_URL` is set:

	$ export ARTIFACT_STORAGE_URL={Artifact_Storage_URL}

>__Note:__ In the above command, replace {Artifact_Storage_URL} with the nexus url


***
## Setup
Navigate to the project root directory where the repository will live, and clone the git repository in that location:

	$ mkdir -p {PROJECT_DIR}/src/github.com/venicegeo
	$ cd {PROJECT_DIR}/src/github.com/venicegeo
    $ git clone git@github.com:venicegeo/bf-geojson-geopkg-converter.git
    $ cd bf-geojson-geopkg-converter

>__Note:__ In the above commands, replace {PROJECT_DIR} with the local directory path for where the project source is to be installed.

## Launching

## Commands
----

Command | Explanation
--------|-------------
`mvn clean install` | Install dependencies, build project, and run tests
`mvn package` | Build project and bundle into JAR in `target/`
`mvn clean install spring-boot:run` | Build project and run it
`mvn spring-boot:run` | Run project without building
`mvn javadoc:javadoc` | Generate Java Docs, found in `target/site/apidocs/`

## Usage
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
