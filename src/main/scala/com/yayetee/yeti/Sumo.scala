package com.yayetee.yeti

import scala.swing._

object Sumo extends AppFactory { def apply(port: String) = new Sumo(port) }

class Sumo(port: String) extends App(port){
	def title = "Sumo"

	val engines = new Engine("Engine 0") :: new Engine("Engine 1") :: Nil

	def gui = new BoxPanel(Orientation.Horizontal) {
		engines foreach { contents += _.panel }
	}

	def parse(msg: Any) = msg match {
		case _ =>
	}
}

class Engine(title0: String) extends YetiSlider(title0)
