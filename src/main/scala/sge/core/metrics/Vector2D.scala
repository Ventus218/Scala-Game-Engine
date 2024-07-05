package sge.core.metrics

object Vector:
  type Vector = (Double, Double)

  extension (v: Vector)
    def x: Double = v._1
    def y: Double = v._2
    def setX(x: Double): Vector = (x, v.y)
    def setY(y: Double): Vector = (v.x, y)
    infix def *(scalar: Double): Vector = (v.x * scalar, v.y * scalar)
    infix def /(scalar: Double): Vector = (v.x / scalar, v.y / scalar)
    infix def +(other: Vector): Vector = (v.x + other.x, v.y + other.y)
    infix def -(other: Vector): Vector = (v.x - other.x, v.y - other.y)

  object Vector:
    def identity: Vector = (1, 1)
  object Versor:
    def up: Vector = (0, 1)
    def down: Vector = (0, -1)
    def right: Vector = (1, 0)
    def left: Vector = (-1, 0)
    def x: Vector = right
    def y: Vector = up
