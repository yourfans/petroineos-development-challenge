# Petroineos Development Challenge
## Overview
Using the Java programming language, the system is built on the Spring Boot 2.7.14 framework and utilizes the Quartz 2.3.2 scheduling system for task scheduling.

To ensure that no task execution is missed, a thread pool mechanism is utilized. It should be noted that Quartz also uses new threads by default for task execution. In cases where a task takes longer to execute and conflicts with subsequent scheduling, Quartz supports concurrent execution. However, the issue lies in the fact that Quartz's thread pool limits the number of threads, and in production environments, there are often a large number of scheduled tasks that need to be executed. Relying solely on Quartz's concurrency mechanism can easily lead to task blockage. Therefore, an independent and cacheable thread pool can effectively avoiding such situations.

## How Can I Use It?
Download the project from GitHub.

Install the execution environment
* [Java 8](https://www.oracle.com/java/technologies/javase/javase8u211-later-archive-downloads.html)
* [Maven 3.0+](https://maven.apache.org/download.cgi)


### Method 1
Navigate to the "releases" directory and execute the following command to start the service.

`java -jar petroineos-development-challenge-1.0-SNAPSHOT.jar`

The CSV file will be generated in the "powertrade-csvs/" directory, at the same directory hierarchy with the jar file.

Naming format: PowerPosition_YYYYMMdd_HHmm.csv, for example: PowerPosition_20240409_1205.csv. 

The timestamp in the file name is converted to London time.

The default interval for task execution is 1 minute.

The log file is located in the same directory as the jar file: logs/pt-dev-challenge.

To modify the configuration, open the jar file using a decompression software and navigate to the BOOT-INF/classes directory. Open the application-prod.properties file.

You can also modify the log file location by opening the logback.xml file. Find line 5 and change the value attribute to the desired directory.

#### Explanation of relevant configurations

##### timeZone
Specifies the time zone to which all time-related operations in the program should be converted. It is currently configured as British London time.
##### csvFileLocation
Specifies the directory where the generated CSV files will be stored. It is currently configured as the /tmp directory.
#### jobStartDelay
Specifies the delay in milliseconds before starting the task execution after the service is launched. It is currently configured as 1000 milliseconds, which is 1 second.
#### jobInterval
Specifies the interval between task executions in milliseconds. It is currently configured as 60000 milliseconds, which is 1 minute.

### Method 2
Packaging and running the program on your own.

#### Modifying Configuration Parameters
Before packaging the program, you can modify the relevant configuration information. 

Go to the configuration file directory

`cd petroineos-development-challenge/resources`

Open the application-prod.properties file with an editor and configure the parameters as explained in Method 1.

#### Packaging and Running
Navigate to the project root directory.

`cd petroineos-development-challenge/`

Execute the packaging command.
`mvn clean package -Pprod`

Once the execution is complete, a jar file named petroineos-development-challenge-1.0-SNAPSHOT.jar will be generated in the petroineos-development-challenge/target directory.

Follow the subsequent steps as mentioned in Method 1.

### Note
**If you are testing in a local development environment using IntelliJ IDEA, it defaults to using the "-Pdev" parameter. In this case, you need to modify the corresponding parameters in the "application-dev.properties" file.**

## logs
The log files are by default located in the "logs/pt-dev-challenge/" directory, which is in the same directory as the jar package.

As mentioned earlier, this directory can be modified by changing the value of the "value" attribute on line 5 of the "logback.xml" file.

Here are some key log messages that may appear.

> When a task starts, logs look like:
>> Query and aggregate power trade begin, thread: pool-1-thread-1-64

> When a task is completed,logs look like:
>> Query and aggregate power trade, time cost: 8ms, thread: pool-1-thread-1-64

> When a task encounters an exception, logs look like: 
>> Query and aggregate power trade failed, time cost: 8ms, thread: pool-1-thread-1-64

## Code Review
### Project Structure
> model
>> Data entities, where all transaction-related entities are defined.

> service
>> Provides modular interfaces for upper layers to use. The two mock interfaces for fetching transaction data are defined here.

> adapters
>> Aggregates services and performs complex business logic processing. The logic for asynchronously aggregating transaction data using a thread pool is implemented here. 

> quartz
>> Components related to scheduled tasks, including job, trigger, and scheduler definitions.

> utils
>> Utility classes that are used in the project, such as JSON processing, time format conversion, etc.

### Core Code Explain
> PowerTradeAggregationAdapter.java
>> A cached thread pool defined, named "executorService".
>> When a task is triggered, it is submitted to the "executorService" for execution. The "executorService" retrieves an available thread from the thread pool to execute the task. If there are no available threads at the moment, a new thread will be created. The threads have a **maximum lifetime of 60 seconds**. If a thread is not used for more than 60 seconds, it will be removed from the thread pool.

>> After the task is submitted to the thread pool, it asynchronously returns a Future object. If needed, this object can be used to track the execution result of the task. However, it's important to note that using the Future object may also cause the main thread to block. A better approach is to use a messaging mechanism to notify relevant modules of the execution result within the thread executing the task. This not only decouples the system components but also leads to a more robust and high-performance system.

> PowerServiceImpl.java
>> Two interfaces from the "PowerService.dll" have been mocked, and the related data is stored in the "resources/mock/trades_data.json" file. When the service is started, the data from the JSON file will be loaded into memory. Currently, data for the period from April 8th to April 30th is prepared.

> SchedulerConfig.java
>> In the "SchedulerConfig" class, the configuration for the Quartz scheduling system is defined. It includes the basic configuration parameters for the Quartz framework. The specific parameter values are stored in the "**resources/config/quartz.properties**" file.

> QuartzConfig.java 
>> It defines the **trigger** and the **job**.

> QuartzStartupListener.java
>> After the service is started, it will utilize the previously defined scheduler to schedule specific "**jobs**" using "**triggers**". 

>> Triggers are responsible for defining the scheduling details, such as the frequency, timing, or conditions for executing the jobs. The scheduler will then execute the jobs based on the configured triggers.

> PetroineosDevelopmentChallengeApplication.java
>> The entry program of the service.



