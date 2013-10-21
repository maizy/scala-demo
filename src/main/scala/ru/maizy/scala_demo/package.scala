package ru.maizy
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

package object scala_demo {
  def demosLister(collection: DemoCollection): String = {
    if (collection.length == 0) {
      "Collection is empty"
    } else {
      val prefix = " "
      val descriptionPrefix = " " * 6
      def describeDemo(num :Int, demo: Demo) : String = {
        var res = s"$prefix $num. ${demo.name}"
        if (demo.description.length > 0)
          res += s"\n$descriptionPrefix(${demo.description})"
        res
      }

      //TODO: refactor in functional way
      var report = ""
      for((i, demo) <- collection.numeratedDemos) {
        report += describeDemo(i, demo) + "\n"
      }
      report
    }
  }

  def demoBlock(name: Option[String])(blockBody: => Unit) {
    val div = "-" * 15
    if (name.isDefined) {
      println(div)
      println(name.get)
      println("-" * 5)
    } else {
      println(div)
    }

    blockBody
    println(div)
    println()
  }

  def demoBlock(blockBody: => Unit) {
    demoBlock(None)(blockBody)
  }

  def demoBlock(name: String)(blockBody: => Unit) {
    demoBlock(Some(name))(blockBody)
  }
}
