package ru.maizy
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

package object scala_demo {
  def demoBlock(name: Option[String])(blockBody: => Unit): Unit = {
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

  def demoBlock(blockBody: => Unit): Unit = {
    demoBlock(None)(blockBody)
  }

  def demoBlock(name: String)(blockBody: => Unit): Unit = {
    demoBlock(Some(name))(blockBody)
  }
}
