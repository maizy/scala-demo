package main.scala.ru.maizy.scala_demo

import scala.collection.mutable

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */
class DemoCollection () {

  //TODO: suport iterator trait/interface
  def length = numeratedDemosX.size
  private val numeratedDemosX: mutable.Map[Int, Demo] =
      mutable.LinkedHashMap.empty[Int, Demo]
  def numeratedDemos = numeratedDemosX

  def this(demos: List[Demo], numerator: Iterator[Int]) = {
    this()
    //TODO: refactor to functional oneliner
    for(demo <- demos) {
      numeratedDemosX += numerator.next() -> demo
    }
  }

  def run(index: Int, settings: Settings) = {
    if (!(numeratedDemosX isDefinedAt index)) {
      Console.err.print(s"Task with index $index not exist")
      false
    } else {
      numeratedDemosX(index).run(settings)
      true
    }
  }
}
