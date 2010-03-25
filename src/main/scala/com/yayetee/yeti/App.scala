package com.yayetee.yeti

import scala.swing._
import scala.swing.event.ButtonClicked
import javax.swing.UIManager
import javax.swing.border.TitledBorder
import actors.Actor

trait App {
	val title: String
	val mainGui: Component

	def gui = new BoxPanel(Orientation.Vertical) {
		contents += preferences.gui
		contents += mainGui
	}

	def log(s: String) {
		Yeti.log("[" + title + "] " + s)
	}

	val serial: SerialActor

	object preferences {
		object ports extends ComboBox[String](Serial.portList)

		val buttonConnect = new Button { text = "Connect" }
		val buttonDisconnect = new Button { text = "Disconnect"; visible = false }
		val buttonRefresh = new Button { text = "Refresh" }

		lazy val gui = new BoxPanel(Orientation.Horizontal) {
			border = new TitledBorder("Preferences")

			contents += ports
			contents += buttonConnect
			contents += buttonDisconnect
			contents += buttonRefresh

			listenTo(buttonConnect, buttonRefresh)

			reactions += {
				case ButtonClicked(`buttonConnect`) =>
					log("Connecting to " + ports.selection.item)
					serial.connect(ports.selection.item)

				case ButtonClicked(`buttonDisconnect`) =>
					serial !! SerialMessage.StopActor

				case ButtonClicked(`buttonRefresh`) =>
					ports.peer.setModel(ComboBox.newConstantModel(Serial.portList))
			}
		}
	}


}
