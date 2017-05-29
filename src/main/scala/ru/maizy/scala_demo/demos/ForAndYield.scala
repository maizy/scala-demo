package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock


class ForAndYield extends Demo {
  override val name: String = "for_yeild"
  override val description: String = "for, list contructions with for + yield"

  def run(settings: Settings): Unit = {
    val sampleList = List("Ru", "En", "De")
    val sampleListRu = List("Рус", "Анг", "Нем")

    demoBlock("1. for + yeild") {
      println(
        for ((e, r) <- sampleList zip sampleListRu)
          yield e +": "+ r)
    }

  }
}
