package main.scala.ru.maizy.scala_demo

import main.scala.ru.maizy.scala_demo.demos.{PrintDate, PrintSettings}
import scala.collection.mutable.ListBuffer

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */
object Launcher extends App {
  override def main(args: Array[String]): Unit = {
    println("Scala-demo v.0.1\n") // TODO: use const for version

    val settings = new Settings()

    val demos = new ListBuffer[Demo]
    demos += new PrintSettings
    demos += new PrintDate
    val collection = new DemoCollection(
      demos.toList,
      (1 to demos.length + 2).toIterator
    )

    print(demosLister(collection))
    print("Run demo #")
    collection.run(readInt(), settings)
  }
}
