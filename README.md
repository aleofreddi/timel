# TimEL: Time-series Expression Language
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0) 
[![Build Status](https://travis-ci.com/aleofreddi/timel.svg?branch=master)](https://travis-ci.com/aleofreddi/timel) 
[![Test Coverage](https://codecov.io/gh/aleofreddi/timel/branch/master/graph/badge.svg)](https://codecov.io/gh/aleofreddi/timel) 
[![Javadocs](https://www.javadoc.io/badge/net.vleo.timel/timel-core.svg)](https://www.javadoc.io/doc/net.vleo.timel/timel-core)

## Why?

Monitoring, metering, IoT, pay-per-use billing: these are only few examples of applications that rely on time-series data! Often you want the final user to be 
able to manipulate and customize some results based on some time-series data - that's when TimEL comes in handy!

## What?

TimEL is a Java library to compile and evaluate TimEL expressions. TimEL expressions are written in a user-friendly language that allows time-series 
manipulation without the need of taking care about upscaling, downscaling or mixing up different time series intervals.

Let's see an expression to count the number of days:

```C
scale(                                      // (3) and then downsample for the whole interval
    scale(
        uniform(1.0),                       // (1) let's take an integral value 1.0
        every(1, "DAY_OF_YEAR", "UTC")      // (2) repeat it every day
    )
)
```

If we evaluate this expression for an interval in the same day, let's say 06:00-18:00, it'll report 0.5 - that is half day. If we evaluate it for more days it 
will count how many days are contained in the interval. The function `uniform` here returns an integral, so TimEL knows how to interpolate it properly - that 
is handled by the interpreter so the user does not need to worry no more about time frames.

**TL;DR?** Pick a random example in the [📺 online console](https://timel.vleo.net/console) and try it yourself!

## Features

With TimEL you can:

  * **Mix multiple time frames** - for example you can sum daily data with hourly data, or even non-regular data like monthly data;
  * **Express** easily recurrent quantities, like 10 units every hour;
  * **Scale** natively integral values (like consumptions) and averages;
  * **Stream** results without the need of having all the operands in memory;
  * **Support** integer, floating point and double expressions;
  * **Extend** with your own types and functions;
  * Evaluate expression **securely** - by default there is no way to access external JVM objects or methods that would expose you to a security risk.

TimEL requires Java 8 and will run in any J2SE or J2EE container.  For complete project information, see [TimEL's website](https://timel.vleo.net).

## Quickstart

To use TimEL you need to import the following dependency:

### Maven

```xml
<dependency>
    <groupId>net.vleo.timel</groupId>
    <artifactId>timel-core</artifactId>
    <version>0.9.2</version>
</dependency>
```

### Gradle

```
implementation 'net.vleo.timel:timel-core:0.9.2'
```

Now you're ready to go! Let's count how many days passed since (unix) epoch:

```java
// Compile the expression
Expression<?> expression = TimEL
        .parse("scale(scale(uniform(1.0), every(1, \"DAY_OF_YEAR\", \"UTC\")))")
        .compile();

// Evaluate and print the number of days from epoch
Interval interval = Interval.of(Instant.ofEpochMilli(0), Instant.now());
System.out.println(TimEL.evaluate(expression, interval).next());
```

For a more detailed guide refer to the [quickstart guide](https://timel.vleo.net/dokuwiki/doku.php/quickstart) on TimEL's website.

## Language

On TimEL's website you can find a list of available [types](https://timel.vleo.net/dokuwiki/doku.php/data_types) and 
[functions](https://timel.vleo.net/dokuwiki/doku.php/functions).

## Extending the language

You can extend TimEL language by adding new types, conversions as well as functions. Refer to the [extension page](http://timel.vleo.net/dokuwiki/doku.php/extensions) on the homepage.
