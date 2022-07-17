# JackHenryCodingChallenge
Scala Application for a Weather Service

## Project Submission - Weather Service 	 	 	   	

Write an http server that uses the Open Weather API that exposes an endpoint
 that takes in lat/long coordinates. 
 This endpoint should return what the weather condition is outside in that area (snow, rain, etc), whether it’s hot, cold, or moderate outside (use your own discretion on what temperature equates to each type), and whether there are any weather alerts going on in that area, with what is going on if there is currently an active alert. The API can be found here: https://openweathermap.org/api. The one-call api returns all of the data while the other apis are piece-mealed sections. You may also find the 
 https://openweathermap.org/faq useful.
 

## Installation And Quick Guide
- Install SBT
- Clone this repository
- Follow the docker-compose instruction [here](docker-compose/README.md)

Running without docker:
```sbt
// inside JackHenryCodingChallenge directory
$ sbt run
```

## Description
Created weather service using the type-level stack. 

The service currently support an API GET calls that will call
the Open Weather API, and returns the expected result output.

## API Document

### GET /weathers
#### Version Information
version : v1
#### Transport Method
- REST

##### Description
Get the weather condition based on the longitude and latitude

```
GET /{{version}}/weathers?latitude={latitude}&longitude={longitude}
```

#### Query Params
| Name | Description | Type | Possible Value |  
| --- | --- | --- | --- |
| longitude <br> **required** | longitude | String | 12.0 |
| latitude <br> **required** | latitude | Double | -90.0 |
| unit | what the response unit should be like (fahrenheit, celsius, kelvin ) | fahrenheit,celsius, kelvin | fahrenheit|

#### Responses
| Http Code | Description | Schema |
| --- | --- | --- |
| 200 | Successful Output | |
| 500 | Internal Server Error | [ServiceError](#ServiceError) |

### Output
| Name | Description | Type | Possible Value |  
| --- | --- | --- | --- |
| longitude <br> **required** | longitude | Double | 12.0 | 
| latitude <br> **required** | latitude | Double | -90.0 |
| timezone <br> **required** | timezone based on the longitude or latitude |String |  New York/New York |
| temperature <br> **required** | the temperature based on the Unit | Double | 12.0 |
| unit <br> **required** | The unit (Fahrenheit, Celsius, Kelvin ) | oneOf(Fahrenheit, Celsius, Kelvin) | Celsius |
| condition <br> **required** | The condition of the weather | String | Sunny, Cloudy | 
| feels <br> **required** | The feels (Hot, Cold, Moderate) | oneOf(Hot, Cold, Moderate) | Hot |
| alerts | The list of alerts coming from that weather | List[[Alert](#Alerts)] | |

#### Alerts
| Name | Description | Type | Possible Value |  
| --- | --- | --- | --- |
| sender_name <br> **required** | Name of the alert source. | String |  
| event <br> **required** | Alert event name | String |
| start <br> **required** |  Date and time of the start of the alert, Unix, UTC | Instant | 2022-07-17T18:46:00Z |
| end  <br> **required** | Date and time of the end of the alert, Unix, UTC | Instant | 2022-07-19T00:00:00Z | 
| description | description of the alert | String |  


### ServiceError
| Name | Type |
| --- | --- |
| message <br> **required** | String |
| code <br> **required** | Int |


###### Sample JSON Response:
request:
```
curl --location --request GET 'http://localhost:8080/v1/weathers?latitude=33.44&longitude=-94.04'
```
response: 
```json
{
    "longitude": -94.04,
    "latitude": 33.44,
    "timezone": "America/Chicago",
    "temperature": 68.526,
    "unit": "Fahrenheit",
    "condition": "Clouds",
    "feels": "Hot",
    "alerts": [
        {
            "sender_name": "NWS Shreveport (Shreveport)",
            "event": "Heat Advisory",
            "start": "2022-07-17T18:46:00Z",
            "end": "2022-07-19T00:00:00Z",
            "description": "...HEAT ADVISORY NOW IN EFFECT UNTIL 7 PM CDT MONDAY...\n* WHAT...Heat index values from 105 to 110 degrees are expected.\n* WHERE...Portions of north central and northwest Louisiana,\nsoutheast Oklahoma, south central and southwest Arkansas and\nnortheast Texas.\n* WHEN...Until 7 PM CDT Monday.\n* IMPACTS...Hot temperatures and high humidity may cause heat\nillnesses to occur."
        }
    ]
}
```
## Architecture
- The architecture are divided the Http Layer, Business Service Layer, Data Layer (if there is any).

- The Http Layer consist of the WeatherRoutes (which has the routes to the Weather Service).

- The Business Service Layer consist fo the WeatherService

- The Model consist of the OpenWeatherAPI model WeatherClientResponse, and the Service Response model, WeatherCondition.

## Prepackage and build image
Use native-docker plugin to build docker image.

To build docker image:
```sbt
// Inside JackHenryCodingChallenge directory
$ sbt docker:publishLocal
```

## ToDO
- Unit Testing
- Integration Testing
- Optimization with Cache
