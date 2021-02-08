# Probate Submit Service 
Microservice to handle submissions to probate registries

## Getting Started
### Prerequisites
- Java 11
- Gradle
- Docker

### Running the application
#### Building and Running the Submit Service
Install dependencies and build the service by executing the following command:  
```
$ ./gradlew clean build
```

Once the build has completed, you will find the new *.jar* in `build/libs`. You can run the *.jar* with the following command:  
```
$ java -jar build/libs/submit-service-0.0.1.jar
```

## Developing

### Local development environment

```
# rebuild every time you make changes
./gradlew assemble

# first time only
npx @hmcts/probate-dev-env --create

# start the dev env
npx @hmcts/probate-dev-env
```

### Unit tests

To run all unit tests please execute the following command:

```bash
$ ./gradlew test
```

### Coding style tests

To run all checks (including unit tests) please execute the following command:

```bash
$ ./gradlew check
```

## Versioning

We use [SemVer](http://semver.org/) for versioning.
For the versions available, see the tags on this repository.

## Troubleshooting

### IDE Settings

#### Project Lombok Plugin
When building the project in your IDE (eclipse or IntelliJ), Lombok plugin will be required to compile. 

For IntelliJ IDEA, please add the Lombok IntelliJ plugin:
* Go to `File > Settings > Plugins`
* Click on `Browse repositories...`
* Search for `Lombok Plugin`
* Click on `Install plugin`
* Restart IntelliJ IDEA

Plugin setup for other IDE's are available on [https://projectlombok.org/setup/overview]

#### JsonMappingException when running tests in your IDE
Add the `-parameters` setting to your compiler arguments in your IDE (Make sure you recompile your code after).  
This is because we use a feature of jackson for automatically deserialising based on the constructor.  
For more info see: https://github.com/FasterXML/jackson-modules-java8/blob/a0d102fa0aea5c2fc327250868e1c1f6d523856d/parameter-names/README.md

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.
