package ru.maizy.scala_demo

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013-2017
 * See LICENSE.txt for details.
 */

import scala.collection.immutable.ListMap

class DemoCollection {

  private val numerator = 1 to Int.MaxValue
  private var _numeratedDemos: Map[Int, Demo] = ListMap.empty

  def length: Int = _numeratedDemos.size

  def numeratedDemos: Map[Int, Demo] = _numeratedDemos

  def this(demos: Demo*) = {
    this()
    demos.zip(numerator).foreach {
      case (demo, index) => _numeratedDemos += index -> demo
    }
  }

  def run(index: Int, settings: Settings): Either[String, Unit] = {
    if (!(_numeratedDemos isDefinedAt index)) {
      Left(s"Demo with index $index doesn't exist")
    } else {
      _numeratedDemos(index).run(settings)
      Right(())
    }
  }

  def run(name: String, settings: Settings): Either[String, Unit] = {
    val matched = _numeratedDemos.filter {
      case (i: Int, d: Demo) => d.name.contains(name) || d.getClass.getCanonicalName.contains(name)
    }

    if (matched.isEmpty) {
      Left(s"Unable to find demo with name = $name")
    } else if (matched.size > 1) {
      Left(s"Ambiguous demos with name $name")
    } else {
      run(matched.head._1, settings)
    }
  }
}


object DemoCollection {

  def demosLister(collection: DemoCollection): String = {
    if (collection.length == 0) {
      "Collection is empty"
    } else {
      val prefix = " "
      val descriptionPrefix = " " * 6

      def describeDemo(num :Int, demo: Demo) : String = {
        var res = s"$prefix $num. ${demo.name}"
        if (demo.description.length > 0) {
          res += s"\n$descriptionPrefix(${demo.description})"
        }
        res
      }

      collection.numeratedDemos.map { case (index, demo) =>
        describeDemo(index, demo)
      }.mkString("\n") + "\n"
    }
  }
}
