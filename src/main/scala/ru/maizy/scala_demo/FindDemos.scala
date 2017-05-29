package ru.maizy.scala_demo

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2017
 * See LICENSE.txt for details.
 */

import java.util.regex.Pattern
import org.clapper.classutil.ClassFinder
import scala.collection.mutable

object FindDemos {
  def inPackages(packageNames: Seq[String]): Map[String, List[String]] = {

    val _buffer: mutable.Map[String, List[String]] = mutable.HashMap(
      packageNames.map(el => el -> List.empty[String]): _*
    )

    val finder = ClassFinder()
    for (klass <- finder.getClasses; pkg <- _buffer.keys) {
      if (klass.name.matches(Pattern.quote(pkg) + "\\.[a-z0-9A-Z_]+")) {
        _buffer(pkg) = klass.name :: _buffer(pkg)
      }
    }

    _buffer.mapValues {_.reverse}.toMap
  }
}
