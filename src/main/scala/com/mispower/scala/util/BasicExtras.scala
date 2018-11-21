package com.mispower.scala.util

object BasicExtras {

    /**
      * 三元操作符
      *
      * @param boolean
      */
    implicit class Ternary(val boolean: Boolean) {

        def ?[T](trueValue: T, falseValue: T): T = if (boolean) {
            trueValue
        } else {
            falseValue
        }
    }

}
