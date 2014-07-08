package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2014
 * See LICENSE.txt for details.
 */
import scala.concurrent.{future, Future}

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock

class FuturesDemo extends Demo {
  val name = "scala.concurent.Future & PartialFunction"

  def run(settings: Settings): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    class MyError(msg: String) extends Exception(msg)
    class MyError2(msg: String) extends Exception(msg)

    def universalCallbackBuilder(label: String): PartialFunction[Any, Unit] =
      {
        case i: Int if i >= 1 => println(s"$label: OK >= 1: $i")
        case e: MyError => println(s"$label: $label: My error: $e")
      }

    val universalCallback: PartialFunction[Any, Unit] = universalCallbackBuilder("")

    val myErrorMatchers: PartialFunction[Exception, Unit] = {
      case e: MyError => println(s"My error: $e")
    }

    val intWithConditions: PartialFunction[Int, Unit] = {
      case i if i >= 4 => println(s"Int: >=4 - $i")
    }

    val alwaysMatchCallback: PartialFunction[Any, Unit] = {
      case other => println(s"Ooops: $other")
    }

    demoBlock("partial functions") {

      println(s"intCallback.isDefinedAt(7)=${intWithConditions.isDefinedAt(7)}") //true
      println(s"intCallback.isDefinedAt(0)=${intWithConditions.isDefinedAt(0)}") //false
      //println(s"intCallback.isDefinedAt(string)=${intWithConditions.isDefinedAt("string")}")  //compile error: type missmatch

      //PartialFunction[Any, Unit] matching everything
      println(s"universalCallback.isDefinedAt(7)=${universalCallback.isDefinedAt(7)}") //true
      println(s"universalCallback.isDefinedAt(0)=${universalCallback.isDefinedAt(0)}") //false
      println(s"universalCallback.isDefinedAt(some string)=${universalCallback.isDefinedAt("some string")}") //false
    }


    demoBlock("simple future callbacks") {

      val simpleFuture: Future[Int] = Future {
        println("simpleFuture compute"); 5
      }

      val otherFuture: Future[Int] = Future {
        println("otherFuture compute"); 3
      }

      simpleFuture onSuccess universalCallbackBuilder("simpleFuture")
      simpleFuture onSuccess universalCallbackBuilder("simpleFuture #2")
      simpleFuture onFailure universalCallbackBuilder("simpleFuture onFailure")

      otherFuture onSuccess universalCallbackBuilder("otherFuture")
      otherFuture onFailure universalCallbackBuilder("otherFuture onFailure")

      val errorFuture: Future[Int] = Future {
        throw new MyError("bubu")
      }

      val unknownErrorFuture: Future[Int] = Future {
        throw new MyError2("bubu")
      }

      errorFuture onSuccess universalCallbackBuilder("errorFuture")
      errorFuture onFailure universalCallbackBuilder("errorFuture onFailure")
      errorFuture onFailure universalCallbackBuilder("errorFuture onFailure2")

      unknownErrorFuture onSuccess universalCallbackBuilder("unknownErrorFuture")
      //unknownErrorFuture onFailure (universalCallback orElse alwaysMatchCallback)
      unknownErrorFuture onFailure universalCallbackBuilder("unknownErrorFuture onFailure")
    }

  }
}
