package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock

import scala.collection.immutable
import scala.collection.mutable

class SetImmuttableDemo extends Demo {
  val name = "set_immuttable"

  def run(settings: Settings): Unit = {
    demoBlock("immutable set manipulation") {
      var s = immutable.Set("One", "Two")
      println(s"s: $s")
      s += "Three" //just a alias for s = s + "Three", it's a new Set!
      println(s"s: $s")
    }
  }
}

class SetMuttableDemo extends Demo {
  val name = "set_muttable"

  def run(settings: Settings): Unit = {
    demoBlock("") {
      //the same code, but val & mutable set
      val s = mutable.Set("One", "Two")
      println(s"s: $s")
      s += "Three"
      println(s"s: $s")
    }
  }
}
