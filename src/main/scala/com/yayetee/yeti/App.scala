package com.yayetee.yeti

import scala.swing._
import scala.swing.event.WindowClosing
import scala.actors.Actor
import scala.actors.Actor._
import gnu.io.SerialPort
import javax.swing.border.TitledBorder
import java.awt.Font

abstract class AppFactory {
	def apply(port: String): App
}

abstract class App(portName: String) extends Actor {
	// connect to serial port
	val port = Serial.connection(portName)
	port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE)


	// set reader and writer actors
	val reader = new Serial.Reader(port.getInputStream, this)
	val writer = new Serial.Writer(port.getOutputStream, this)
	reader.start
	writer.start

	start

	def act {
		loop {
			receive {
				case SerialMessage.Log(msg) =>
					log("[OUTPUT] " + msg)

				case SerialMessage.Stop =>
					println("App Stop")
					reader !! SerialMessage.Stop
					writer !! SerialMessage.Stop
					port.close
					exit

				case x:Any =>
					log("[INPUT] " + x)
					parse(x)
			}
		}
	}

	def title: String

	def parse(x: Any)

	def log(s: String) {
		logTextArea.append(s + "\n")
		logTextArea.caret.position = logTextArea.peer.getDocument.getLength
	}

	lazy val logTextArea = new TextArea {
    rows = 10
		editable = false

		peer.setFont(new Font("Courier", Font.PLAIN, 10))
	}

	def app = this

	def frame = new Frame {
		title = app.title
		contents = new BoxPanel(Orientation.Vertical){
			contents += gui
			contents += new ScrollPane {
				contents = logTextArea
			}
		}

		reactions += {
			case WindowClosing(_) => app ! SerialMessage.Stop
		}
	}

	def gui: Component
}


class YetiSlider(title: String) {
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
