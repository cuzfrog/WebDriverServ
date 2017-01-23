package com.github.cuzfrog.webdriver

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Created by cuz on 1/18/17.
  */
object MacrosTest {
  def printf(format: String, params: Any*): Unit = macro printf_impl
  def printf_impl(c: blackbox.Context)(format: c.Tree, params: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val q"${s_format: String}" = format
    val evals = ListBuffer[ValDef]()


    def precompute(value: Tree, tpe: Type): Ident = {
      val freshName = TermName(c.freshName("eval$"))
      evals += ValDef(Modifiers(), freshName, TypeTree(tpe), value)
      Ident(freshName)
    }

    val paramsStack = mutable.Stack[Tree](params map (_.tree): _*)

    val refs = s_format.split("(?<=%[\\w%])|(?=%[\\w%])") map {
      case "%d" => precompute(paramsStack.pop, typeOf[Int])
      case "%s" => precompute(paramsStack.pop, typeOf[String])
      case "%%" => Literal(Constant("%"))
      case part => Literal(Constant(part))
    }
    val stats = evals ++ refs.map(ref => reify(print(c.Expr[Any](ref).splice)).tree)
    c.Expr[Unit](Block(stats.toList, Literal(Constant(()))))
  }
}
