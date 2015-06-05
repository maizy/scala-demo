package ru.maizy.scala_demo
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo.demos._
import scala.collection.mutable.ListBuffer

object Launcher extends App {
  override def main(args: Array[String]): Unit = {
    println("Scala-demo v.0.1\n") // TODO: use const for version

    val settings = new Settings()

    val demos = new ListBuffer[Demo]

    demos += new PrintSettings
    demos += new PrintDate
    demos += new SeqProcessing
    demos += new ArrayDemo
    demos += new ListDemo
    demos += new ListMethods
    demos += new TuplesDemo
    demos += new MapDemo
    demos += new SetImmuttableDemo
    demos += new SetMuttableDemo
    demos += new ForAndYield
    demos += new StreamsDemo
    demos += new PartialFunctionsDemo
    demos += new BasicFutureDemo
    demos += new ErrorsInFutureDemo
    demos += new FutureChainsDemo
    demos += new RegexpDemo
    demos += new DecoratorsDemo

    val collection = new DemoCollection(
      demos.toList,
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
      collection.run(readInt(), settings)
    }
  }
}
