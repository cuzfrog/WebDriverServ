package com.github.cuzfrog.webdriver


import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Extract source code of a function.
  * Created by cuz on 1/22/17.
  */
private object SourceCode {
  /**
    * Extract source code of a function.
    */
  def apply[P, R](fn: P => R): String = macro SourceCodeMacroImpl.apply_impl[P, R]
}

private object SourceCodeMacroImpl {
  def apply_impl[P, R](c: blackbox.Context)(fn: c.Expr[P => R]): c.Tree = {
    import c.universe._
    q"${showCode(q"${fn }")}"
  }
}
