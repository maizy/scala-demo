package ru.maizy.scala_demo
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import scala.collection.mutable

class DemoCollection () {

  //TODO: suport iterator trait/interface
  def length = numeratedDemosX.size
  private val numeratedDemosX: mutable.Map[Int, Demo] =
      mutable.LinkedHashMap.empty
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
