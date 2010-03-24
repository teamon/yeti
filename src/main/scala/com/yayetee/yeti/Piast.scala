package com.yayetee.yeti

import javax.swing.SwingConstants
import javax.swing.border.TitledBorder
import swing.{Label, Slider, Orientation, BoxPanel}
import java.awt.Dimension
import collection.mutable.ListBuffer

class Axis(title: String) {
	object slider extends Slider {
		max = 100
		min = -100
		value = 0
		majorTickSpacing = 100
		minorTickSpacing = 10
		paintLabels = true
		enabled = false
		peer.setOrientation(SwingConstants.VERTICAL)
//		peer.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
	}

	object label extends Label {
		text = slider.value.toString
//		horizontalAlignment = Alignment.Left
//		peer.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
	}


	object panel extends BoxPanel(Orientation.Vertical){
		border = new TitledBorder(title)

		contents += slider
		contents += label
	}
}

object Piast extends App {
	val x = new Axis("X axis")
	val y = new Axis("Y axis")
	val z = new Axis("Z axis")

	val axes = new ListBuffer[Axis]()
	axes += x
	axes += y
	axes += z


	def title = "Piast"

	def gui = new BoxPanel(Orientation.Horizontal) {
		println("gui requested")
		axes foreach { contents += _.panel }
	}

	def size = new Dimension(100, 100)
}
