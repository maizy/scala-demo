package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo.{Settings, Demo}
import java.util.Date

class PrintDate extends Demo {
  override val name = "print_date"
  override val description = "prints current date and time"

  def run(settings: Settings): Unit = {
    println(new Date().toString)
    // TODO: is there any dateTime lib for scala, better than java std lib?
  }
}

class PrintSettings extends Demo {
  override val name = "print_settings"

  def run(settings: Settings): Unit = {
    println(s"Current settings is:\n$settings\n")
  }
}

