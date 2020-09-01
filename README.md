# geld project

This project is in its early stages and by far not usable at the moment.

## Development

Useful documentation:

* [Elasticsearch Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-getting-started-maven.html)
* [Quarkus RESTEasy JSON-B](https://quarkus.io/guides/rest-json)
* [Quarkus Hibernate Validator](https://quarkus.io/guides/validation)
* [Quarkus RESTEasy Qute](https://quarkus.io/guides/qute)

## Building and running

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

### Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `geld-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/geld-1.0.0-SNAPSHOT-runner.jar`.

### Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/geld-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.