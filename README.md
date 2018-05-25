# integral-mecenat-integration

A docker container to generate information from Ladok3 Uppföljningsdatabas to Mecenat, a company
for student benefits and cards.

## Configuration

The application is based on [spring-boot](https://projects.spring.io/spring-boot/).
The default properties file `application.properties` is loaded from the path
`/run/secrets,/opt/data,/opt/camel`. See example of application.properties below.

The configuration can (mostly) also be specified as environment variables, where e.g.
sql.database is expressed as the environment variable SQL_DATABASE. You can use combinations
of properties and environment.

### Logging

Logging is done with spring-boot default logger, [logback](https://logback.qos.ch/), and
can be configured at runtime with properties in application.properties. Note that it is
not possible to set these with environment variables due to case mangling issues.

Properties are of standard log4j like type, with the package name and level, prefixed by
`logging.level`, e.g., `logging.level.se.kth.integral=DEBUG`. See example below.

### Example

Below are the settings required for the application to work. Additional settings can be made.

```
# application.properties
#

# Ladok3 upppföljningsdatabas

ladok3.database=
ladok3.username=
ladok3.password=

# Log configuration, examples
# logging.level.org.apache.camel=DEBUG
# logging.level.se.kth.integral=DEBUG
```

### Docker environment

The image can be started with 

```docker run \
    --env-file environment\
    -v /Users/username/some/dir:/opt/data
    kthse/integral-mecenat-integration:latest
```

using an environment file for the above settings. There is a skeleton available in environment.in.

## Development

The application is a spring-boot application and can be run from maven as `mvn spring-boot:run`

The project uses git-flow branch strategy, see
[introduction](http://nvie.com/posts/a-successful-git-branching-model/)
and the [git-flow tool](https://github.com/nvie/gitflow). Mainly all
development goes into development branch and via releases into master
which is built and pushed to docker hub continously by Jenkins.

Set the version in all components with `mvn versions:set` from project root.

### IBM DB2 driver

The IBM DB2 JDBC driver required for connecting to the database is not distributed in a public maven
repo. In order to work reasonably well during build it is instead installed manually to a local
repository within the project root. New versions of this driver can be downloaded and installed 
with maven with a command like below (adjust paths and version).

```
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file\
    -Dfile=/path/to/ibm/driver/db2jcc4.jar\
    -DgroupId=com.ibm.db2.jcc\
    -DartifactId=db2jcc4\
    -Dversion=11.1.3.3\
    -Dpackaging=jar\
    -DlocalRepositoryPath=/path/to/the/project/root/repo
```

### Building

Complete build and testing is run with maven: ```mvn clean install docker:build```

Pre built docker images are available on public docker hub as kthse/integral-mecenat-integration.

See the jenkins job at:
https://jenkins.sys.kth.se/view/Integral/job/integral-mecenat-integration/

### Release process with git flow

```
git flow release start x.y.z
mvn versions:set -DnewVersion=x.y.z
 *do whatever testing and update of RELEASENOTES.md*
 *commit changes*

git flow release finish x.y.z
```

It is possible to publish the release branch `git flow release publish x.y.z` to the 
repository if it is to be shared between developers or used in some CI environment.