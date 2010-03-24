package com.yayetee.yeti

import javax.swing.SwingConstants
import swing._
import javax.swing.border.TitledBorder
import java.awt.{Dimension, Font}

class GroupPanel extends Panel with SequentialContainer.Wrapper {
	override lazy val peer = {
		val p = new javax.swing.JPanel with SuperMixin
		val l = new javax.swing.GroupLayout(p)
		p.setLayout(l)
		p
	}
}

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
	

	object panel extends BoxPanel(Orientation.Vertical){
		border = new TitledBorder(title)

		contents += slider
		contents += label
	}


}

object Sumo extends App {
	val engines = List(new Engine("Engine 0"), new Engine("Engine 1"))

	def title = "Sumo"

	def gui = new BoxPanel(Orientation.Horizontal) {
		engines.foreach { contents += _.panel }
	}

	def size = new Dimension(200, 200)
}


