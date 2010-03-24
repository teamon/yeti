package com.yayetee.yeti

import swing.Component

trait App {
	val gui: Component

	def title: String
}
