package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */
import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock


class DecoratorsDemo extends Demo {
  val name: String = "decorators"

  def run(settings: Settings): Unit = {
    class Prefixer(val prefix: String)

    object TestObj {
      def simple(s: String): String = s"s: $s"
      def describe(a: String, b: Int): String = s"a: $a, b: $b"
    }

    class SimpleDecorator[I](
        val postfix: String,
        val func: (I) => String,
        // to demonstrate other internal usage of params
        // ex: computer cache key
        val keyFunc: (I) => String
    ) {
      def apply(params: I): String = {
        val key = keyFunc(params)
        s"[${func(params)}]=$postfix (call with key=$key)"
      }
    }

    demoBlock("simple decorator for arity=1") {

      val func: (String => String) = TestObj.simple // just to demonstrate type

      val simpleDecorated = new SimpleDecorator[String](
        postfix = "bar",
        func = TestObj.simple,
        keyFunc = s => {
          val hash = s.hashCode.toString
          s.hashCode.toString.substring(0, scala.math.min(10, hash.length))
        }
      )

      println(s"Before decorate: ${TestObj.simple("foo")}")
      println(s"After decorate: ${simpleDecorated("foo")}")

      val secondLevelDecorator = new SimpleDecorator[String](
        postfix = "second",
        func = simpleDecorated.apply,
        keyFunc = s => {
          val hash = s.hashCode.toString
          s.hashCode.toString.substring(scala.math.max(hash.length - 5, 0), hash.length)
        }
      )
      println(s"After second decorator: ${secondLevelDecorator("foo")}")

    }

    demoBlock("simple decorator with arity>1") {
      // types:
      val func: (String, Int) => String = TestObj.describe

      val funcTupled: (Tuple2[String, Int] => String) = func.tupled
      // or with syntax shugar, lisp-style :)
      val funcTupledShugar: (((String, Int)) => String) = func.tupled

      val multiArityDecorator = new SimpleDecorator[(String, Int)](
        func = (TestObj.describe _).tupled, // use _ to convert method to anon func
        postfix = "baz",
        keyFunc = {
          case (a, b) => s"$a-$b"
        }
      )

      println(s"Before decorate: ${TestObj.describe("bebe", 10)}")
      println(s"After decorate: ${multiArityDecorator(("bebe", 10))}")
    }

    object TestObjWithImplicit {
      def simpleWithPrefixer(s: String)(implicit p: Prefixer): String = s"[${p.prefix}] s: $s"
      def describeWithPrefixer(a: String, b: String)(implicit p: Prefixer): String = s"[${p.prefix}] a: $a, b: $b"
      def curriedDescribeWithPrefixer(a: String, b: String)(p: Prefixer): String = s"[${p.prefix}] a: $a, b: $b"
    }

    demoBlock("decorate method with implicits") {
      implicit val prefixer = new Prefixer("implicit prefix")
      val otherPrefixer = new Prefixer("other prefix")

      // direct call
      println(TestObjWithImplicit.describeWithPrefixer("rabbit", "jump"))

      // direct call with explicit param
      println(TestObjWithImplicit.describeWithPrefixer("rabbit", "jump")(otherPrefixer))

      val func: (String, String) => String = TestObjWithImplicit.describeWithPrefixer
      // why not `val func: (String, String) => Prefixer => String` ?
      println(func("bird", "fly"))
      // func("bird", "fly")(otherPrefixer) //how?

      // there isn't solution for that, because implicit params may exist only
      // for methods

      val curriedFunc: (String, String) => Prefixer => String =
        TestObjWithImplicit.curriedDescribeWithPrefixer
    }
  }
}
