package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock

class SeqProcessing extends Demo {
  val name: String = "seq processing by functional code"

  def run(settings: Settings): Unit = {
    val sampleList = List("Ru", "En", "De")

    demoBlock("1. foreach") {
      sampleList.foreach(lang => println(lang.reverse))
    }

    demoBlock {
      sampleList.foreach(println)
    }
  }
}


class ArrayDemo extends Demo {

  val name = "array examples"

  def run(settings: Settings): Unit = {
    demoBlock("Array init") {
      //arrays are muttable, but fixed in size
      val stations = new Array[String](3)
      stations(0) = "Krukovo"
      stations(1) = "Malino"
      stations(2) = "Firsanovskaya"
      //not a preffer way in init Array

      def printStations() = println("Stations: " + stations.toList.mkString(", "))

      printStations()
      stations(2) = "Himki"
      printStations()
    }
  }
}
