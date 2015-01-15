#!/bin/sh
exec scala "$0" "$@"
!#

object HelloWorld extends App {
  println((args mkString ", "))
}

HelloWorld.main(args)
