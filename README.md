# JOI Energy

## Overview

JOI Energy is a new energy company that uses data to ensure customers are 
able to be on the best pricePlan for their energy consumption.

## API

Below is a list of API endpoints with their respective input and output.

### Store Readings

#### Endpoint

```
POST
/readings/store
```

#### Input

```json
{
    "smartMeterId": <smartMeterId>,
    "electricityReadings": [
        { "time": <timestamp>, "reading": <reading> },
        ...
    ]
}
```

`timestamp`: Unix timestamp, e.g. `1504777098`   
`reading`: kW reading of smart meter at that time, e.g. `0.0503`

### Get Stored Readings

#### Endpoint

```
GET
/readings/read/<smartMeterId>
```

`smartMeterId`: A string value, e.g. `smart-meter-0`

#### Output

```json
[
    { "time": "2017-09-07T10:37:52.362Z", "reading": 1.3524882598124337 },
    ...
]
```

### Calculate Usage Cost of Smart Meter against all Price Plans

#### Endpoint

```
GET
/pricePlans/compare-all/<smartMeterId>
```

`smartMeterId`: A string value, e.g. `smart-meter-0`

#### Output

```json
{
    "pricePlan-0": 21.78133785680731809,
    ...
}
```

## Requirements

- [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Gradle](https://gradle.org/)

## Build

```console
$ gradle build
```

## Test

```console
$ gradle test # Run unit tests
$ gradle functionalTest # Run functional tests
```

## Run

```console
$ gradle run # available at localhost:8080 by default
```
