package ru.maizy.scala_demo.herding_cats

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2017
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo._

class Day0Polymorphism extends Demo {
  override def run(settings: Settings): Unit = {

    demoBlock("parametric") {
      def second[A](a: List[A]): Either[String, A] = {
        if (a.length < 2) {
          Left("Ooops! no second element")
        } else {
          Right(a(1))
        }
      }

      println(second(List(1, 2)))

      case class DayOfWeek(name: String)
      println(second(List(DayOfWeek("monday"), DayOfWeek("tuesday"))))
    }

    demoBlock("subtype") {

      trait Plusable[T] {
        def plus(other: T): T
      }

      trait ToMyString[T] {
        def toMyString(value: T): String
      }

      trait Outputable[T, L] {
        def output(value: T, label: L): String
      }

      class MyInt(val value: Int) extends Plusable[MyInt] {
        override def plus(other: MyInt): MyInt =
          new MyInt(this.value + other.value)

        override def toString: String = s"MyInt[$value]"
      }

      class MyIntToMyString(border: String) extends ToMyString[MyInt] {
        override def toMyString(intContainer: MyInt): String = s"${border}myInt$border${intContainer.value}"
      }


      class MyOutput[T, S <: ToMyString[T]](toMyString: S) extends Outputable[T, String] {
        override def output(value: T, label: String): String = {
          s"${label.toUpperCase}: ${toMyString.toMyString(value)}"
        }
      }

      def plusAndOutput[T <: Plusable[T], O <: Outputable[T, String]](
          label: String,
          a: T,
          b: T,
          output: O): String =
      {
        val res = a.plus(b)
        output.output(res, label)
      }

      val myIntA = new MyInt(500)
      val myIntB = new MyInt(100000)
      val myOutput = new MyOutput[MyInt, MyIntToMyString](new MyIntToMyString("|"))
      println(plusAndOutput("total", myIntA, myIntB, myOutput))
    }

    demoBlock("ad-hoc") {
      trait CanMultiply[T] {
        def mul(a: T, b: T): T
      }

      implicit object StringMul extends CanMultiply[String] {
        override def mul(a: String, b: String): String =
          (a.toDouble * b.toDouble).toString
      }

      implicit object DoubleMul extends CanMultiply[Double] {
        override def mul(a: Double, b: Double): Double =
          a * b
      }

      implicit object DoubleMul2 extends CanMultiply[Double] {
        override def mul(a: Double, b: Double): Double =
          a * b * 2.0
      }

      def mul[A: CanMultiply](a: A, b: A): String = {
        val res = implicitly[CanMultiply[A]].mul(a, b)
        s"$a x $b = $res"
      }

      def mulWithExplicitImplicitParam[T](a: T, b: T)(implicit canMultiply: CanMultiply[T]): String = {
        val res = canMultiply.mul(a, b)
        s"$a x $b = $res"
      }

      println(s"mul(String, String) = ${mul("2.7", "3.7")}")

      // println(s"mul(Int, Int) = ${mul(4, 5)}")
      // =>
      // could not find implicit value for evidence parameter of type CanMultiply[Int]

      // println(s"mul(Double, Double) = ${mul(2.7, 3.7)}")
      // =>
      // ambiguous implicit values:
      // both object DoubleMul2 of type DoubleMul2.type
      // and object DoubleMul of type DoubleMul.type
      // match expected type CanMultiply[Double]

      println(s"mul(Double, Double)(DoubleMul) = ${mul(2.7, 3.7)(DoubleMul)}")
      println(s"mul(Double, Double)(DoubleMul2) = ${mul(2.7, 3.7)(DoubleMul2)}")

      // println(s"mulWithExplicitImplicitParam(Int, Int) = ${mulWithExplicitImplicitParam(4, 5)}")
      // =>
      // could not find implicit value for parameter canMultiply: CanMultiply[Int]

      // println(s"mulWithExplicitImplicitParam(Double, Double) = ${mulWithExplicitImplicitParam(2.7, 3.7)}")
      // =>
      // ambiguous implicit values:
      // both object DoubleMul2 of type DoubleMul2.type
      // and object DoubleMul of type DoubleMul.type
      // match expected type CanMultiply[Double]

      println("mulWithExplicitImplicitParam(Double, Double)(DoubleMul2) = " +
        mulWithExplicitImplicitParam(2.7, 3.7)(DoubleMul2))
    }
  }
}
