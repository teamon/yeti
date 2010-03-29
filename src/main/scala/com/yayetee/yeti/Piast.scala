package com.yayetee.yeti

import scala.swing._
import event.ButtonClicked
import javax.swing.border.TitledBorder

object Piast extends AppFactory { def apply(port: String) = new Piast(port) }

class Piast(port: String) extends App(port){
	def title = "Piast"
	
	val axes = new Axis("X axis") :: new Axis("Y axis") :: new Axis("Z axis") :: Nil

	object send {
		lazy val button = new Button {
			text = "Send"
		}

		lazy val textField = new TextField {
			preferredSize = (200, 30)
		}

		def gui = new BoxPanel(Orientation.Vertical) {
			border = new TitledBorder("Send")

//			contents += new BoxPanel {
				contents += textField
//			}

			contents += button

			listenTo(button)

			reactions += {
				case ButtonClicked(`button`) =>
					writer ! SerialMessage.Plain(textField.text)
			}
		}
	}



	def gui = new BoxPanel(Orientation.Horizontal) {
		axes foreach {contents += _.panel}
		contents += send.gui
	}

	def parse(msg: Any) = msg match {
		case SerialMessage.Joystick(id, value) =>
			axes(id).value_=(value)
		case _ =>
	}
}

class Axis(title0: String) extends YetiSlider(title0)
