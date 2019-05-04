# TimEL: a Java time-serie library [![Build Status](https://travis-ci.com/aleofreddi/timel.svg?branch=master)](https://travis-ci.com/aleofreddi/timel)

TimEL is a Java library to evaluate expressions with time-series data. TimEL's aim is to provide an easy to use - yet powerful language - to model, aggregate and manipulate time-series. With TimEL you can:

  * **Mix multiple time frames** - for example you can sum daily data with hourly data, or even non-regular data like monthly data;
  * **Express** easily express recurrent quantities, like 10 units every hour;
  * **Autoscale** natively integral values (like consumptions);
  * **Stream** results without the need of having all the operands in memory;
  * **Support** integer, floating point and double expressions;
  * **Extend**: you can define your own types and functions;

TL;DR? Pick a random example in the [online console](https://timel.vleo.net/console) and try it yourself!

TimEL requires Java 8 and will run in any J2SE or J2EE container.

## Quickstart

To use TimEL you need to import the following dependecy:

```xml
<dependency>
    <groupId>net.vleo.timel</groupId>
    <artifactId>timel</artifactId>
    <version>0.9.0</version>
</dependency>
```

When the dependency is in place, use the TimEL facade class to compile and evaluate expressions:

```java
// Assemble the evaluation interval as [now, now + 1 second)
Instant now = Instant.now();
Interval interval = Interval.of(now, now.plus(1, ChronoUnit.SECONDS));
 
// Compile the expression 1 + 1.
// As we expect an Integer here, we explicitly request an IntegerType
// for the sake of type safety, but we can compile generic code as well.
Expression<Integer> expression = TimEL
        .parse("1 + 1")
        .compile(new IntegerType());
 
// Iterate through the results
TimeIterator<Integer> itor = TimEL
        .evaluate(expression, interval);
 
// Since 1+1 is constant we'll have a single sample for the whole interval
int v = itor.next().getValue();
```

That's it!

Now let's try something a bit more complex, where we provide an input time-serie variable. As TimEL is a streaming api, it will pull values out of the variable when it needs them. In this case we are going to use a TreeMapVariable, which is a Variable backed by a regular in memory TreeMap:

```java
// Create a new variable backed by a TreeMap
TreeMapVariable<Integer> variable = new TreeMapVariable<>();
TreeMap<Interval, Integer> values = variable.getTreeMap();
 
// Add some values to the variable
Instant now = Instant.now();
for(int[] i : new int[][] {
        // start, end, value
        {0, 1, 1},
        {1, 2, 2},
        // gap between 2nd and 3rd minute
        {3, 4, 4}
 }) {
    values.put(Interval.of(
            now.plus(i[0], ChronoUnit.MINUTES),
            now.plus(i[1], ChronoUnit.MINUTES)
    ), i[2]);
}
 
// Compile "a * a" without type requirement
Expression<?> expression = TimEL
        .parse("a * a")
        .withVariable("a", new IntegerType(), variable) // Declare 'a' as a int
        .compile();
 
// Evaluate the expression in the interval [now, now + 5 minutes)
TimeIterator<?> itor = TimEL
        .evaluate(expression, Interval.of(now, now.plus(5, ChronoUnit.MINUTES)));
 
// Pull the results, effectively evaluating the expression.
// This will print 3 lines, one for each interval, with the value 1, 4 and 16.
while(itor.hasNext())
    System.out.println("\t" + itor.next());
```

That's it! 

