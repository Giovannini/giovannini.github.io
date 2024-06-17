package io.github.giovannini.opaquetypes

import cats.data.NonEmptyMap
import cats.kernel.Order
import play.api.libs.json._

object OpaqueTypes extends App {

  final case class Foo(a: Int, b: NonEmptyMap[String, Int])
  object Foo {
    implicit def keyedMapReads[V](implicit
      valueReads: Reads[V]
    ): Reads[NonEmptyMap[String, V]] = {
      @annotation.tailrec
      def parse(
        fields: Seq[(String, JsValue)],
        res: Map[String, V]
      ): JsResult[Map[String, V]] = fields.headOption match {
        case Some((key, v)) =>
          (for {
            value <- valueReads.reads(v)
          } yield (res + (key -> value))) match {
            case JsSuccess(upd, _) => parse(fields.tail, upd)
            case JsError(details)  => JsError(details)
          }

        case _ => JsSuccess(res)
      }

      implicit val order: cats.kernel.Order[String] =
        cats.kernel.Order.fromOrdering[String]
      Reads[NonEmptyMap[String, V]] {
        case obj @ JsObject(_) =>
          parse(obj.fields, Map.empty) match {
            case JsSuccess(map, path) =>
              if (map.isEmpty) JsError("Expected a non empty map")
              else JsSuccess(NonEmptyMap.fromMapUnsafe(
                scala.collection.immutable.TreeMap(map.toArray:_*)
              ), path)
            case JsError(details)  => JsError(details)
          }

        case _ =>
          JsError("error.expected.jsobject")
      }
    }

    implicit def keyedMapWrites[V: Writes]: OWrites[NonEmptyMap[String, V]] = {
      OWrites[NonEmptyMap[String, V]] { m =>
        JsObject(m.toNel.toList.map {
          case (k, v) => k -> Json.toJson(v)
        })
      }
    }

    implicit val nemFormat: OFormat[NonEmptyMap[String, Int]] = {
      OFormat[NonEmptyMap[String, Int]](
        keyedMapReads[Int],
        keyedMapWrites[Int]
      )
    }
    // val format: OFormat[Foo] = Json.format[Foo]
    // 👆 No instance of play.api.libs.json.Format is available
    // for cats.data.Newtype2.Type[java.lang.String, scala.Int]
    // in the implicit scope

    val format: OFormat[Foo] = {
      import play.api.libs.functional.syntax._
      (
        (__ \ "a").format[Int] and
        (__ \ "b").format[NonEmptyMap[String, Int]]
      )(Foo.apply, unlift(Foo.unapply))
    }
  }

  val foo = {
    // Needed as `NonEmptyMap is based on `SortedMap`s.
    implicit val order: cats.kernel.Order[String] =
      Order.fromOrdering[String]
    Foo(1, NonEmptyMap.of("a" -> 1, "b" -> 2))
  }

  println(foo)
  println(Json.stringify(Foo.format.writes(foo)))
}