package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock

import scala.util.matching.Regex

class RegexpDemo extends Demo {
  val name: String = "regexps"

  def run(settings: Settings) {
    //rich string method
    val supapupaRegexp: Regex = """(s|p)uper""".r

    demoBlock("Simple regexp, findFirstIn") {

      val matchString: Option[String] = supapupaRegexp findFirstIn "super"
      println(matchString)
      val processedMatch: Option[String] = (supapupaRegexp findFirstIn "puper") map {
        v => v + v
      }
      println(processedMatch)
    }

    //constructor form, addition group names may be provided
    val otherRegexp = new Regex("""([no]+)\s+(\d+)""", "label", "value")

    demoBlock("findFirstMatchIn") {
      val maybeRes: Option[Regex.Match] = otherRegexp findFirstMatchIn "nooo 77"
      for (res <- maybeRes) {
        println("all match: "+ res.group(0))
        println("label: "+ res.group(1)) // by index
        println("value: "+ res.group("value")) //by code
      }
    }

    demoBlock("findAllIn") {
      //use as simple Iterator[String]
      val allRes: Iterator[String] = supapupaRegexp findAllIn "super puper blabla"
      allRes foreach println

      //use as specialiterator
      val customIter: Regex.MatchIterator = otherRegexp findAllIn "n 6, nnnoo 77, ooon 55"
      val matchIter: Iterator[Regex.Match] = customIter.matchData
      matchIter foreach {
        r => {
          println(s"groupNames: ${r.groupNames}")
          println(s"groupCount: ${r.groupCount}")
          println(s"subgroups: ${r.subgroups}")
          println(s"group(0): ${r.group(0)}")
          println(s"group(1): ${r.group(1)}")
          println(s"group(2): ${r.group(2)}")
          println(s"group(label): ${r.group("label")}")
          println(s"group(value): ${r.group("value")}")
          try {
            println(r.group(777))
          } catch {
            case e: ArrayIndexOutOfBoundsException => println(e)
          }

          try {
            println(r.group("wtf"))
          } catch {
            case e: NoSuchElementException => println(e)
          }
          println()
        }
      }
    }

    demoBlock("full match patterns") {
      val fullMatch = """^(a|b)\d+$""".r
      println(fullMatch findFirstIn "a334")
      println(fullMatch findFirstIn "a334b66")
    }

    val withDashRegexp = """(\d+)-(\d+)""".r
    val withDotRegexp = """(\d+).(\d+).(\d{1})""".r

    demoBlock("Pattern match style") {
      for (s <- List("5-8", "4.644.8")) {
        s match {
          case withDashRegexp(first, secound) => println(s"with dash = f: $first, s: $secound")
          case withDotRegexp(first, secound, third) => println(s"with dot = f: $first, s: $secound, t: $third")
        }
      }

      try {
        "5.4" match {
          case withDashRegexp(first, secound) => println("??")
        }
      } catch {
        case e: MatchError => println(e)
      }
    }
  }
}
