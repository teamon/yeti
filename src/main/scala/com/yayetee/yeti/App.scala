package com.yayetee.yeti

import swing.Component
import java.awt.Dimension


trait App {
	def gui: Component
	def title: String
	def size: Dimension
}
