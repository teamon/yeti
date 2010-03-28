package com.yayetee.yeti

import scala.swing._

object Piast extends AppFactory { def apply(port: String) = new Piast(port) }

class Piast(port: String) extends App(port){
	def title = "Piast"
	
	val axes = new Axis("X axis") :: new Axis("Y axis") :: new Axis("Z axis") :: Nil

	def gui = new BoxPanel(Orientation.Horizontal) {
		axes foreach {contents += _.panel}
	}

	def parse(msg: Any) = msg match {
		case SerialMessage.Joystick(id, value) =>
			axes(id).value_=(value)
		case _ =>
	}
}

class Axis(title0: String) extends YetiSlider(title0)
