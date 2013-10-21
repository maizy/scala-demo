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
      //not a preffer way to init Array, just for demonstation

      //TODO: ex of preffer way

      def printStations() = println(
        "Stations: " + stations.toList.mkString(", "))

      printStations()
      stations(2) = "Himki"
      printStations()
    }
  }
}


class ListDemo extends Demo {
  val name = "list demo"

  def run(settings: Settings): Unit = {
    demoBlock("init lists") {
      val listOne = List("jan", "feb")
      val listTwo = List("apr", "may")

      println(listOne ::: listTwo) //new list

      println("mar" :: listTwo) //prepend

      val listThree = "jun" :: "jul" :: "aug" :: Nil
      println(listThree)
    }
  }
}

class ListMethods extends Demo {
  val name = "list methods"

  def run(settings: Settings): Unit = {
    val rabbitNames = List(
      "Розмаринчик", "Горицветик", "Пируэтта",
      "Одуванчик", "Сыроежик")
    demoBlock("list filtering") {

      println("Drop 2: " + rabbitNames.drop(2)) // List(Пируэтта, Одуванчик)
      println("DropRight 3: " + rabbitNames.drop(3)) // List(Одуванчик, Сыроежик)
      println("Remove: " + rabbitNames.filterNot(_.toLowerCase.contains("о")))
    }

    demoBlock("list checking") {
      println(rabbitNames.filter(_.length % 2 == 0))
      println(rabbitNames.filterNot(_.length % 2 == 0))
      println(rabbitNames.count(_.length % 2 == 0)) // 2
      println(rabbitNames.exists(n => n.length % 2 == 0 && n(0) == 'Г')) // true
      println(rabbitNames.forall(_.length <= 10))
    }

    demoBlock("Ordering") {
      println(rabbitNames.sorted)
      println(rabbitNames.sortWith((a, b) => a.last < b.last))
    }
  }
}


class TuplesDemo extends Demo {
  val name = "tuples demo"

  def run(settings: Settings): Unit = {
    demoBlock("tuples") {
      val a = (1999, "N")
      println(a)
      println(a._1 == 1999) //true, tuples 1-based !

      val (year, letter) = a //simple pattern matching. "(" & ")" required !
      println(s"Year: $year letter: $letter")
    }
  }
}
