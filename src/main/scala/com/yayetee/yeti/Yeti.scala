package com.yayetee.yeti

import scala.swing._
import event.ButtonClicked
import javax.swing.UIManager
import javax.swing.border.TitledBorder

object Yeti extends SwingApplication {

	val applications = Piast :: Sumo :: Nil

	def top = new MainFrame {
		title = "Yeti"

		val buttonConnect = new Button { text = "Connect" }
		val buttonRefresh = new Button { text = "Refresh" }
		val apps = new ComboBox[AppFactory](applications)
		val ports = new ComboBox[String](Serial.portList)

		contents = new BoxPanel(Orientation.Vertical){
			border = new TitledBorder("")
			contents += ports
			contents += apps
			contents += new BoxPanel(Orientation.Horizontal){
				contents += buttonConnect
				contents += buttonRefresh
			}
		}

		listenTo(buttonRefresh, buttonConnect)

		reactions += {
			case ButtonClicked(`buttonRefresh`) => ports.peer.setModel(ComboBox.newConstantModel(Serial.portList))
			case ButtonClicked(`buttonConnect`) =>
				val app = apps.selection.item(ports.selection.item)
//				val app = new Piast(ports.selection.item)
				val frame = app.frame
				frame.pack
				frame.visible = true
		}
	}

	override def startup(args: Array[String]) {
		try {
			UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel")
		} catch {
			case e => println(e)
		}

		// taken directly from SimpleSwingApplication.scala (scala 2.8.0.Beta1-RC8)
		val t = top
		t.pack
		t.visible = true
	}
}

