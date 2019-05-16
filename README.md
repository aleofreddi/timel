# TimEL: Time-serie Expression Language [![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0) [![Build Status](https://travis-ci.com/aleofreddi/timel.svg?branch=master)](https://travis-ci.com/aleofreddi/timel) [![Test Coverage](https://codecov.io/gh/aleofreddi/timel/branch/master/graph/badge.svg)](https://codecov.io/gh/aleofreddi/timel)

TimEL is a Java library to parse and evaluate expressions with time-series data:

```java
// Compile the expression
Expression<?> expression = TimEL
        .parse("scale(scale(uniform(1), every(1, \"DAY_OF_YEAR\", \"UTC\")))")
        .compile();

// Evaluate and print the number of days from epoch
Interval interval = Interval.of(Instant.ofEpochMilli(0), Instant.now());
System.out.println(TimEL.evaluate(expression, interval).next());
```

TimEL's aim is to provide an easy to use - yet powerful language - to model, aggregate and manipulate time-series. With TimEL you can:

  * **Mix multiple time frames** - for example you can sum daily data with hourly data, or even non-regular data like monthly data;
  * **Express** easily express recurrent quantities, like 10 units every hour;
  * **Scale** natively integral values (like consumptions) and averages;
  * **Stream** results without the need of having all the operands in memory;
  * **Support** integer, floating point and double expressions;
  * **Extend** with your own types and functions.

**TL;DR?** Pick a random example in the [📺 online console](https://timel.vleo.net/console) and try it yourself!

TimEL requires Java 8 and will run in any J2SE or J2EE container.  For complete project information, see [TimEL's website](https://timel.vleo.net).

## Quickstart

To use TimEL you need to import the following dependency:

```xml
<dependency>
    <groupId>net.vleo.timel</groupId>
    <artifactId>timel-core</artifactId>
    <version>0.9.1</version>
</dependency>
```

And now you're ready to go! For a more detailed guide refer to the [quickstart guide](https://timel.vleo.net/dokuwiki/doku.php/quickstart) on TimEL's website.

## Language

On TimEL's website you can find a list of available [types](https://timel.vleo.net/dokuwiki/doku.php/data_types) and [functions](https://timel.vleo.net/dokuwiki/doku.php/functions).

## Extending the language

You can extend TimEL language by adding new types, conversions as well as functions. Refer to the [extension page](http://timel.vleo.net/dokuwiki/doku.php/extensions) on the homepage.
