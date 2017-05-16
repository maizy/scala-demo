package ru.maizy.scala_demo
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

class Settings { // TODO: extends Configs
  val someSetting = "string val"
  val someOtherComplexSetting = s"Some: $someSetting"

  override def toString: String = s"Settings:\nsomeSetting:"+
    s"$someSetting\n...\n" // TODO: dump all values?

}
