[![Build Status](https://travis-ci.org/timgent/open-holidays.svg?branch=master)](https://travis-ci.org/timgent/open-holidays)
## Open Holidays

Intended as a microservice for handling employee annual leave, built with Scala, Spray, and Slick.

In the early stages of development....


### Running the application with sbt
Follow these steps to get started:

1. Git-clone this repository.

        $ git clone git://github.com/spray/spray-template.git my-project

2. Change directory into your clone:

        $ cd my-project

3. Launch SBT:

        $ sbt

4. Compile everything and run all tests:

        > test

5. Start the application:

        > re-start

6. Browse to [http://localhost:8080](http://localhost:8080/)

7. Stop the application:

        > re-stop

### Building and running the application with Docker

1. Build the application jar

        > ./build.sh
        
2. Run the application

        > docker build -t open-holidays . && docker run -t -p 8080:8080 open-holidays