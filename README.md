# OpenGive - Denver 2017 [![Build Status](https://travis-ci.org/Credera/OpenGive-Denver-2017.svg?branch=master)](https://travis-ci.org/Credera/OpenGive-Denver-2017)

## Development

Before you can build and run this project, you must install and configure the following dependencies on your machine:

1. [Java][]: The backend is built with Java 8, utilizing Spring Boot
2. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.
3. [Yarn][]: We use Yarn to manage Node dependencies.
   Depending on your system, you can install Yarn either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

    yarn install

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.  The gradle wrapper will automatically download and install [Gradle]
on your system if required.

    ./gradlew
    yarn start

We use yarn scripts and [Webpack][] as our front-end build system.  [Yarn][] is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `yarn update` and `yarn install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `yarn help update`.

The `yarn run` command will list all of the scripts available to run for this project.

The database schema is managed by [Liquibase].  During development, the project is automatically configured to utilize
an H2 on-disk local database.  This will seamlessly switch to a managed MySQL instance when deploying in the cloud.

### Branching Strategy

We are using a simplified "Git Flow" workflow for branching and merging for this project.  At a high level, we want to
keep this as simple as possible so that everyone can be focused on adding features groing forward.

During the Hackathon event:
* Please feel free to work in teams – pair programming is encouraged!
* Each feature should be developed in its own **feature** branch (for example, **feature/portfolio-management**), which has been based of the **develop** branch

```
git fetch --all
git checkout develop
git checkout -b feature/portfolio-management
```

* As you or your group completes a feature, merge the latest code from the **develop** branch into your feature branch
```
git fetch --all
git merge origin/develop
```
* Create a pull request to merge your group’s branch into develop: https://help.github.com/articles/creating-a-pull-request/
  * Be sure that you set the base branch to **develop**
* Someone with merge privileges within the repo will be available throughout the event
  * Currently: Kevin McDonald, Ari Cooperman, Kash Kummings and Jeremy Leisy have privileges to merge into the develop branch
* Once the event is over and we’re ready to deliver, we’ll work to merge **develop** into **master** and release the
project to our non-profit friends


### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

    yarn add --exact leaflet

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

    yarn add --dev --exact @types/leaflet

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:

Edit [src/main/webapp/app/vendor.ts](src/main/webapp/app/vendor.ts) file:
~~~
import 'leaflet/dist/leaflet.js';
~~~

Edit [src/main/webapp/content/css/vendor.css](src/main/webapp/content/css/vendor.css) file:
~~~
@import '~leaflet/dist/leaflet.css';
~~~

Note: there are still few other things remaining to do for Leaflet that we won't detail here.

### Using angular-cli

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

    ng generate component my-component

will generate few files:

    create src/main/webapp/app/my-component/my-component.component.html
    create src/main/webapp/app/my-component/my-component.component.ts
    update src/main/webapp/app/app.module.ts

## Building for production

To optimize the opengive application for production, run:

    ./gradlew -Pprod clean bootRepackage

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar build/libs/*.war

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

## Testing

To launch your application's tests, run:

    ./gradlew test

### Client tests

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

    yarn test

UI end-to-end tests are powered by [Protractor][], which is built on top of WebDriverJS. They're located in [src/test/javascript/e2e](src/test/javascript/e2e)
and can be run by starting Spring Boot in one terminal (`./gradlew bootRun`) and running the tests (`yarn run e2e`) in a second one.
### Other tests

Performance tests are run by [Gatling][] and written in Scala. They're located in [src/test/gatling](src/test/gatling) and can be run with:

    ./gradlew gatlingRun

<!---
## Using Docker to simplify development (optional)

You can use Docker to improve the OpenGive development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.
For example, to start a mariadb database in a docker container, run:

    docker-compose -f src/main/docker/mariadb.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/mariadb.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./gradlew bootRepackage -Pprod buildDocker

Then run:

    docker-compose -f src/main/docker/app.yml up -d
--->
## Continuous Integration / Continuous Deployment

[Travis CI] has been setup to automatically download deploy changes which have been approved and merged to the **develop**
branch.  Upon a successful pull request and merge to **develop**, [Travis CI] will build and run the application on
Heroku in production mode.

The cloud-based database is a Heroku managed MySQL instance which is automatically updated thanks to [Liquibase] at boot
time.


[Gatling]: http://gatling.io/
[Node.js]: https://nodejs.org/
[Yarn]: https://yarnpkg.org/
[Webpack]: https://webpack.github.io/
[Angular CLI]: https://cli.angular.io/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/
[Leaflet]: http://leafletjs.com/
[DefinitelyTyped]: http://definitelytyped.org/
[Java]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[Gradle]: https://gradle.org/
[Travis CI]: https://travis-ci.org/
[Liquibase]: http://www.liquibase.org/
