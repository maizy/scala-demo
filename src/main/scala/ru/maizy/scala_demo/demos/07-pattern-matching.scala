package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock


class PatternMatchingWithCaseClasses extends Demo {
  val name: String = "pattern_matching"
  override val description: String = "Pattern matching + case classes"

  private abstract class ParsedField(typeCode: String, cValue: String, subfield: Option[ParsedField] = None)
  {
    val value = cValue
    override def toString = {s"<$typeCode: value=$value, subfield=$subfield>"}
  }
  private case class Name (name: String, posibleLastName: Option[ParsedField] = None)
    extends ParsedField("name", name, posibleLastName)
  private case class LastName (lastName: String, posibleFirstName: Option[ParsedField] = None)
    extends ParsedField("last_name", lastName)
  private case class Years (years: Int) extends ParsedField("years", years.toString)

  def run(settings: Settings): Unit = {

    ////любое значение + любое значение, где v1=v2 => просто значение (strip duplicates)
    //any(any1(any2 -> if any1.v == any2.v - strip internal dublicates
    //идея - что это рез-ты парсинга и там есть field + posibleLinkedField
    //закрыть вариант field(pos(pos...))
    //но оставить вариант field(pos1(pos2(None)) если pos1.v == pos2.v и pos1.class == pos2.class
    //сделать варианты имя + возможно фамилия
    //фамилия + возможно имя
    //год + имя или фамилия => ошибка
    //любое значение + любое значение, где v1=v2 => просто значение (strip duplicates)
    //сделать тесты
    //сделать вариант с if/else
    //сравнить скорость

    def fullNameMatcher(field: ParsedField) = field match {
      case Name(v, _) => s"$v [:lastname:]"
      case LastName(v, Some(Name(v2, None))) => s"[~ $v2] $v"
      //case LastName(v, Some(p: Name)) => s"[~ ${p.value}] $v"
      case LastName(v, _) => "[:firstname:] $v"
      case _ => "[?]"
    }

//    val variants = List(
//      new Name("Ivan"),
//      new LastName("Ivanov"),
//      new LastName("Ivanov2", Some(Name("Petr")))
//    )
//    demoBlock("1. pattern match on case class") {
//      variants.
//    }
    //FIXME realize

  }
}

//TODO: other pattern types
// + Option
// + sealed
// + @ensure
// + pattern guards
// + type matching
