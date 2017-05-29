package ru.maizy.scala_demo
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo.demos._

object Launcher extends App {
  println("Scala-demo v.0.1\n") // TODO: use const for version

  val demosDynamic = FindDemos.inPackages(
    Seq(
      "ru.maizy.scala_demo.demos",
      "ru.maizy.scala_demo.demos.herding_cats"
    )
  )
  println(demosDynamic)

  val settings = new Settings()

  val collection = new DemoCollection(
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

  def runDemo(spec: String): Unit = {
    val res = spec match {
      case n: String if n.matches("[0-9]+") =>
        println(s"Run demo #$n")
        collection.run(n.toInt, settings)
      case name: String =>
        collection.run(name, settings)
    }

    res match {
      case Left(error) => Console.err.println(error)
      case _ =>
    }
  }

  if (args.length > 0) {
    runDemo(args(0))
  } else {
    print(DemoCollection.demosLister(collection))
    runDemo(scala.io.StdIn.readLine(text = "Run demo [# or name]: "))
  }
}
