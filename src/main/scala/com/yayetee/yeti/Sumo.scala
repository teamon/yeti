package com.yayetee.yeti

import javax.swing.SwingConstants
import swing._
import javax.swing.border.TitledBorder

class Engine(title: String) {
	object slider extends Slider {
		max = 100
		min = -100
		value = 0
		majorTickSpacing = 100
		minorTickSpacing = 10
		paintLabels = true
		//		paintTicks = true
		snapToTicks = true
		peer.setOrientation(SwingConstants.VERTICAL)
		peer.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
	}

	object label extends Label {
		text = slider.value.toString
		//		horizontalAlignment = Alignment.Left
		peer.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
	}


	object panel extends BoxPanel(Orientation.Vertical) {
		border = new TitledBorder(title)

		contents += slider
		contents += label
	}


}

object Sumo extends App {
	val e0 = new Engine("Engine 0")
	val e1 = new Engine("Engine 1")

	def title = "Sumo"

	lazy val gui = new BoxPanel(Orientation.Horizontal) {
		List(e0, e1) foreach {contents += _.panel}
	}
}


