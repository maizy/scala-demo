package ru.maizy.scala_demo.demos

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2015
 * See LICENSE.txt for details.
 */
import ru.maizy.scala_demo.{Demo, Settings, demoBlock}


class StackableTraitDemo extends Demo {
  val name: String = "stackable_traits"

  def run(settings: Settings) {
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
        def computeKey(params: I): String
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

      def testResource(resource: Resource[String, String]) {
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

    object ResourceWithImplicits {
      def get(s: String)(implicit p: Prefixer): String = s"[${p.prefix}] s: $s"
    }

    object WrappedResourceWithImplicits extends Resource[(String, Prefixer), String] {
      def get(params: (String, Prefixer)): String =
        params match {
          case (s, p) => ResourceWithImplicits.get(s)(p)
        }
    }
  }
}
