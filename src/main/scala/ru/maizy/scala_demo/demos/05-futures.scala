package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2014
 * See LICENSE.txt for details.
 */
import scala.concurrent.{future, Future}

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock

class FuturesDemo extends Demo {
  val name = "scala.concurent.Future"

  def run(settings: Settings): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    demoBlock("simple future callbacks") {

      class MyError(msg: String) extends Exception(msg)
      class MyError2(msg: String) extends Exception(msg)

      val simpleFuture: Future[Int] = Future {
        println("simpleFuture compute"); 5
      }

      val otherFuture: Future[Int] = Future {
        println("otherFuture compute"); 3
      }


      val callback = PartialFunction[Any, Unit] {
        case i: Int if i >= 4 => println(s"OK - $i")
        case e: MyError => println(s"MyError - $e")
      }

      val alwaysMatchCallback = PartialFunction[Any, Unit] {
        case other => println(s"Oops something wrong: $other")
      }

      simpleFuture onSuccess callback
      simpleFuture onSuccess callback
      simpleFuture onFailure callback

      otherFuture onSuccess callback
      otherFuture onFailure callback

      val errorFuture: Future[Int] = Future {
        throw new MyError("bubu")
      }

      val unknownErrorFuture: Future[Int] = Future {
        throw new MyError2("bubu")
      }

      errorFuture onSuccess callback
      errorFuture onFailure callback
      errorFuture onFailure callback

      unknownErrorFuture onSuccess callback
      unknownErrorFuture onFailure (callback orElse alwaysMatchCallback)
    }

    demoBlock("future successful operation") {
      val f3: Future[Int] = Future {
        println("f3 compute"); 5
      }
      println(s"f3.isCompleted = ${f3.isCompleted}")

      val f4: Future[Int] = Future.successful {
        println("f4 compute"); 7
      }
      println(s"f4.isCompleted = ${f4.isCompleted}")

      val printResOfInt: PartialFunction[Int, Unit] = {
        case i: Int => println(s"$i")
      }

      f3 onSuccess printResOfInt
      f4 onSuccess printResOfInt
    }

  }
}
