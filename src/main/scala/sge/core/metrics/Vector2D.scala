package sge.core.metrics

object Vector2D:
  type Vector2D = (Double, Double)

  extension (v: Vector2D)
    def x: Double = v._1
    def y: Double = v._2
    def setX(x: Double): Vector2D = (x, v.y)
    def setY(y: Double): Vector2D = (v.x, y)
    infix def *(scalar: Double): Vector2D = (v.x * scalar, v.y * scalar)
    infix def /(scalar: Double): Vector2D = (v.x / scalar, v.y / scalar)
    infix def +(other: Vector2D): Vector2D = (v.x + other.x, v.y + other.y)
    infix def -(other: Vector2D): Vector2D = (v.x - other.x, v.y - other.y)

  object Vector2D:
    def identity: Vector2D = (1, 1)
  object Versor2D:
    def up: Vector2D = (0, 1)
    def down: Vector2D = (0, -1)
    def right: Vector2D = (1, 0)
    def left: Vector2D = (-1, 0)
    def x: Vector2D = right
    def y: Vector2D = up
