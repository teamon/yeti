package com.yayetee.yeti

import javax.swing.border.TitledBorder
import scala.swing._
import scala.actors.Actor
import scala.actors.Actor.loop

class Axis(title: String) {
	object slider extends Slider {
		max = 100
		min = -100
		value = 0
		majorTickSpacing = 100
		minorTickSpacing = 10
		paintLabels = true
		enabled = false
		orientation = Orientation.Vertical
		peer.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
	}

	object label extends Label {
		text = slider.value.toString
		horizontalAlignment = Alignment.Left
		peer.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
	}


	object panel extends BoxPanel(Orientation.Vertical) {
		border = new TitledBorder(title)

		contents += slider
		contents += label
	}
}

object Piast extends App {
	val x = new Axis("X axis")
	val y = new Axis("Y axis")
	val z = new Axis("Z axis")

	val title = "Piast"

	val serial = new SerialActor {
		def parse(x: Any) = x match {
			case SerialMessage.Plain(msg) => log("got: " + msg)
		}
	}

	lazy val mainGui = new BoxPanel(Orientation.Horizontal) {
		List(x, y, z) foreach {contents += _.panel}
	}



}
