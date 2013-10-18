package ru.maizy.scala_demo
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

trait Demo {
  def name: String
  def description: String = ""
  def run(settings: Settings) : Unit
}
