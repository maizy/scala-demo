package ru.maizy.scala_demo
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo.demos._

object Launcher extends App {
  println("Scala-demo v.0.1\n") // TODO: use const for version

  val settings = new Settings()

  val demos = List(
    new PrintSettings,
    new PrintDate,
    new SeqProcessing,
    new ArrayDemo,
    new ListDemo,
    new ListMethods,
    new TuplesDemo,
    new MapDemo,
    new SetImmuttableDemo,
    new SetMuttableDemo,
    new ForAndYield,
    new StreamsDemo,
    new PartialFunctionsDemo,
    new BasicFutureDemo,
    new ErrorsInFutureDemo,
    new FutureChainsDemo,
    new RegexpDemo,
    new DecoratorsDemo,
    new StackableTraitDemo,
    new PubSubDemo,
    new StreamsWithSkips
  )

  val collection = new DemoCollection(
    demos,
    (1 to demos.length + 2).toIterator
  )

  if (args.length > 0) {
    args(0) match {
      case n: String if n.matches("[0-9]+") =>
        println(s"Run demo #$n")
        collection.run(n.toInt, settings)
      case name: String => demos.zipWithIndex.find{ case (d: Demo, i: Int) => d.name == name }
        match {
          case Some((_, num)) => collection.run(num + 1, settings)
          case None => println(s"Unable to find demo with name = $name")
        }
    }
  } else {
    print(demosLister(collection))
    print("Run demo #")
    collection.run(scala.io.StdIn.readInt(), settings)
  }
}
