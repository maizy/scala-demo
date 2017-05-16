package ru.maizy.scala_demo.demos

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2015
 * See LICENSE.txt for details.
 */
import ru.maizy.scala_demo.{Demo, Settings, demoBlock}

import scala.reflect.ClassTag


class StackableTraitDemo extends Demo {
  val name: String = "stackable_traits"

  def run(settings: Settings): Unit = {
    class Prefixer(val prefix: String)

    abstract class Resource[I, O] {
      type Input = I
      type Output = O
      def get(params: Input): Output
    }

    demoBlock("trait for resources") {

      class SimpleResource extends Resource[String, String] {
        def get(s: String): String = s"res[$s]"
      }

      class Arrity2Resource extends Resource[(String, Int), Int] {
        def get(params: (String, Int)): Int = params match {
          case (s, b) if s == "a" => b * 2
          case (s, b) => b
        }
      }


      trait Cache[I, O] extends Resource[I, O] {
        override type Input = I
        override type Output = O
        private val cache = scala.collection.mutable.Map[String, Output]()
        def computeKey(params: Input): String
        abstract override def get(params: Input): Output = {
          val key = computeKey(params)
          print(s"Get key=$key ")
          cache.get(key) match {
            case Some(o) =>
              println(s"from cache ($o)")
              o
            case None =>
              println("do direct invoke")
              val res = super.get(params)
              println(s"direct invoke returns $res")
              cache(key) = res
              res
          }
        }
      }

      trait DoublingString extends Resource[String, String] {
        override type Input = String
        override type Output = String

        abstract override def get(params: Input): Output = {
          println("double string")
          val res = super.get(params)
          s"$res-$res"
        }
      }

      trait SimpleCache extends Cache[String, String]

      class CachedResource extends SimpleResource with SimpleCache {
        override def computeKey(params: Input): String = s"cache.$params"
      }

      class CachedDoubledResource extends SimpleResource with SimpleCache with DoublingString {
        override def computeKey(params: String): String = s"cache2.$params"
      }

      class DoubledCachedResource extends SimpleResource with DoublingString with SimpleCache {
        override def computeKey(params: String): String = s"cache3.$params"
      }

      def testResource(resource: Resource[String, String]): Unit = {
        Seq(
          () => s"== Test ${resource.getClass}} ==",
          () => "get a",
          () => s"res: ${resource.get("a")}\n",
          () => "get b",
          () => s"res: ${resource.get("b")}\n",
          () => "get b",
          () => s"res: ${resource.get("b")}\n",
          () => "\n"
        ).foreach(x => println(x()))
      }

      testResource(new CachedResource)
      testResource(new CachedDoubledResource)
      testResource(new DoubledCachedResource)

    }

    // ... extends Resource[String, String]
    object ResourceWithImplicits{
      def get(s: String)(implicit p: Prefixer): String = s"[${p.prefix}] s: $s"
    }

    class WrappedResourceWithImplicits extends Resource[(String, Prefixer), String] {
      def get(params: (String, Prefixer)): String =
        params match {
          case (s, p) => ResourceWithImplicits.get(s)(p)
        }
    }

    demoBlock("ClassTag implicit convertion problem solve") {

      object CacheStub {
        def getAs[T](key: String)(implicit ct: ClassTag[T]): Option[T] = {
          None // Stub
        }
      }

      abstract class Resource2[I, O] (someDependancy: String)  {
        type Input = I
        type Output = O
        def get(params: I): Output
      }

      trait ResourceCache[I, O] extends Resource2[I, O] {

        override type Input = I
        override type Output = O

        implicit val ct: ClassTag[Output]

        def ifNone(params: Input): Output

        abstract override def get(params: Input): Output = {
          val key = "..."
          CacheStub.getAs[Output](key).getOrElse[Output](ifNone(params))
        }
      }

      class MyResourceBase(dependancy: String) extends Resource2[Int, Double](dependancy) {
        override def get(params: Int): Double = params.toDouble
      }

      class MyResourceExtended(dependancy: String)(implicit val ct: ClassTag[Double])
        extends MyResourceBase(dependancy)
        with ResourceCache[Int, Double]
      {
        override def ifNone(params: Int): Double = (params * 2).toDouble
      }

      val simple = new MyResourceBase("simple")
      println(s"simple: ${simple.get(5)}")

      val adv = new MyResourceExtended("adv")
      println(s"adv: ${adv.get(5)}")
    }
  }

}
