
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/** Context Queries (Formerly known as Implicit Function Types):
  * - http://dotty.epfl.ch/docs/reference/contextual/implicit-function-types.html,
  * - https://www.scala-lang.org/blog/2016/12/07/implicit-function-types.html */
object ContextQueries1 extends App {
  object context {
    // type alias Contextual
    type Contextual[T] = (given ExecutionContext) => T

    // sum is expanded to sum(x, y)(ctx)
    def asyncSum(x: Int, y: Int): Contextual[Future[Int]] = Future(x + y)

    def asyncMult(x: Int, y: Int)
                 (given ctx: ExecutionContext) = Future(x * y)
  }

  object parse {
    type Parseable[T] = (given ImpliedInstances.StringParser[T]) => Try[T]

    def sumStrings(x: String, y: String): Parseable[Int] = {
      val parser = implicitly[ImpliedInstances.StringParser[Int]]
      val tryA = parser.parse(x)
      val tryB = parser.parse(y)

      for {
        a <- tryA
        b <- tryB
      } yield a + b
    }
  }

  def test: Unit = {
    import ExecutionContext.Implicits.global

    context.asyncSum(3, 4).foreach(println)
    context.asyncMult(3, 4).foreach(println)

    println(parse.sumStrings("3", "4"))
    println(parse.sumStrings("3", "a"))
  }

  test
}
