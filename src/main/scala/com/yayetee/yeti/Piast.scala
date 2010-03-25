package com.yayetee.yeti

import scala.swing._
import javax.swing.border.TitledBorder

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

	def value_=(v: Int) {
		label.text = v.toString
		slider.value = v
	}
}
