# BpaLab Fake Factory

This app was created with Bootify.io - tips on working with the code [can be found here](https://bootify.io/next-steps/).

# what it does
* puts editable mqtt messages to broker

* to schedule set clock in Edit > 0

* to unschedule set clock in Edit = 0 or delete topic

## Development

Update your local database AND mqtt broker connection in `config/application.yml`.

## Build

The application can be built using the following command:

```
gradlew clean build
```

Start your application with the following command :

```
java ./build/libs/BpaLabFakeFactory.jar
```

After starting the application it is accessible under `localhost:9090`.


## Further readings

* [Gradle user manual](https://docs.gradle.org/)  
* [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)  
* [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/reference/jpa.html)
* [Thymeleaf docs](https://www.thymeleaf.org/documentation.html)  
* [Bootstrap docs](https://getbootstrap.com/docs/5.3/getting-started/introduction/)  
* [Learn Spring Boot with Thymeleaf](https://www.wimdeblauwe.com/books/taming-thymeleaf/)  
